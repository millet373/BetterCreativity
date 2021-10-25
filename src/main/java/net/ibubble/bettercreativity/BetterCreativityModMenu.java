package net.ibubble.bettercreativity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.ibubble.bettercreativity.config.ItemSortListEntry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BetterCreativityModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        ConfigManager configManager = ConfigManager.getInstance();
        ConfigObject config = configManager.getConfig();

        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new LiteralText("Better Creativity"))
                    .setDoesConfirmSave(false);
            ConfigCategory creativeInventory = builder.getOrCreateCategory(new LiteralText("Creative inventory"));

            for (ItemGroup group: ItemGroup.GROUPS) {
                if (group.isSpecial() || group == ItemGroup.INVENTORY) continue;
                ItemStack[] currentItemStacks = config.getItemGroupStacks(group.getName());
                creativeInventory.addEntry(new ItemSortListEntry(group, currentItemStacks == null ? null : List.of(currentItemStacks), newValue -> {
                    config.setItemGroupStacks(group.getName(), newValue.toArray(new ItemStack[0]));
                }));
            }

            builder.setSavingRunnable(configManager::saveConfig);

            return builder.build();
        };
    }
}
