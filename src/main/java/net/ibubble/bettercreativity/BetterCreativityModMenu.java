package net.ibubble.bettercreativity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.config.ConfigManager;
import net.ibubble.bettercreativity.config.ConfigObject;
import net.ibubble.bettercreativity.config.ItemSortListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BetterCreativityModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::getConfigScreen;
    }

    private Screen getConfigScreen(Screen parent) {
        ConfigManager configManager = ConfigManager.getInstance();
        ConfigObject config = configManager.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.bettercreativity.title"))
                .setDoesConfirmSave(false);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        entryBuilder.setResetButtonKey(new TranslatableText("controls.reset"));

        ConfigCategory basics = builder.getOrCreateCategory(new TranslatableText("config.bettercreativity.basics"));
        basics.addEntry(createBehaviorSubCategory(entryBuilder, config));
        basics.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.bettercreativity.searchItemById"), config.searchItemById)
                .setDefaultValue(false)
                .setTooltip(new TranslatableText("config.bettercreativity.searchItemById.tooltip"))
                .setSaveConsumer(newValue -> config.searchItemById = newValue)
                .build()
        );

        ConfigCategory creativeTabs = builder.getOrCreateCategory(new TranslatableText("config.bettercreativity.creativeTabs"));

        for (ItemGroup group: ItemGroup.GROUPS) {
            if (group.isSpecial() || group == ItemGroup.INVENTORY) continue;
            ItemStack[] currentItemStacks = config.getItemGroupStacks(group.getName());
            creativeTabs.addEntry(new ItemSortListEntry(group, currentItemStacks == null ? null : List.of(currentItemStacks), newValue -> {
                config.setItemGroupStacks(group.getName(), newValue.toArray(new ItemStack[0]));
            }));
        }

        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);
        builder.setSavingRunnable(configManager::saveConfig);

        return builder.build();
    }

    private SubCategoryListEntry createBehaviorSubCategory(ConfigEntryBuilder entryBuilder, ConfigObject config) {
        SubCategoryBuilder behavior = entryBuilder.startSubCategory(new TranslatableText("config.bettercreativity.behavior"));
        behavior.add(entryBuilder
                .startEnumSelector(new TranslatableText("config.bettercreativity.onLeftClickSlot"), ConfigObject.CreativeSlotAction.class, config.onLeftClickSlot)
                .setDefaultValue(ConfigObject.CreativeSlotAction.DEFAULT)
                .setTooltip(new TranslatableText("config.bettercreativity.onLeftClickSlot.tooltip"))
                .setEnumNameProvider(value -> new TranslatableText("config.bettercreativity.slotAction." + value))
                .setSaveConsumer(newValue -> config.onLeftClickSlot = newValue)
                .build()
        );
        behavior.add(entryBuilder
                .startEnumSelector(new TranslatableText("config.bettercreativity.onRightClickSlot"), ConfigObject.CreativeSlotAction.class, config.onRightClickSlot)
                .setDefaultValue(ConfigObject.CreativeSlotAction.DEFAULT)
                .setTooltip(new TranslatableText("config.bettercreativity.onRightClickSlot.tooltip"))
                .setEnumNameProvider(value -> new TranslatableText("config.bettercreativity.slotAction." + value))
                .setSaveConsumer(newValue -> config.onRightClickSlot = newValue)
                .build()
        );
        behavior.add(entryBuilder
                .startEnumSelector(new TranslatableText("config.bettercreativity.onShiftAndLeftClickSlot"), ConfigObject.CreativeSlotAction.class, config.onShiftAndLeftClickSlot)
                .setDefaultValue(ConfigObject.CreativeSlotAction.DEFAULT)
                .setTooltip(new TranslatableText("config.bettercreativity.onShiftAndLeftClickSlot.tooltip"))
                .setEnumNameProvider(value -> new TranslatableText("config.bettercreativity.slotAction." + value))
                .setSaveConsumer(newValue -> config.onShiftAndLeftClickSlot = newValue)
                .build()
        );
        behavior.add(entryBuilder
                .startEnumSelector(new TranslatableText("config.bettercreativity.onShiftAndRightClickSlot"), ConfigObject.CreativeSlotAction.class, config.onShiftAndRightClickSlot)
                .setDefaultValue(ConfigObject.CreativeSlotAction.DEFAULT)
                .setTooltip(new TranslatableText("config.bettercreativity.onShiftAndRightClickSlot.tooltip"))
                .setEnumNameProvider(value -> new TranslatableText("config.bettercreativity.slotAction." + value))
                .setSaveConsumer(newValue -> config.onShiftAndRightClickSlot = newValue)
                .build()
        );
        behavior.add(entryBuilder
                .startBooleanToggle(new TranslatableText("config.bettercreativity.searchOnInputWithShift"), config.searchOnInputWithShift)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.searchOnInputWithShift = newValue)
                .build()
        );
        return behavior.build();
    }
}
