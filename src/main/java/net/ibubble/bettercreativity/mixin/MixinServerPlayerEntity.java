package net.ibubble.bettercreativity.mixin;

import net.ibubble.bettercreativity.Ability;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
    @Redirect(method = "applyMovementEffects", at = @At(value = "INVOKE", target = "net.minecraft.server.network.ServerPlayerEntity.isSpectator()Z"))
    private boolean dontApplyMovementEffects(ServerPlayerEntity player) {
        if (Ability.NO_CLIP.isEnabled(player)) {
            return player.getAbilities().flying;
        }
        return player.isSpectator();
    }
}
