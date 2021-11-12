package millet373.bettercreativity.mixin;

import millet373.bettercreativity.Ability;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    @ModifyConstant(
            method = "onPlayerInteractBlock",
            constant = @Constant(doubleValue = 64.0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "net.minecraft.server.network.ServerPlayerEntity.squaredDistanceTo(DDD)D")
            ),
            allow = 1
    )
    private double modifyReachDistance(double value) {
        if (Ability.LONG_REACH.isEnabled(player)) {
            return 300D;
        }
        return value;
    }
}
