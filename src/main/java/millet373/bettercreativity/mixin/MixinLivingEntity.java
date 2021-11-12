package millet373.bettercreativity.mixin;

import millet373.bettercreativity.Ability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Redirect(method = "isPushable", at = @At(value = "INVOKE", target = "net.minecraft.entity.LivingEntity.isSpectator()Z"))
    private boolean disablePush(LivingEntity entity) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (Ability.NO_CLIP.isEnabled(player)) {
                return player.getAbilities().flying;
            }
        }
        return entity.isSpectator();
    }
}
