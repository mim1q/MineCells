package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CellForgeScreen extends HandledScreen<CellForgeScreenHandler> {
  private static final Identifier BACKGROUND_TEXTURE = MineCells.createId("textures/gui/container/cell_forge.png");
  public CellForgeScreen(CellForgeScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
    this.backgroundWidth = 195;
    this.backgroundHeight = 201;
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    this.fillGradient(matrices, 0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
  }

  @Override
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    CellForgeRecipe recipe = this.getScreenHandler().getSelectedRecipe();
    if (recipe != null) {
      drawInput(recipe, matrices, 54, 87);
    }
  }

  protected void drawInput(CellForgeRecipe recipe, MatrixStack matrices, int x, int y) {
    DrawableHelper.drawCenteredText(matrices, this.textRenderer, String.valueOf(recipe.getCells()), x - 34, y - 10, 0xFFFFFF);
    for (int i = 0; i < recipe.getInput().size(); i++) {
      ItemStack stack = recipe.getInput().get(i);
      this.itemRenderer.renderInGui(stack, x + i * 18, y);
      this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, x + i * 18, y);

      matrices.push();
      matrices.translate(0.0D, 0.0D, 256.0D);
      this.fillGradient(matrices, x + i * 18, y, x + i * 18 + 16, y + 16, 0xA08B8B8B, 0xA08B8B8B);
      matrices.pop();
    }
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    super.render(matrices, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(matrices, mouseX, mouseY);
  }
}
