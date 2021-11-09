package net.ibubble.bettercreativity.api;

import net.ibubble.bettercreativity.Ability;

import java.util.Set;

public interface AbilityHolder {
    Set<Ability> bc$getAbilities();

    boolean bc$hasAbility(Ability ability);

    boolean bc$addAbility(Ability ability);

    boolean bc$removeAbility(Ability ability);

    void bc$setAbilities(Set<Ability> abilities);
}
