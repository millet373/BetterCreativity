package net.ibubble.bettercreativity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.ibubble.bettercreativity.config.ItemSortListEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.collection.DefaultedList;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.stream.Collectors;

public class BetterCreativityModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new LiteralText("Better Creativity"))
                    .setDoesConfirmSave(false);
            ConfigCategory creativeInventory = builder.getOrCreateCategory(new LiteralText("Creative inventory"));

            for (ItemGroup group: ItemGroup.GROUPS) {
                if (group.isSpecial() || group == ItemGroup.INVENTORY) continue;
                creativeInventory.addEntry(new ItemSortListEntry(group, LogManager.getLogger()::info));
            }
            return builder.build();
        };
    }
}
