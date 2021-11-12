package millet373.bettercreativity;

import millet373.bettercreativity.api.AbilityHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public enum Ability {
    NO_CLIP(texture("mob_effect/invisibility"), false),
    SPEED_FLIGHT(texture("mob_effect/jump_boost"), true),
    NIGHT_VISION(texture("mob_effect/night_vision"), true),
    INSTANT_BREAK(texture("mob_effect/haste"), true),
    LONG_REACH(texture("particle/sweep_2"), 32, false),
    POWERFUL_ATTACK(texture("mob_effect/strength"), false);

    public final Identifier texture;
    public final int textureSize;
    public final boolean worksOnClient;

    private static Identifier texture(String name) {
        return new Identifier("textures/" + name + ".png");
    }

    Ability(Identifier texture, boolean worksOnClient) {
        this(texture, 18, worksOnClient);
    }
    Ability(Identifier texture, int textureSize, boolean worksOnClient) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.worksOnClient = worksOnClient;
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

    public boolean isEnabled(PlayerEntity player) {
        GameMode gameMode;
        if (player instanceof ClientPlayerEntity) {
            gameMode = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();
        } else if (player instanceof ServerPlayerEntity) {
            gameMode = ((ServerPlayerEntity) player).interactionManager.getGameMode();
        } else {
            return false;
        }
        return !gameMode.isSurvivalLike() && ((AbilityHolder) player).bc$hasAbility(this);
    }
}
