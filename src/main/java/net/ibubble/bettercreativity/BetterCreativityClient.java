package net.ibubble.bettercreativity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.ibubble.bettercreativity.mixin.client.AccessorMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.search.TextSearchableContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class BetterCreativityClient implements ClientModInitializer {
    private static boolean clientMode = false;
    public static final SearchManager.Key<ItemStack> SEARCH_KEY_ITEM_ID_AND_TOOLTIP = new SearchManager.Key<>();
    public static final BetterCreativityInteractionManager interactionManager = new BetterCreativityInteractionManager();

    public static boolean isClientMode() {
        return clientMode;
    }

    @Override
    public void onInitializeClient() {
        initializeSearchableContainer();
        setupPingPong();
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeSearchableContainer() {
        SearchManager searchManager = ((AccessorMinecraftClient) MinecraftClient.getInstance()).getSearchManager();
        SearchableContainer<ItemStack> searchableContainer = new TextSearchableContainer<>(itemStack -> {
            return itemStack.getTooltip(null, TooltipContext.Default.NORMAL).stream().map(text -> {
                String id = Registry.ITEM.getId(itemStack.getItem()).getPath();
                return Formatting.strip(text.getString()).trim() + id;
            }).filter(string -> !string.isEmpty());
        }, itemStack -> {
            return Stream.of(Registry.ITEM.getId(itemStack.getItem()));
        });
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (Item item : Registry.ITEM) {
            item.appendStacks(ItemGroup.SEARCH, defaultedList);
        }
        defaultedList.forEach(searchableContainer::add);
        searchManager.put(SEARCH_KEY_ITEM_ID_AND_TOOLTIP, searchableContainer);
    }

    private void setupPingPong() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            clientMode = true;
            ClientPlayNetworking.send(BetterCreativity.PING_PACKET, PacketByteBufs.empty());
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            clientMode = false;
        });
        ClientPlayNetworking.registerGlobalReceiver(BetterCreativity.PONG_PACKET, (client, handler, buf, responseSender) -> {
            BetterCreativity.LOGGER.info("Running in normal mode...");
            clientMode = false;
        });
    }
}
