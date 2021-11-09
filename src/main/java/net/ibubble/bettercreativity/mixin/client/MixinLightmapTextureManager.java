package net.ibubble.bettercreativity.mixin.client;

import net.ibubble.bettercreativity.Ability;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
    @Shadow @Final
    MinecraftClient client;

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;gamma:D"))
    private double modifyGamma(GameOptions options) {
        assert client.player != null && client.interactionManager != null;
        if (Ability.NIGHT_VISION.isEnabled(client)) {
            return 200D;
        }
        return options.gamma;
    }
}
