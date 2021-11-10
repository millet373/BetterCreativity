package net.ibubble.bettercreativity;

import net.ibubble.bettercreativity.api.AbilityHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public enum Ability {
    NO_CLIP(effectTex("invisibility"), false),
    NIGHT_VISION(effectTex("night_vision"), true),
    SPEED_BOOST(effectTex("speed"), false);

    public final Identifier texture;
    public final boolean client;

    private static Identifier effectTex(String name) {
        return new Identifier("textures/mob_effect/" + name + ".png");
    }

    Ability(Identifier texture, boolean client) {
        this.texture = texture;
        this.client = client;
    }

    public static Ability ordinalOf(int n) {
        for (Ability ability : values()) {
            if (ability.ordinal() == n) {
                return ability;
            }
        }
        return null;
    }

    public String getTranslationKey() {
        return "ability.bettercreativity." + name();
    }

    public boolean isEnabled(MinecraftClient client) {
        assert client.player != null && client.interactionManager != null;
        return !client.interactionManager.getCurrentGameMode().isSurvivalLike() && ((AbilityHolder) client.player).bc$hasAbility(this);
    }
}
