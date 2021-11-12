package net.ibubble.bettercreativity.mixin.client;

import net.ibubble.bettercreativity.Ability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.network.ClientPlayerEntity.isSpectator()Z"))
    private boolean setupInnerTerrain(ClientPlayerEntity player) {
        if (Ability.NO_CLIP.isEnabled(player)) {
            return player.getAbilities().flying;
        }
        return player.isSpectator();
    }
}
