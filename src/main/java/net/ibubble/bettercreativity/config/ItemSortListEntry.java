package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemSortListEntry extends TooltipListEntry<List<ItemStack>> {
    protected Supplier<List<ItemStack>> defaultValue;
    protected Consumer<List<ItemStack>> saveConsumer;
    protected List<ItemStack> value;
    private final ButtonWidget editButton;
    private final List<Object> widgets;

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public ItemSortListEntry(ItemGroup itemGroup, Consumer<List<ItemStack>> saveConsumer) {
        super(itemGroup.getTranslationKey(), null, false);
        DefaultedList<ItemStack> stacks = DefaultedList.of();
        itemGroup.appendStacks(stacks);
        defaultValue = () -> stacks;
        this.saveConsumer = saveConsumer;
        this.value = getDefaultValue().isPresent() ? List.copyOf(getDefaultValue().get()) : List.of();

        editButton = new ButtonWidget(0, 0, 120, 20, new LiteralText("Edit"), widget -> {
            MinecraftClient.getInstance().setScreen(new ItemSortScreen(getConfigScreen(), getDisplayedFieldName(), value));
        });
        widgets = Lists.newArrayList(editButton);
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
//    public ItemSortListEntry(Text fieldName, Supplier<Optional<Text[]>> tooltipSupplier, Supplier<List<Item>> defaultValue, Consumer<List<Item>> saveConsumer) {
//        super(fieldName, tooltipSupplier, false);
//        this.defaultValue = defaultValue;
//        this.saveConsumer = saveConsumer;
//        this.value = getDefaultValue().isPresent() ? List.copyOf(getDefaultValue().get()) : List.of();
//
//        editButton = new ButtonWidget(0, 0, 120, 20, new LiteralText("Edit"), widget -> {
//            MinecraftClient.getInstance().setScreen(new ItemSortScreen(getConfigScreen(), getDisplayedFieldName(), value));
//        });
//        widgets = Lists.newArrayList(editButton);
//    }

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

        editButton.y = y;
        Text displayedFieldName = getDisplayedFieldName();
        if (textRenderer.isRightToLeft()) {
            textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - textRenderer.getWidth(displayedFieldName), y + 6, getPreferredTextColor());
            editButton.x = x;
        } else {
            textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), x, y + 6, getPreferredTextColor());
            editButton.x = x + entryWidth - editButton.getWidth();
        }
        editButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> narratables() {
        return (List<Selectable>) (List) widgets;
    }

    @Override
    public List<? extends Element> children() {
        return (List<Element>) (List) widgets;
    }

    public class LabelWidget implements Selectable, Element {
        public final Rectangle rectangle = new Rectangle();
        private boolean isHovered;

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            LogManager.getLogger().info("click");
            if (rectangle.contains(mouseX, mouseY)) {
                return isHovered = true;
            }
            return isHovered = false;
        }

        @Override
        public SelectionType getType() {
            return isHovered ? SelectionType.HOVERED : SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            builder.put(NarrationPart.TITLE, getFieldName());
        }
    }
}
