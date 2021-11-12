package millet373.bettercreativity.config;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemListWidget extends ElementListWidget<ItemListWidget.ItemRowEntry> {
    private final ItemSortScreen.CursorItemManager cursorItemManager = ItemSortScreen.CursorItemManager.getInstance();

    private final boolean modifiable;
    private ArrayList<ItemStack> items, displayedItems;
    private int cols;
    private double scrollDelta = 0;
    private ItemWidget previousHoveredItemWidget;

    public ItemListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight, boolean modifiable) {
        super(minecraftClient, width, height, top, bottom, itemHeight);

        this.modifiable = modifiable;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemStack> items, int cols) {
        this.cols = cols;
        this.items = items;
        displayedItems = items;
        updateEntries(items);
    }

    public void addItem(ItemStack stack) {
        items.add(stack);
        updateEntries(items);
    }

    public void updateEntries(List<ItemStack> items) {
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
                displayedItems = new ArrayList<>(items.size() + 1);
                displayedItems.addAll(items.subList(0, hoveredIndex));
                displayedItems.add(ItemStack.EMPTY);
                displayedItems.addAll(items.subList(hoveredIndex + 1, items.size()));
                updateEntries(displayedItems);

                items = Lists.newArrayList(displayedItems);
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
                ItemWidget hovered = getHoveredItemWidget(mouseX, mouseY);
                boolean bl = hovered != null && previousHoveredItemWidget != null;
                if (hovered == previousHoveredItemWidget || (bl && hovered.col == previousHoveredItemWidget.col && hovered.row == previousHoveredItemWidget.row)) {
                    previousHoveredItemWidget = hovered;
                    return false;
                }

                if (hovered != null) {
                    int hoveredIndex = hovered.row * cols + hovered.col;
                    displayedItems = new ArrayList<>(items.size() + 1);
                    displayedItems.addAll(items.subList(0, hoveredIndex));
                    displayedItems.add(ItemStack.EMPTY);
                    displayedItems.addAll(items.subList(hoveredIndex, items.size()));
                } else {
                    displayedItems = new ArrayList<>(items.size() + 1);
                    displayedItems.addAll(items);
                    displayedItems.add(ItemStack.EMPTY);
                }
                updateEntries(displayedItems);
                previousHoveredItemWidget = hovered;
            } else {
                if (mouseX >= left && mouseX <= right) {
                    scrollDelta = (mouseY < top ? mouseY - top : mouseY - bottom) / 10D;
                    if (mouseY > bottom && items.size() % cols == 0) {
                        displayedItems = new ArrayList<>(items.size() + 1);
                        displayedItems.addAll(items);
                        displayedItems.add(ItemStack.EMPTY);
                        updateEntries(displayedItems);
                        return true;
                    }
                }
                if (items != displayedItems) {
                    displayedItems = items;
                    updateEntries(displayedItems);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollDelta = 0;
        if (modifiable && button == 0 && isMouseOver(mouseX, mouseY) && cursorItemManager.hasCursorStack()) {
            if (previousHoveredItemWidget != null) {
                int hoveredIndex = previousHoveredItemWidget.row * cols + previousHoveredItemWidget.col;
                displayedItems = new ArrayList<>(items.size() + 1);
                displayedItems.addAll(items.subList(0, hoveredIndex));
                displayedItems.add(cursorItemManager.getCursorStack());
                displayedItems.addAll(items.subList(hoveredIndex, items.size()));
            } else {
                displayedItems = new ArrayList<>(items.size() + 1);
                displayedItems.addAll(items);
                displayedItems.add(cursorItemManager.getCursorStack());
            }
            updateEntries(displayedItems);
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

    public ItemWidget getHoveredItemWidget(double mouseX, double mouseY) {
        ItemRowEntry rowEntry = getEntryAtPosition(mouseX, mouseY);
        if (rowEntry == null) return null;
        for (ItemWidget widget : rowEntry.widgets) {
            if (mouseX >= widget.x && mouseX < widget.x + ItemWidget.width) {
                return widget;
            }
        }
        return null;
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
