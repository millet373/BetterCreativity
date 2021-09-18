package net.ibubble.bettercreativity.config;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ItemSortScreen extends Screen {
    private static final Identifier BG_TEX = new Identifier("minecraft", "textures/block/oak_planks.png");
    private static final DefaultedList<ItemStack> allItemStacks = DefaultedList.of();

    protected final Screen parent;
    private final List<ItemStack> itemStacks;
    private ItemListWidget selectedList, paletteList;
    private ButtonWidget okButton;
    private ButtonWidget cancelButton;

    private final Manager manager;
    private boolean isEdited;
//    private ItemStack cursorStack;
//    private ItemWidget cursorItemWidget;

    protected ItemSortScreen(Screen parent, Text title, List<ItemStack> items) {
        super(title);
        this.parent = parent;
        this.itemStacks = items;
        manager = Manager.getInstance();
    }

    @Override
    protected void init() {
        super.init();

        int scrollBarWidth = 8;
        int itemWidth = ItemWidget.width;
        int itemHeight = ItemWidget.height;
        int listWidth = itemWidth * 9 + scrollBarWidth;
        selectedList = new ItemListWidget(client, listWidth, height, 32, height - 32, itemHeight, true);
        selectedList.setLeftPos(width / 4 - listWidth / 2);
        selectedList.setItems(itemStacks, 9);

//        List<ItemStack> unselected = Registry.ITEM.getEntries().stream().map(Map.Entry::getValue).filter(item -> !items.contains(item)).collect(Collectors.toList());
        List<ItemStack> unselected = allItemStacks.stream().filter(itemStack -> itemStacks.stream().noneMatch(stack -> ItemStack.areEqual(stack, itemStack))).collect(Collectors.toList());
        int cols = (width / 2 - 10 - scrollBarWidth) / itemWidth;
        listWidth = itemWidth * cols + scrollBarWidth;
        paletteList = new ItemListWidget(client, listWidth, height, 32, height - 32, itemHeight, false);
        paletteList.setLeftPos(width / 4 * 3 - listWidth / 2);
        paletteList.setItems(unselected, cols);

        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        okButton = new ButtonWidget(width / 2 + 3, height - 26, buttonWidths, 20, new LiteralText("OK"), button -> close(false));
        cancelButton = new ButtonWidget(width / 2 - buttonWidths - 3, height - 26, buttonWidths, 20, isEdited() ? new TranslatableText("text.cloth-config.cancel_discard") : new TranslatableText("gui.cancel"), button -> close(true));

        addSelectableChild(selectedList);
        addSelectableChild(paletteList);
        addDrawableChild(okButton);
        addDrawableChild(cancelButton);
    }

    protected void close(boolean cancelled) {
        LogManager.getLogger().info("close {}", cancelled);
        MinecraftClient.getInstance().setScreen(parent);
        Manager.discard();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        renderBackground(matrices);
//        drawBackground(matrices, new Rectangle(0, 0, width, height), 64);
        selectedList.render(matrices, mouseX, mouseY, delta);
        paletteList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 12, -1);

        super.render(matrices, mouseX, mouseY, delta);

        manager.renderCursorStack(matrices, mouseX, mouseY);

        if (!this.isDragging()) {
            ItemWidget hoveredItemWidget = getHoveredItemWidget(mouseX, mouseY);
            if (hoveredItemWidget != null) {
                ItemStack itemStack = hoveredItemWidget.getItemStack();
                renderTooltip(matrices, itemStack.getTooltip(MinecraftClient.getInstance().player, TooltipContext.Default.ADVANCED), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = false;
        ItemWidget hovered = getHoveredItemWidget((int) mouseX, (int) mouseY);
        for (Element element : children()) {
            handled = handled || element.mouseClicked(mouseX, mouseY, button);
        }
        if (button == 0 && hovered != null) {
            manager.setCursorStack(hovered.getItemStack().copy());
            manager.deltaX = (int) (mouseX - hovered.x);
            manager.deltaY = (int) (mouseY - hovered.y);
            setDragging(true);
            setFocused(hovered);
            return true;
        }
        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean handled = false;
        for (Element element : children()) {
            handled = handled || element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = false;
        for (Element element : children()) {
            handled = handled || element.mouseReleased(mouseX, mouseY, button);
        }
        setDragging(false);
        if (manager.hasCursorStack()) {
            manager.setCursorStack(null);
            return true;
        }
        return handled;
    }

    private ItemWidget getHoveredItemWidget(int mouseX, int mouseY) {
        AtomicReference<ItemWidget> hoveredItemWidget = new AtomicReference<>();
        hoveredElement(mouseX, mouseY).ifPresent(element1 -> {
            if (element1 instanceof ItemListWidget) {
                ((ItemListWidget) element1).hoveredElement(mouseX, mouseY).ifPresent(element2 -> {
                    if (element2 instanceof ItemListWidget.ItemRowEntry) {
                        ((ItemListWidget.ItemRowEntry) element2).hoveredElement(mouseX, mouseY).ifPresent(element3 -> {
                            if (element3 instanceof ItemWidget) {
                                hoveredItemWidget.set((ItemWidget) element3);
                            }
                        });
                    }
                });
            }
        });
        return hoveredItemWidget.get();
    }

    public boolean isEdited() {
        return isEdited;
    }

    private void drawBackground(MatrixStack matrices, Rectangle rect, int c) {
        Matrix4f matrix = matrices.peek().getModel();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BG_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, rect.getMinX(), rect.getMaxY(), 0.0F).texture(rect.getMinX() / 32.0F, rect.getMaxY() / 32.0F).color(c, c, c, 255).next();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMaxY(), 0.0F).texture(rect.getMaxX() / 32.0F, rect.getMaxY() / 32.0F).color(c, c, c, 255).next();
        buffer.vertex(matrix, rect.getMaxX(), rect.getMinY(), 0.0F).texture(rect.getMaxX() / 32.0F, rect.getMinY() / 32.0F).color(c, c, c, 255).next();
        buffer.vertex(matrix, rect.getMinX(), rect.getMinY(), 0.0F).texture(rect.getMinX() / 32.0F, rect.getMinY() / 32.0F).color(c, c, c, 255).next();
        tessellator.draw();
    }

    static {
        for (Item item: Registry.ITEM) {
            ItemGroup group = item.getGroup();
            if (item instanceof EnchantedBookItem) {
                for (Enchantment enchantment : Registry.ENCHANTMENT) {
                    for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
                        allItemStacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
                    }
                }
            } else if (group == null) {
                allItemStacks.add(item.getDefaultStack());
            } else {
                item.appendStacks(group, allItemStacks);
            }
        }
    }

    public static class Manager {
        private static Manager instance = null;

        private ItemSortScreen itemSortScreen;
        private ItemStack cursorStack;
        private ItemWidget cursorItemWidget;
        public int deltaX, deltaY;

        public static Manager getInstance() {
            if (instance == null) instance = new Manager();
            return instance;
        }

        public static void discard() {
            instance = null;
        }

        private Manager() {}

        public void setItemSortScreen(ItemSortScreen itemSortScreen) {
            this.itemSortScreen = itemSortScreen;
        }

        public ItemSortScreen getItemSortScreen() {
            return itemSortScreen;
        }

        public ItemStack getCursorStack() {
            return cursorStack;
        }

        public void setCursorStack(ItemStack cursorStack) {
            this.cursorStack = cursorStack;
            cursorItemWidget = cursorStack == null ? null : new ItemWidget(cursorStack);
        }

        public boolean hasCursorStack() {
            return cursorStack != null;
        }

        public void renderCursorStack(MatrixStack matrices, int mouseX, int mouseY) {
            if (cursorItemWidget != null) {
                cursorItemWidget.render(matrices, mouseX - deltaX, mouseY - deltaY, mouseX, mouseY, false);
            }
        }
    }
}
