package net.ibubble.bettercreativity.config;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ItemSortScreen extends Screen {
    private static final Identifier BG_TEX = new Identifier("minecraft", "textures/block/iron_block.png");
    private static final DefaultedList<ItemStack> allItemStacks = DefaultedList.of();

    protected final Screen parent;
    protected final Consumer<List<ItemStack>> onOK;

    private final List<ItemStack> itemStacks;
    private ItemListWidget selectedList, availableItemList;

    private final CursorItemManager cursorItemManager;

    protected ItemSortScreen(Screen parent, Text title, List<ItemStack> items, Consumer<List<ItemStack>> onOK) {
        super(title);
        this.parent = parent;
        this.onOK = onOK;
        this.itemStacks = items;
        cursorItemManager = CursorItemManager.getInstance();
    }

    @Override
    protected void init() {
        super.init();

        int scrollBarWidth = 8;
        int itemWidth = ItemWidget.width;
        int itemHeight = ItemWidget.height;
        int listWidth = itemWidth * 9 + scrollBarWidth;

        Text selectedListTitle = new LiteralText("Items for tab:").append(title).formatted(Formatting.WHITE);
        selectedList = new ItemListWidget(client, listWidth, height, 32, height - 32, itemHeight, selectedListTitle, true);
//        selectedList.setBackgroundTexture(BG_TEX);
        selectedList.setLeftPos(width / 4 - listWidth / 2);
        selectedList.setItems(itemStacks, 9);

//        List<ItemStack> unselected = allItemStacks.stream().filter(itemStack1 -> itemStacks.stream().noneMatch(itemStack2 -> ItemStack.areEqual(itemStack1, itemStack2))).collect(Collectors.toList());
        int cols = (width / 2 - 10 - scrollBarWidth) / itemWidth;
        listWidth = itemWidth * cols + scrollBarWidth;
        availableItemList = new ItemListWidget(client, listWidth, height, 32, height - 32, itemHeight, new LiteralText("All available items"), false);
//        availableItemList.setBackgroundTexture(BG_TEX);
        availableItemList.setLeftPos(width / 4 * 3 - listWidth / 2);
        availableItemList.setItems(allItemStacks, cols);

        int buttonWidth = Math.min(200, (width - 50 - 12) / 3);
        ButtonWidget cancelButton = new ButtonWidget(width / 2 - buttonWidth - 3, height - 26, buttonWidth, 20, new TranslatableText("gui.cancel"), button -> close(true));
        ButtonWidget okButton = new ButtonWidget(width / 2 + 3, height - 26, buttonWidth, 20, new LiteralText("OK"), button -> close(false));

        addSelectableChild(selectedList);
        addSelectableChild(availableItemList);
        addDrawableChild(okButton);
        addDrawableChild(cancelButton);
    }

    protected void close(boolean cancelled) {
        MinecraftClient.getInstance().setScreen(parent);
        CursorItemManager.discard();
        if (!cancelled) this.onOK.accept(selectedList.getItems());
    }

    public void drawBackground(MatrixStack matrices) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BG_TEX);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int c = 64;
        float f = 32.0F;
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float)this.height / f).color(c, c, c, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float)this.width / f, (float)this.height / f).color(c, c, c, 255).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float)this.width / f, 0.0F).color(c, c, c, 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, 0.0F).color(c, c, c, 255).next();
        tessellator.draw();
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);
//        drawBackground(matrices);

        selectedList.render(matrices, mouseX, mouseY, delta);
        availableItemList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, new LiteralText("Drag and drop items to edit").formatted(Formatting.GRAY), width / 2, 20, 0xFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);

        setZOffset(200);
        cursorItemManager.renderCursorStack(matrices, mouseX, mouseY, 200);
        setZOffset(0);

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
            cursorItemManager.setCursorStack(hovered.getItemStack().copy());
            cursorItemManager.deltaX = (int) (mouseX - hovered.x);
            cursorItemManager.deltaY = (int) (mouseY - hovered.y);
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
        if (cursorItemManager.hasCursorStack()) {
            cursorItemManager.setCursorStack(null);
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

    static {
        for (Item item: Registry.ITEM) {
            ItemGroup group = item.getGroup();
            if (item instanceof EnchantedBookItem) {
                for (Enchantment enchantment : Registry.ENCHANTMENT) {
//                    for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
//                        allItemStacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
//                    }
                    allItemStacks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
                }
            } else if (group == null) {
                allItemStacks.add(item.getDefaultStack());
            } else {
                item.appendStacks(group, allItemStacks);
            }
        }
    }

    public static class CursorItemManager {
        private static CursorItemManager instance = null;

        private ItemStack cursorStack;
        private ItemWidget cursorItemWidget;
        public int deltaX, deltaY;

        public static CursorItemManager getInstance() {
            if (instance == null) instance = new CursorItemManager();
            return instance;
        }

        public static void discard() {
            instance = null;
        }

        private CursorItemManager() {}

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

        public void renderCursorStack(MatrixStack matrices, int mouseX, int mouseY, int zOffset) {
            if (cursorItemWidget != null) {
                cursorItemWidget.render(matrices, mouseX - deltaX, mouseY - deltaY, mouseX, mouseY, false, zOffset);
            }
        }
    }
}
