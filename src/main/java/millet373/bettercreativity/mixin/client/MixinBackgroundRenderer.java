package net.ibubble.bettercreativity.mixin.client;

import net.ibubble.bettercreativity.Ability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "net.minecraft.entity.Entity.isSpectator()Z"))
    private static boolean disableFog(Entity entity) {
        if (entity instanceof PlayerEntity player) {
            if (Ability.NO_CLIP.isEnabled(player)) {
                return player.getAbilities().flying;
            }
        }
        return entity.isSpectator();
    }
}
