package net.ibubble.bettercreativity.mixin;

import net.ibubble.bettercreativity.Ability;
import net.ibubble.bettercreativity.api.AbilityHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

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

    public boolean bc$addAbility(Ability ability) {
        return abilities.add(ability);
    }

    public boolean bc$removeAbility(Ability ability) {
        return abilities.remove(ability);
    }

    public void bc$setAbilities(Set<Ability> abilities) {
        this.abilities = abilities;
    }
}
