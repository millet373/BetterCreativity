package net.ibubble.bettercreativity.config;

import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemWidget extends DrawableHelper implements Element, Selectable {
    private static final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
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
        this.x = x;
        this.y = y;
        itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1);
        if (highlight) {
            fill(matrices, x, y, x + width, y + height, 0x80ffffff);
        }
    }

    public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, boolean highlight, int zOffset) {
        itemRenderer.zOffset = (float)zOffset;
        this.x = x;
        this.y = y;
        itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1);
        if (highlight) {
            fill(matrices, x, y, x + width, y + height, 0x80ffffff);
        }
        itemRenderer.zOffset = 0F;
    }

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
