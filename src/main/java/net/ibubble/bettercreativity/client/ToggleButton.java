package net.ibubble.bettercreativity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ToggleButton extends ClickableWidget {
    protected Identifier iconTexture;
    protected boolean value;
    protected ClickHandler onClick;
    protected TooltipSupplier tooltipSupplier;

    public ToggleButton(int x, int y, int width, int height, boolean value, Identifier iconTexture, ClickHandler onClick) {
        this(x, y, width, height, value, iconTexture, onClick, null);
    }

    public ToggleButton(int x, int y, int width, int height, boolean value, Identifier iconTexture, ClickHandler onClick, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, LiteralText.EMPTY);
        this.value = value;
        this.iconTexture = iconTexture;
        this.onClick = onClick;
        this.tooltipSupplier = tooltipSupplier;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (onClick != null) {
            if (onClick.apply(this, !value)) value = !value;
        }
    }

    private void renderButtonBackground(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int v = 46 + (value ? 1 : 0) * 20;
        int regionHeight = 20;
        int regionWidthHalf =  (int) ((float) regionHeight / height * width / 2);
        drawTexture(matrices, x, y, width / 2, height, 0, v, regionWidthHalf, regionHeight, 256, 256);
        drawTexture(matrices, x + width / 2, y, width / 2, height, 200 - regionWidthHalf, v, regionWidthHalf, regionHeight, 256, 256);
    }

    private void renderButtonIcon(MatrixStack matrices) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, iconTexture);
        if (value) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 0.5F);
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        setZOffset(1);
        int tw = 18, th = 18;
        int w = width - 2, h = height - 2;
        drawTexture(matrices, x + 1, y + 1, w, h, 0, 0, tw, th, tw, th);
        setZOffset(0);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (tooltipSupplier != null) {
            tooltipSupplier.renderTooltip(this, matrices, mouseX, mouseY);
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderButtonBackground(matrices);
        renderButtonIcon(matrices);
        if (isHovered()) {
            renderTooltip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        appendDefaultNarrations(builder);
    }

    @Environment(EnvType.CLIENT)
    public interface TooltipSupplier {
        void renderTooltip(ToggleButton button, MatrixStack matrices, int mouseX, int mouseY);
    }

    @FunctionalInterface
    public interface ClickHandler {
        boolean apply(ToggleButton button, boolean value);
    }
}
