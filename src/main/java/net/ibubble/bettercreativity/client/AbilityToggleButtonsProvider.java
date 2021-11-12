package net.ibubble.bettercreativity.client;

import com.google.common.collect.Lists;
import net.ibubble.bettercreativity.Ability;
import net.ibubble.bettercreativity.BetterCreativityClient;
import net.ibubble.bettercreativity.api.AbilityHolder;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AbilityToggleButtonsProvider {
    public static List<ToggleButton> get(MinecraftClient client, int screenWidth, int inventoryY, int inventoryHeight, RenderTooltip renderTooltip) {
        assert client.player != null && client.interactionManager != null;
        List<ToggleButton> buttons = Lists.newArrayList();
        ConfigObject config = ConfigManager.getInstance().getConfig();
        AbilityHolder player = (AbilityHolder) client.player;
        int size = 16;
        List<Ability> availableAbilities = Stream.of(Ability.values()).filter(ability -> !BetterCreativityClient.isClientMode() || ability.worksOnClient).collect(Collectors.toList());
        int buttonX = screenWidth / 2 - size * availableAbilities.size() / 2;
        int buttonY = Objects.equals(config.displayPosition, "upper") ? inventoryY - size - 2 : inventoryY + inventoryHeight + 2;
        for (Ability ability : availableAbilities) {
            ToggleButton toggleButton = new ToggleButton(buttonX, buttonY, size, size, player.bc$hasAbility(ability), ability.texture, ability.textureSize, (button, value) -> {
                if (value) {
                    BetterCreativityClient.interactionManager.requestAbility(ability, () -> button.setValue(true));
                } else {
                    BetterCreativityClient.interactionManager.deleteAbility(ability, () -> button.setValue(false));
                }
                return false;
            }, (button, matrices, mouseX, mouseY) -> {
                renderTooltip.apply(matrices, new TranslatableText(ability.getTranslationKey()), mouseX, mouseY);
            });
            buttons.add(toggleButton);
            buttonX += size;
        }
        return buttons;
    }

    public interface RenderTooltip {
        void apply(MatrixStack matrices, Text tooltipText, int mouseX, int mouseY);
    }
}
