package net.ibubble.bettercreativity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.mixin.MinecraftClientAccessor;
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
    public static final SearchManager.Key<ItemStack> SEARCH_KEY_ITEM_ID_AND_TOOLTIP = new SearchManager.Key<>();

    @Override
    public void onInitializeClient() {
        initializeSearchableContainer();
    }

    @SuppressWarnings("ConstantConditions")
    private void initializeSearchableContainer() {
        SearchManager searchManager = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getSearchManager();
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
}
