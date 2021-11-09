package net.ibubble.bettercreativity;

import net.ibubble.bettercreativity.api.AbilityHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public enum Ability {
    NO_CLIP(effectTex("invisibility"), false, () -> Text.of("NoClip")),
    NIGHT_VISION(effectTex("night_vision"), true, () -> Text.of("NightVision")),
    SPEED_BOOST(effectTex("speed"), false, () -> Text.of("SpeedBoost"));

    public static final List<Ability> VALUES = List.of(values());

    public final Identifier texture;
    public final boolean client;
    public final Supplier<Text> tooltip;

    private static Identifier effectTex(String name) {
        return new Identifier("textures/mob_effect/" + name + ".png");
    }

    Ability(Identifier texture, boolean client, Supplier<Text> tooltip) {
        this.texture = texture;
        this.client = client;
        this.tooltip = tooltip;
    }

    public static Ability ordinalOf(int n) {
        for (Ability ability : VALUES) {
            if (ability.ordinal() == n) {
                return ability;
            }
        }
        return null;
    }

    public boolean isEnabled(MinecraftClient client) {
        assert client.player != null && client.interactionManager != null;
        return !client.interactionManager.getCurrentGameMode().isSurvivalLike() && ((AbilityHolder) client.player).bc$hasAbility(this);
    }
}
