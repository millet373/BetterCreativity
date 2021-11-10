package net.ibubble.bettercreativity.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.BetterCreativityClient;
import net.ibubble.bettercreativity.client.AbilityToggleButtonsProvider;
import net.ibubble.bettercreativity.client.ToggleButton;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.search.SearchManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Shadow static int selectedTab;
    @Shadow boolean ignoreTypedCharacter;
    @Shadow TextFieldWidget searchBox;

    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Shadow
    abstract boolean isCreativeInventorySlot(Slot slot);

    @Shadow
    abstract void setSelectedTab(ItemGroup itemGroup);

    @Shadow
    abstract void search();

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        assert client != null;
        int tabHeight = 28;
        for (ToggleButton button : AbilityToggleButtonsProvider.create(client, width, y - tabHeight, backgroundHeight + tabHeight * 2, this::renderTooltip)) {
            addDrawableChild(button);
        }
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

    @Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
    private void modifyClickAction(Slot slot, int slotId, int button, SlotActionType slotActionType, CallbackInfo ci) {
        assert client != null && client.player != null && client.interactionManager != null;

        if (slot == null || (slotActionType != SlotActionType.PICKUP && slotActionType != SlotActionType.QUICK_MOVE)) return;
        ItemStack cursorStack = handler.getCursorStack();
        ItemStack slotStack = slot.getStack();

        if (!isCreativeInventorySlot(slot) || !cursorStack.isEmpty() || slotStack.isEmpty()) return;
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

        ConfigObject config = ConfigManager.getInstance().getConfig();
        boolean isLeftClick = button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        boolean isShiftPressed = Screen.hasShiftDown();
        ConfigObject.CreativeSlotAction action = isLeftClick ? (isShiftPressed ? config.onShiftAndLeftClickSlot : config.onLeftClickSlot) : (isShiftPressed ? config.onShiftAndRightClickSlot : config.onRightClickSlot);
        ItemStack newStack;
        switch (action) {
            case PICKUP:
                handler.setCursorStack(slotStack.copy());
                break;
            case PICKUP_STACK:
                newStack = slotStack.copy();
                newStack.setCount(newStack.getMaxCount());
                handler.setCursorStack(newStack);
                break;
            case TRANSFER:
                transfer(slotStack.copy());
                break;
            case TRANSFER_STACK:
                newStack = slotStack.copy();
                newStack.setCount(slotStack.getMaxCount());
                transfer(newStack);
                break;
            default:
                return;
        }
        ci.cancel();
    }

    private void transfer(ItemStack stack) {
        assert client != null && client.player != null;
        PlayerScreenHandler handler = client.player.playerScreenHandler;
        for (int i = 0; i < 36; i++) {
            Slot slot = handler.getSlot(i < 9 ? i + 36 : i);
            stack = slot.insertStack(stack);
            if (stack.isEmpty()) break;
        }
        if (!stack.isEmpty()) {
            handler.getSlot(client.player.getInventory().selectedSlot + 36).setStack(stack);
        }
        handler.sendContentUpdates();
    }

    @Inject(method = "charTyped", at = @At("RETURN"), cancellable = true)
    private void searchOnCharTypeWithShift(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || ignoreTypedCharacter || selectedTab == ItemGroup.SEARCH.getIndex()) return;
        if (Screen.hasShiftDown()) {
            setSelectedTab(ItemGroup.SEARCH);
            String string = searchBox.getText();
            if (searchBox.charTyped(chr, modifiers)) {
                if (!Objects.equals(string, searchBox.getText())) {
                    search();
                }
                cir.setReturnValue(true);
            }
        }
    }
}
