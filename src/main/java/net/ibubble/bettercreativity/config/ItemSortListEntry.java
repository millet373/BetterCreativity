package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemSortListEntry extends TooltipListEntry<List<ItemStack>> {
    protected Supplier<List<ItemStack>> defaultValue;
    protected Consumer<List<ItemStack>> saveConsumer;
    protected List<ItemStack> value;
    protected List<ItemStack> originalValue;
    private final ButtonWidget resetButton, editButton;
    private final List<?> widgets;

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public ItemSortListEntry(ItemGroup itemGroup, List<ItemStack> value, Consumer<List<ItemStack>> saveConsumer) {
        super(itemGroup.getTranslationKey(), null, false);
        DefaultedList<ItemStack> stacks = DefaultedList.of();
        itemGroup.appendStacks(stacks);
        defaultValue = () -> stacks;
        this.saveConsumer = saveConsumer;
        this.value = value != null ? value : (getDefaultValue().isPresent() ? List.copyOf(getDefaultValue().get()) : List.of());
        this.originalValue = List.copyOf(this.value);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text resetText = new TranslatableText("controls.reset");
        resetButton = new ButtonWidget(0, 0, textRenderer.getWidth(resetText) + 6, 20, resetText, button -> {
            this.value = this.getDefaultValue().orElse(List.of());
        });
        editButton = new ButtonWidget(0, 0, 120, 20, new LiteralText("Edit"), button -> {
            ItemSortScreen screen = new ItemSortScreen(getConfigScreen(), itemGroup.getTranslationKey(), this.value, newValue -> this.value = newValue);
            MinecraftClient.getInstance().setScreen(screen);
        });

        widgets = Lists.newArrayList(resetButton, editButton);
    }

    @Override
    public int getItemHeight() {
        return 24;
    }

    @Override
    public List<ItemStack> getValue() {
        return value;
    }

    @Override
    public Optional<List<ItemStack>> getDefaultValue() {
        if (defaultValue == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(defaultValue.get());
        }
    }

    @Override
    public boolean isEdited() {
        List<ItemStack> value = getValue();
        if (originalValue.size() != value.size()) return true;
        for (int i = 0; i < originalValue.size(); i++) {
            if (!ItemStack.areEqual(originalValue.get(i), value.get(i))) return true;
        }
        return false;
    }

    @Override
    public void save() {
        if (saveConsumer != null) {
            saveConsumer.accept(getValue());
        }
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);

        Window window = MinecraftClient.getInstance().getWindow();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        resetButton.y = y;
        resetButton.active = isEdited();
        editButton.y = y;

        Text displayedFieldName = getDisplayedFieldName();
        if (textRenderer.isRightToLeft()) {
            textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - textRenderer.getWidth(displayedFieldName), y + 6, getPreferredTextColor());
            resetButton.x = x;
            editButton.x = x + resetButton.getWidth() + 6;
        } else {
            textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), x, y + 6, getPreferredTextColor());
            resetButton.x = x + entryWidth - resetButton.getWidth();
            editButton.x = resetButton.x - 6 - editButton.getWidth();
        }
        resetButton.render(matrices, mouseX, mouseY, delta);
        editButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> narratables() {
        return (List<Selectable>) widgets;
    }

    @Override
    public List<? extends Element> children() {
        return (List<Element>) widgets;
    }
}
