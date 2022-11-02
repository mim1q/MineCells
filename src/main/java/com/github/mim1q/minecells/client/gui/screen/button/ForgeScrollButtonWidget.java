package com.github.mim1q.minecells.client.gui.screen.button;

import com.github.mim1q.minecells.client.gui.screen.CellForgeScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ForgeScrollButtonWidget extends ButtonWidget {
  private final boolean up;

  public ForgeScrollButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, boolean scrollUp) {
    super(x, y, width, height, message, onPress);
    this.up = scrollUp;
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    super.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    boolean mouseOver = false;
    int v = 15;
    int u = this.up ? 210 : 195;
    if (this.active) {
      v = 0;
    }
    if (this.isMouseOver(mouseX, mouseY)) {
      v = 30;
      mouseOver = true;
    }
    RenderSystem.setShaderTexture(0, CellForgeScreen.BACKGROUND_TEXTURE);
    this.drawTexture(matrices, this.x, this.y, u, v, this.width, this.height);
    if (mouseOver) {
      this.renderTooltip(matrices, mouseX, mouseY);
    }
  }
}
