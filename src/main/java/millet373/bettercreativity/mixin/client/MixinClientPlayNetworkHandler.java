package millet373.bettercreativity.mixin.client;

import millet373.bettercreativity.Ability;
import millet373.bettercreativity.api.AbilityHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    private Set<Ability> abilities;

    @Shadow @Final
    MinecraftClient client;

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
    private void storeAbility(CallbackInfo ci) {
        assert client.player != null;
        abilities = ((AbilityHolder) client.player).bc$getAbilities();
    }

    @Inject(method = "onPlayerRespawn", at = @At("RETURN"))
    private void keepAbility(CallbackInfo ci) {
        assert client.player != null;
        ((AbilityHolder) client.player).bc$setAbilities(abilities);
    }
}
