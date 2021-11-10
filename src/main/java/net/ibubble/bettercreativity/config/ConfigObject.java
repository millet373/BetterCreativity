package net.ibubble.bettercreativity.config;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.Ability;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ConfigObject {
    private CreativeInventoryItemConfig[] creativeInventoryTabs = {};
    public boolean searchItemById = false;
    public CreativeSlotAction onLeftClickSlot = CreativeSlotAction.DEFAULT;
    public CreativeSlotAction onShiftAndLeftClickSlot = CreativeSlotAction.DEFAULT;
    public CreativeSlotAction onRightClickSlot = CreativeSlotAction.DEFAULT;
    public CreativeSlotAction onShiftAndRightClickSlot = CreativeSlotAction.DEFAULT;
    public boolean searchOnInputWithShift = false;
    public String displayPosition = "upper";
    private KeyBinding[] abilityKeyBindings = {};

    ConfigObject() {}

    private CreativeInventoryItemConfig createItemGroupConfig(String name) {
        CreativeInventoryItemConfig config = new CreativeInventoryItemConfig(name);

        int n = creativeInventoryTabs.length;
        creativeInventoryTabs = Arrays.copyOf(creativeInventoryTabs, n + 1);
        creativeInventoryTabs[n] = config;

        return config;
    }

    public ItemStack[] getItemGroupStacks(String name) {
        for (CreativeInventoryItemConfig config : creativeInventoryTabs) {
            if (Objects.equals(config.groupName, name)) {
                return config.getStacks();
            }
        }
        return null;
    }

    public void setItemGroupStacks(String name, ItemStack[] stacks) {
        CreativeInventoryItemConfig config = null;
        for (CreativeInventoryItemConfig c : creativeInventoryTabs) {
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

    private KeyBinding createAbilityKeyBinding(Ability ability) {
        KeyBinding keyBinding = new KeyBinding(ability.getTranslationKey(), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.bettercreativity");
        int n = abilityKeyBindings.length;
        abilityKeyBindings = Arrays.copyOf(abilityKeyBindings, n + 1);
        abilityKeyBindings[n] = keyBinding;
        return keyBinding;
    }

    public KeyBinding getAbilityKeyBinding(Ability ability) {
        for (KeyBinding keyBinding : abilityKeyBindings) {
            if (Objects.equals(keyBinding.getTranslationKey(), ability.getTranslationKey())) {
                return keyBinding;
            }
        }
        return createAbilityKeyBinding(ability);
    }

    public void setAbilityKeyBinding(Ability ability, InputUtil.Key key) {
        KeyBinding keyBinding = getAbilityKeyBinding(ability);
        keyBinding.setBoundKey(key);
    }

    public static class CreativeInventoryItemConfig {
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
                if (stackObject.nbt == null) continue;
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

    public static class ItemStackObject {
        String id;
        String nbt;

        public ItemStackObject(String id, String nbt) {
            this.id = id;
            this.nbt = nbt;
        }
    }

    public enum CreativeSlotAction {
        DEFAULT,
        PICKUP,
        PICKUP_STACK,
        TRANSFER,
        TRANSFER_STACK,
    }
}
