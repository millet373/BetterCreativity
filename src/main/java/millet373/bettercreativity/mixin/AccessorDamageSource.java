package millet373.bettercreativity.mixin;

import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface AccessorDamageSource {
    @Invoker("setBypassesArmor")
    DamageSource bc$invokeSetByPassesArmor();

    @Invoker("setUnblockable")
    DamageSource bc$invokeSetUnblockable();
}
