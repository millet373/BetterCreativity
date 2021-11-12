package net.ibubble.bettercreativity.mixin.client;

import net.ibubble.bettercreativity.client.AbilityToggleButtonsProvider;
import net.ibubble.bettercreativity.client.ToggleButton;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> {
    public MixinInventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        assert client != null && client.interactionManager != null;
        if (client.interactionManager.getCurrentGameMode().isSurvivalLike()) return;
        for (ToggleButton button : AbilityToggleButtonsProvider.get(client, width, y, backgroundHeight, this::renderTooltip)) {
            addDrawableChild(button);
        }
    }
}
