package com.github.mim1q.minecells.client.gui.screen.button;

import com.github.mim1q.minecells.client.gui.screen.CellForgeScreen;
import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ForgeButtonWidget extends ButtonWidget {
  public final CellForgeScreenHandler handler;
  public ForgeButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier, CellForgeScreenHandler handler) {
    super(x, y, width, height, message, onPress, tooltipSupplier);
    this.handler = handler;
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {

  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    boolean mouseOver = false;
    int v = 65;
    if (this.active) {
      v = 45;
    }
    if (this.isMouseOver(mouseX, mouseY)) {
      v = 85;
      mouseOver = true;
    }
    RenderSystem.setShaderTexture(0, CellForgeScreen.BACKGROUND_TEXTURE);
    this.drawTexture(matrices, this.x, this.y, 195, v, this.width, this.height);
    if (mouseOver) {
      this.renderTooltip(matrices, mouseX, mouseY);
    }
  }
}
