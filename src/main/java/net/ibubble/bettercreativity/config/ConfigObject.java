package net.ibubble.bettercreativity.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Objects;

public class ConfigObject {
    private CreativeInventoryItemConfig[] creativeInventoryItemConfigs = {};

    ConfigObject() {}

    private CreativeInventoryItemConfig createItemGroupConfig(String name) {
        CreativeInventoryItemConfig config = new CreativeInventoryItemConfig(name);

        int n = creativeInventoryItemConfigs.length;
        creativeInventoryItemConfigs = Arrays.copyOf(creativeInventoryItemConfigs, n + 1);
        creativeInventoryItemConfigs[n] = config;

        return config;
    }

    public ItemStack[] getItemGroupStacks(String name) {
        for (CreativeInventoryItemConfig config : creativeInventoryItemConfigs) {
            if (Objects.equals(config.groupName, name)) {
                return config.getStacks();
            }
        }
        return null;
    }

    public void setItemGroupStacks(String name, ItemStack[] stacks) {
        CreativeInventoryItemConfig config = null;
        for (CreativeInventoryItemConfig c : creativeInventoryItemConfigs) {
            if (Objects.equals(c.groupName, name)) {
                config = c;
                break;
            }
        }
        if (config == null) {
            config = createItemGroupConfig(name);
        }
        config.setStacks(stacks);
    }

    private static class CreativeInventoryItemConfig {
        private final String groupName;
        private ItemStackObject[] stacks = {};

        CreativeInventoryItemConfig(String groupName) {
            this.groupName = groupName;
        }

        private ItemStack[] getStacks() {
            ItemStack[] stacks = new ItemStack[this.stacks.length];
            for (int i = 0; i < this.stacks.length; i++) {
                ItemStackObject stackObject = this.stacks[i];
                Item item = Registry.ITEM.get(new Identifier(stackObject.id));
                stacks[i] = new ItemStack(item, 1);
                try {
                    NbtCompound nbt = StringNbtReader.parse(stackObject.nbt);
                    stacks[i].setNbt(nbt);
                } catch(CommandSyntaxException ignored) {}
            }
            return stacks;
        }

        private void setStacks(ItemStack[] stacks) {
            this.stacks = new ItemStackObject[stacks.length];
            for (int i = 0; i < stacks.length; i++) {
                ItemStack stack = stacks[i];
                String id = Registry.ITEM.getId(stack.getItem()).toString();
                String nbt = stack.getNbt() != null ? stack.getNbt().toString() : null;
                this.stacks[i] = new ItemStackObject(id, nbt);
            }
        }
    }

    private record ItemStackObject(String id, String nbt) {}
}