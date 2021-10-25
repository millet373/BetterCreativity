package net.ibubble.bettercreativity.mixin.gui;

import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public class CreativeInventoryScreenMixin {
    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemGroup;appendStacks(Lnet/minecraft/util/collection/DefaultedList;)V"))
    private void onInvokeAppendStacks(ItemGroup group, DefaultedList<ItemStack> itemStackList) {
        ConfigObject config = ConfigManager.getInstance().getConfig();
        ItemStack[] stacks = config.getItemGroupStacks(group.getName());
        if (stacks != null) {
            itemStackList.addAll(Arrays.asList(stacks));
        } else {
            group.appendStacks(itemStackList);
        }
    }
}
