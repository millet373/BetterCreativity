package millet373.bettercreativity.mixin;

import millet373.bettercreativity.Ability;
import millet373.bettercreativity.api.AbilityHolder;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Set;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements AbilityHolder {
    private Set<Ability> abilities = EnumSet.noneOf(Ability.class);

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public Set<Ability> bc$getAbilities() {
        return abilities;
    }

    public boolean bc$hasAbility(Ability ability) {
        return abilities.contains(ability);
    }

    public void bc$addAbility(Ability ability) {
        abilities.add(ability);
    }

    public void bc$removeAbility(Ability ability) {
        abilities.remove(ability);
    }

    public void bc$setAbilities(Set<Ability> abilities) {
        this.abilities = abilities;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerEntity.updateWaterSubmersionState()Z"))
    private void onTick(CallbackInfo ci) {
        if (bc$hasAbility(Ability.NO_CLIP) && ((PlayerEntity) (Object) this).getAbilities().flying) {
            noClip = true;
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerEntity.isSpectator()Z"))
    private boolean disableCollision(PlayerEntity player) {
        if (Ability.NO_CLIP.isEnabled(player)) {
            return player.getAbilities().flying;
        }
        return player.isSpectator();
    }

    @Redirect(method = "updatePose", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerEntity.isSpectator()Z"))
    private boolean disableSwimmingWhenCollide(PlayerEntity player) {
        if (Ability.NO_CLIP.isEnabled(player)) {
            return player.getAbilities().flying;
        }
        return player.isSpectator();
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "net.minecraft.entity.player.PlayerAbilities.getFlySpeed()F"))
    private float modifyFlySpeed(PlayerAbilities abilities) {
        if (!this.world.isClient()) return abilities.getFlySpeed();

        if (Ability.SPEED_FLIGHT.isEnabled((ClientPlayerEntity) (Object) this)) {
            return abilities.getFlySpeed() * 4;
        }
        return abilities.getFlySpeed();
    }

//    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
//    private void powerfulAttack(Entity target, CallbackInfo ci) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        assert client.interactionManager != null;
//        PlayerEntity self = (PlayerEntity) (Object) this;
//        if (Ability.POWERFUL_ATTACK.isEnabled(self, client.interactionManager.getCurrentGameMode())) {
//            if (target.isAttackable() && !target.handleAttack(self)) {
//                boolean sweep = self.getMainHandStack().getItem() instanceof SwordItem;
//                float amount = target instanceof LivingEntity ? ((LivingEntity)target).getHealth() * 2 : 200F;
//                if (target instanceof EnderDragonPart) {
//                    amount = ((EnderDragonPart) target).owner.getHealth() * 2;
//                }
//                DamageSource damageSource = DamageSource.player(self);
//                ((AccessorDamageSource) damageSource).bc$invokeSetByPassesArmor();
//                ((AccessorDamageSource) damageSource).bc$invokeSetUnblockable();
//                if (target.damage(damageSource, amount)) {
//                    int sweepCount = 0;
//                    if (sweep) {
//                        for (LivingEntity livingEntity : self.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox())) {
//                            boolean bl = livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity) livingEntity).isMarker();
//                            if (livingEntity != self && livingEntity != target && self.isTeammate(livingEntity) && !bl) {
//                                target.damage(damageSource, livingEntity.getHealth() * 2);
//                                sweepCount++;
//                            }
//                        }
//                    }
//                    if (sweepCount > 0) {
//                        self.world.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, self.getSoundCategory(), 1F, 1F);
//                        self.spawnSweepAttackParticles();
//                    } else {
//                        self.world.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, self.getSoundCategory(), 1F, 1F);
//                    }
//                    self.onAttacking(target);
//                } else {
//                    self.world.playSound(null, self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, self.getSoundCategory(), 1F, 1F);
//                }
//            }
//            ci.cancel();
//        }
//    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "net.minecraft.entity.Entity.damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyAttackStrength(Entity entity, DamageSource damageSource, float f) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (Ability.POWERFUL_ATTACK.isEnabled(self) && damageSource.getSource() == self) {
            ((AccessorDamageSource) damageSource).bc$invokeSetByPassesArmor();
            ((AccessorDamageSource) damageSource).bc$invokeSetUnblockable();
            float amount = entity instanceof LivingEntity ? ((LivingEntity)entity).getHealth() * 2 : 200F;
            if (entity instanceof EnderDragonPart) {
                amount = ((EnderDragonPart) entity).owner.getHealth() * 4;
            }
            return entity.damage(damageSource, amount);
        }
        return entity.damage(damageSource, f);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "net.minecraft.entity.LivingEntity.damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean modifyAttackStrength(LivingEntity entity, DamageSource damageSource, float amount) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (Ability.POWERFUL_ATTACK.isEnabled(self) && damageSource.getSource() == self) {
            ((AccessorDamageSource) damageSource).bc$invokeSetByPassesArmor();
            ((AccessorDamageSource) damageSource).bc$invokeSetUnblockable();
            return entity.damage(damageSource, entity.getHealth() * 2);
        }
        return entity.damage(damageSource, amount);
    }
}
