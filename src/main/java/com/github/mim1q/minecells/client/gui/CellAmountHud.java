package com.github.mim1q.minecells.client.gui;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CellAmountHud {

  public static final Identifier CELL_TEXTURE = MineCells.createId("textures/entity/cell.png");

  private final MinecraftClient client;
  private ClientPlayerEntity player;
  private int lastChange = 0;
  private int cellAmount = 0;
  private int lastAmount = 0;
  private final AnimationProperty offset = new AnimationProperty(20.0F, MathUtils::easeInOutQuad);

  public CellAmountHud(MinecraftClient client) {
    this.client = client;
  }

  public void render(MatrixStack matrixStack, float deltaTick) {
    this.player = this.client.player;

    if (this.player != null) {
      int width = this.client.getWindow().getScaledWidth();
      int height = this.client.getWindow().getScaledHeight();
      this.cellAmount = ((PlayerEntityAccessor) this.player).getCells();

      if (this.cellAmount != this.lastAmount) {
        this.lastAmount = this.cellAmount;
        this.lastChange = this.player.age;
      }

      int changeTicks = this.player.age - this.lastChange;
      if (changeTicks < 100 && this.cellAmount > 0) {
        this.offset.setupTransitionTo(20.0F, 5.0F);
      } else {
        this.offset.setupTransitionTo(0.0F, 10.0F);
      }
      this.offset.update(player.age + deltaTick);

      this.renderInfo(matrixStack, width / 2 + 96, height - (int) this.offset.getValue());
    }
  }

  public void renderInfo(MatrixStack matrixStack, int x, int y) {
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    this.client.textRenderer.drawWithShadow(
      matrixStack,
      String.valueOf(this.cellAmount),
      x + 20,
      y + 4,
      0x95D2FF
    );
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, CELL_TEXTURE);
    DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
    RenderSystem.disableBlend();
  }

  public void renderInInventory(MatrixStack matrixStack) {
    int height = this.client.getWindow().getScaledHeight();

    if (this.cellAmount > 0) {
      this.renderInfo(matrixStack, 4, height - 20);
    }
  }
}
