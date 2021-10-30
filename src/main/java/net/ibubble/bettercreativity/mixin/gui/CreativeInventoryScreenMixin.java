package net.ibubble.bettercreativity.mixin.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.BetterCreativityClient;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow TextFieldWidget searchBox;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"))
    private void overrideAppendStacks(ItemGroup group, DefaultedList<ItemStack> itemStackList) {
        ConfigObject config = ConfigManager.getInstance().getConfig();
        ItemStack[] stacks = config.getItemGroupStacks(group.getName());
        if (stacks != null) {
            itemStackList.addAll(Arrays.asList(stacks));
        } else {
            group.appendStacks(itemStackList);
        }
    }

    @ModifyArg(
            method = "search",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getSearchableContainer(Lnet/minecraft/client/search/SearchManager$Key;)Lnet/minecraft/client/search/SearchableContainer;"
            ),
            index = 0
    )
    private SearchManager.Key<ItemStack> replaceSearchableContainer(SearchManager.Key<ItemStack> key) {
        if (ConfigManager.getInstance().getConfig().searchItemById) return BetterCreativityClient.SEARCH_KEY_ITEM_ID_AND_TOOLTIP;
        return key;
    }
}
