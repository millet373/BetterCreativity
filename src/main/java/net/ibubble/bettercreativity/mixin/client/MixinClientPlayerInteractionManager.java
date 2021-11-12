package net.ibubble.bettercreativity.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.Ability;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Shadow @Final
    MinecraftClient client;
    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
    private void modifyReachDistance(CallbackInfoReturnable<Float> cir) {
        if (Ability.LONG_REACH.isEnabled(client.player)) {
            cir.setReturnValue(cir.getReturnValue() * 3);
        }
    }

    @Redirect(
            method = "attackBlock",
            at = @At(value = "FIELD", target = "net.minecraft.client.network.ClientPlayerInteractionManager.blockBreakingCooldown:I")
    )
    private void modifyBlockBreakingCooldown(ClientPlayerInteractionManager interactionManager, int value) {
        if (Ability.INSTANT_BREAK.isEnabled(client.player)) {
            blockBreakingCooldown = 0;
        } else {
            blockBreakingCooldown = value;
        }
    }

    @Redirect(
            method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD", target = "net.minecraft.client.network.ClientPlayerInteractionManager.blockBreakingCooldown:I"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "net.minecraft.world.GameMode.isCreative()Z")
            )
    )
    private void modifyBlockBreakingCooldownOnUpdate(ClientPlayerInteractionManager interactionManager, int value) {
        if (Ability.INSTANT_BREAK.isEnabled(client.player)) {
            blockBreakingCooldown = 0;
        } else {
            blockBreakingCooldown = value;
        }
    }
}
