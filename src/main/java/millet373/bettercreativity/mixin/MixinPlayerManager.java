package millet373.bettercreativity.mixin;

import millet373.bettercreativity.api.AbilityHolder;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void keepAbility(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        AbilityHolder newPlayer = (AbilityHolder) cir.getReturnValue();
        newPlayer.bc$setAbilities(((AbilityHolder) oldPlayer).bc$getAbilities());
    }
}
