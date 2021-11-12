package millet373.bettercreativity.mixin.client;

import millet373.bettercreativity.Ability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Shadow @Final
    protected MinecraftClient client;

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerAbilities.getFlySpeed()F"))
    private float modifyFlySpeed(PlayerAbilities abilities) {
        if (Ability.SPEED_FLIGHT.isEnabled((ClientPlayerEntity) (Object) this)) {
            return abilities.getFlySpeed() * 4;
        }
        return abilities.getFlySpeed();
    }

    @Redirect(method = "updateWaterSubmersionState", at = @At(value = "INVOKE", target = "net.minecraft.client.network.ClientPlayerEntity.isSpectator()Z"))
    private boolean eraseWaterInOutSound(ClientPlayerEntity player) {
        if (Ability.NO_CLIP.isEnabled(player)) {
            return player.getAbilities().flying;
        }
        return player.isSpectator();
    }
}
