package net.ibubble.bettercreativity.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ItemSortScreen extends Screen {
    private static final DefaultedList<ItemStack> allItemStacks = DefaultedList.of();

    private final Screen parent;
    private final ItemGroup itemGroup;
    private final List<ItemStack> itemStacks;
    private final Supplier<List<ItemStack>> defaultValue;
    private final Consumer<List<ItemStack>> onOK;

    private ItemListWidget selectedList, availableItemList;
    private Text selectedListTitle, availableListTitle;

    private final CursorItemManager cursorItemManager;

    protected ItemSortScreen(Screen parent, ItemGroup itemGroup, List<ItemStack> itemStacks, Supplier<List<ItemStack>> defaultValue, Consumer<List<ItemStack>> onOK) {
        super(new TranslatableText("config.bettercreativity.creativeTabs.sort.title"));
        this.parent = parent;
        this.itemGroup = itemGroup;
        this.itemStacks = itemStacks;
        this.defaultValue = defaultValue;
        this.onOK = onOK;
        cursorItemManager = CursorItemManager.getInstance();
    }

    @Override
    protected void init() {
        super.init();

        int scrollBarWidth = 8;
        int itemWidth = ItemWidget.width;
        int itemHeight = ItemWidget.height;

        int leftListWidth = itemWidth * 9 + scrollBarWidth;
        int leftListLeft = width / 4 - leftListWidth / 2;
        selectedListTitle = itemGroup.getTranslationKey();
        selectedList = new ItemListWidget(client, leftListWidth, height, 64, height - 32, itemHeight, true);
        selectedList.setLeftPos(leftListLeft);
        selectedList.setItems(itemStacks, 9);
        ButtonWidget defaultButton = new ButtonWidget(leftListLeft + leftListWidth - 64, 32, 20, 20, Text.of("D"), button -> {
            selectedList.setItems(defaultValue.get(), 9);
        }, (button, matrices, mouseX, mouseY) -> {
            if (button.isMouseOver(mouseX, mouseY)) {
                renderTooltip(matrices, Text.of("Default"), mouseX, mouseY);
            }
        });
        ButtonWidget recommendedButton = new ButtonWidget(leftListLeft + leftListWidth - 42, 32, 20, 20, Text.of("R"), button -> {
            selectedList.setItems(List.of(), 9);
        }, (button, matrices, mouseX, mouseY) -> {
            if (button.isMouseOver(mouseX, mouseY)) {
                renderTooltip(matrices, Text.of("Recommended"), mouseX, mouseY);
            }
        });
        ButtonWidget clearButton = new ButtonWidget(leftListLeft + leftListWidth - 20, 32, 20, 20, Text.of("C"), button -> {
            selectedList.setItems(List.of(), 9);
        }, (button, matrices, mouseX, mouseY) -> {
            if (button.isMouseOver(mouseX, mouseY)) {
                renderTooltip(matrices, Text.of("Clear"), mouseX, mouseY);
            }
        });

        addSelectableChild(selectedList);
        addDrawableChild(defaultButton);
        addDrawableChild(recommendedButton);
        addDrawableChild(clearButton);

//        List<ItemStack> unselected = allItemStacks.stream().filter(itemStack1 -> itemStacks.stream().noneMatch(itemStack2 -> ItemStack.areEqual(itemStack1, itemStack2))).collect(Collectors.toList());
        int cols = (width / 2 - 10 - scrollBarWidth) / itemWidth;
        int rightListWidth = itemWidth * cols + scrollBarWidth;
        availableListTitle = new TranslatableText("config.bettercreativity.creativeTabs.sort.availableListTitle");
        availableItemList = new ItemListWidget(client, rightListWidth, height, 64, height - 32, itemHeight, false);
        availableItemList.setLeftPos(width / 4 * 3 - rightListWidth / 2);
        availableItemList.setItems(allItemStacks, cols);

        addSelectableChild(availableItemList);

        int buttonWidth = Math.min(200, (width - 50 - 12) / 3);
        ButtonWidget cancelButton = new ButtonWidget(width / 2 - buttonWidth - 3, height - 26, buttonWidth, 20, new TranslatableText("gui.cancel"), button -> close(true));
        ButtonWidget okButton = new ButtonWidget(width / 2 + 3, height - 26, buttonWidth, 20, Text.of("OK"), button -> close(false));

        addDrawableChild(cancelButton);
        addDrawableChild(okButton);
    }

    protected void close(boolean cancelled) {
        MinecraftClient.getInstance().setScreen(parent);
        CursorItemManager.discard();
        if (!cancelled) this.onOK.accept(selectedList.getItems());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(0);

        selectedList.render(matrices, mouseX, mouseY, delta);
        availableItemList.render(matrices, mouseX, mouseY, delta);
        float titleY = 32F + 10F - (float) textRenderer.fontHeight / 2F;
        textRenderer.draw(matrices, selectedListTitle, selectedList.getLeft(), titleY, 0xFFFFFF);
        textRenderer.draw(matrices, availableListTitle, availableItemList.getLeft(), titleY, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, title, width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, textRenderer, new TranslatableText("config.bettercreativity.creativeTabs.sort.hint").formatted(Formatting.GRAY), width / 2, 20, 0xFFFFFF);

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
