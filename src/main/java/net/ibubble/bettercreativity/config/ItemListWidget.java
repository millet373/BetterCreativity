package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ibubble.bettercreativity.BetterCreativity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class ItemListWidget extends ElementListWidget<ItemListWidget.ItemRowEntry> {
    private final ItemSortScreen.CursorItemManager cursorItemManager = ItemSortScreen.CursorItemManager.getInstance();

    private List<ItemStack> items, displayedItems;
    private int cols;
    private final boolean modifiable;
    private double scrollDelta = 0;

    public ItemListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight, boolean modifiable) {
        super(minecraftClient, width, height, top, bottom, itemHeight);

        this.modifiable = modifiable;
    }

    public int getLeft() {
        return left;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(List<ItemStack> items, int cols) {
        this.cols = cols;
        this.items = items;
        displayedItems = items;
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
                displayedItems = Lists.newArrayList();
                displayedItems.addAll(items.subList(0, hoveredIndex));
                displayedItems.add(ItemStack.EMPTY);
                displayedItems.addAll(items.subList(hoveredIndex + 1, items.size()));
                initEntries(displayedItems);

                items = new ArrayList<>(displayedItems);
                items.remove(hoveredIndex);

                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        scrollDelta = 0;
        if (modifiable && button == 0 && cursorItemManager.hasCursorStack()) {
            if (isMouseOver(mouseX, mouseY)) {
                ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
                if (hovered != null) {
                    int hoveredIndex = hovered.row * cols + hovered.col;
                    displayedItems = Lists.newArrayList();
                    displayedItems.addAll(items.subList(0, hoveredIndex));
                    displayedItems.add(ItemStack.EMPTY);
                    displayedItems.addAll(items.subList(hoveredIndex, items.size()));
                    initEntries(displayedItems);
                    return true;
                }
            } else {
                if (mouseX >= left && mouseX <= right) {
                    scrollDelta = mouseY < top ? (mouseY - top) / 10 : (mouseY - bottom) / 10;
                }
                if (items != displayedItems) {
                    initEntries(items);
                    displayedItems = items;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollDelta = 0;
        if (modifiable && button == 0 && isMouseOver(mouseX, mouseY) && cursorItemManager.hasCursorStack()) {
            ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
            if (hovered != null) {
                int hoveredIndex = hovered.row * cols + hovered.col;
                displayedItems = Lists.newArrayList();
                displayedItems.addAll(items.subList(0, hoveredIndex));
                displayedItems.add(cursorItemManager.getCursorStack());
                displayedItems.addAll(items.subList(hoveredIndex, items.size()));
            } else {
                displayedItems = Lists.newArrayList();
                displayedItems.addAll(items);
                displayedItems.add(cursorItemManager.getCursorStack());
                BetterCreativity.LOGGER.info(displayedItems);
            }
            initEntries(displayedItems);
            items = displayedItems;
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        setScrollAmount(getScrollAmount() + scrollDelta);
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
