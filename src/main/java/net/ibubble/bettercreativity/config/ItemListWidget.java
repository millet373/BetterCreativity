package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ItemListWidget extends ElementListWidget<ItemListWidget.ItemRowEntry> {
    private final ItemSortScreen.Manager manager = ItemSortScreen.Manager.getInstance();
    private List<ItemStack> items, currentItems;
    private int cols;
    private final boolean modifiable;

    public ItemListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight, boolean modifiable) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
        this.modifiable = modifiable;
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
        if (modifiable && button == 0 && manager.hasCursorStack()) {
            if (isMouseOver(mouseX, mouseY)) {
                ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
                if (hovered != null) {
                    int hoveredIndex = hovered.row * cols + hovered.col;
                    currentItems = Lists.newArrayList();
                    currentItems.addAll(items.subList(0, hoveredIndex));
//                    currentItems.add(manager.getCursorStack());
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
        if (modifiable && button == 0 && isMouseOver(mouseX, mouseY) && manager.hasCursorStack()) {
            LogManager.getLogger().info("release");
            ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
            if (hovered != null) {
                int hoveredIndex = hovered.row * cols + hovered.col;
                currentItems = Lists.newArrayList();
                currentItems.addAll(items.subList(0, hoveredIndex));
                currentItems.add(manager.getCursorStack());
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
        private final ItemSortScreen.Manager manager = ItemSortScreen.Manager.getInstance();
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
                widget.render(matrices, x + i * ItemWidget.width, y, mouseX, mouseY, !manager.hasCursorStack() && widget.isMouseOver(mouseX, mouseY));
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
