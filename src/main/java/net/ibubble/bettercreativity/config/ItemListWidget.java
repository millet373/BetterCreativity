package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemListWidget extends ElementListWidget<ItemListWidget.ItemRowEntry> {
    private final ItemSortScreen.CursorItemManager cursorItemManager = ItemSortScreen.CursorItemManager.getInstance();

    private List<ItemStack> items, currentItems;
    private int cols;
    private final boolean modifiable;
    private final Text title;

    public ItemListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight, Text title, boolean modifiable) {
        super(minecraftClient, width, height, top, bottom, itemHeight);

        this.title = title;
        this.modifiable = modifiable;

        setRenderHeader(true, (int)(9.0F * 1.5F));
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items, int cols) {
        this.cols = cols;
        this.items = items;
        currentItems = items;
        initEntries(items);
    }

    public void initEntries(List<ItemStack> items) {
        clearEntries();
        int size = items.size();
        int rows = MathHelper.ceil((double) size / cols);
        for (int i = 0; i < rows; i++) {
            addEntry(new ItemRowEntry(i, items.subList(i * cols, Math.min((i + 1) * cols, size))));
        }
    }

    @Override
    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
        TextRenderer textRenderer = client.textRenderer;
        Text text = (new LiteralText("")).append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
        textRenderer.draw(matrices, text, (float)(x + this.width / 2 - textRenderer.getWidth(text) / 2), (float)Math.min(this.top + 3, y), 16777215);
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    protected int getScrollbarPositionX() {
        return right - 6;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (modifiable && button == 0 && isMouseOver(mouseX, mouseY)) {
            ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
            if (hovered != null) {
                int hoveredIndex = hovered.row * cols + hovered.col;
                currentItems = Lists.newArrayList();
                currentItems.addAll(items.subList(0, hoveredIndex));
                currentItems.add(ItemStack.EMPTY);
                currentItems.addAll(items.subList(hoveredIndex + 1, items.size()));
                initEntries(currentItems);

                items = new ArrayList<>(currentItems);
                items.remove(hoveredIndex);

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (modifiable && button == 0 && cursorItemManager.hasCursorStack()) {
            if (isMouseOver(mouseX, mouseY)) {
                ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
                if (hovered != null) {
                    int hoveredIndex = hovered.row * cols + hovered.col;
                    currentItems = Lists.newArrayList();
                    currentItems.addAll(items.subList(0, hoveredIndex));
                    currentItems.add(ItemStack.EMPTY);
                    currentItems.addAll(items.subList(hoveredIndex, items.size()));
                    initEntries(currentItems);
                    return true;
                }
            } else if (items != currentItems) {
                initEntries(items);
                currentItems = items;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (modifiable && button == 0 && isMouseOver(mouseX, mouseY) && cursorItemManager.hasCursorStack()) {
            ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
            if (hovered != null) {
                int hoveredIndex = hovered.row * cols + hovered.col;
                currentItems = Lists.newArrayList();
                currentItems.addAll(items.subList(0, hoveredIndex));
                currentItems.add(cursorItemManager.getCursorStack());
                currentItems.addAll(items.subList(hoveredIndex, items.size()));
                initEntries(currentItems);
                items = currentItems;
                return true;
            }
        }
        return false;
    }

    public ItemWidget getHoveredItemWidget(int mouseX, int mouseY) {
        AtomicReference<ItemWidget> hoveredItemWidget = new AtomicReference<>();
        hoveredElement(mouseX, mouseY).ifPresent(element1 -> {
            if (element1 instanceof ItemListWidget.ItemRowEntry) {
                ((ItemListWidget.ItemRowEntry) element1).hoveredElement(mouseX, mouseY).ifPresent(element2 -> {
                    if (element2 instanceof ItemWidget) {
                        hoveredItemWidget.set((ItemWidget) element2);
                    }
                });
            }
        });
        return hoveredItemWidget.get();
    }

    protected static class ItemRowEntry extends ElementListWidget.Entry<ItemRowEntry> {
        private final ItemSortScreen.CursorItemManager cursorItemManager = ItemSortScreen.CursorItemManager.getInstance();
        private final List<ItemWidget> widgets;

        private ItemRowEntry(int row, List<ItemStack> items) {
            widgets = Lists.newArrayList();
            for (int i = 0; i < items.size(); i++) {
                widgets.add(new ItemWidget(items.get(i), row, i));
            }
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            for (int i = 0; i < widgets.size(); i++) {
                ItemWidget widget = widgets.get(i);
                widget.render(matrices, x + i * ItemWidget.width, y, mouseX, mouseY, !cursorItemManager.hasCursorStack() && widget.isMouseOver(mouseX, mouseY));
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return widgets;
        }

        @Override
        public List<? extends Element> children() {
            return widgets;
        }
    }
}
