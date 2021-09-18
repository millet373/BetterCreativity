package net.ibubble.bettercreativity.config;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;

public class ItemWidget extends DrawableHelper implements Element, Selectable {
    private static final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
//    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    public static final int width = 18;
    public static final int height = 18;
    public int x, y, row, col;
    private final ItemStack itemStack;

    public ItemWidget(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemWidget(ItemStack itemStack, int row, int col) {
        this.itemStack = itemStack;
        this.row = row;
        this.col = col;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, boolean highlight) {
        itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1);
        this.x = x;
        this.y = y;
//        itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
        if (highlight) {
            fill(matrices, x, y, x + width, y + height, 0x80ffffff);
        }
    }

//    @Override
//    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
//        LogManager.getLogger().info("dragged {} {} {} {} {}", mouseX, mouseY, button, deltaX, deltaY);
//        return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return new Rectangle(x, y, width, height).contains(mouseX, mouseY);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
