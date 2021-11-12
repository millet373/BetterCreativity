package millet373.bettercreativity.mixin;

import millet373.bettercreativity.Ability;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {
    @Shadow @Final
    ServerPlayerEntity player;

    @ModifyConstant(
            method = "processBlockBreakingAction",
            constant = @Constant(doubleValue = 36.0),
            allow = 1
    )
    private double modifyReachDistance(double value) {
        if (Ability.LONG_REACH.isEnabled(player)) {
            return 300D;
        }
        return value;
    }
}
