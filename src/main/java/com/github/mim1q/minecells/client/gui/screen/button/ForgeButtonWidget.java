package com.github.mim1q.minecells.client.gui.screen.button;

import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
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
    super.render(matrices, mouseX, mouseY, delta);
  }
}
