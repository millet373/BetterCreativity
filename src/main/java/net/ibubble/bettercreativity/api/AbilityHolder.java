package net.ibubble.bettercreativity.api;

import net.ibubble.bettercreativity.Ability;

import java.util.Set;

public interface AbilityHolder {
    Set<Ability> bc$getAbilities();

    boolean bc$hasAbility(Ability ability);

    void bc$addAbility(Ability ability);

    void bc$removeAbility(Ability ability);

    void bc$setAbilities(Set<Ability> abilities);
}
