package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.client.gui.screen.button.ForgeButtonWidget;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CellForgeScreen extends HandledScreen<CellForgeScreenHandler> {
  public static final Identifier BACKGROUND_TEXTURE = MineCells.createId("textures/gui/container/cell_forge.png");

  private ForgeButtonWidget forgeButton;
  private final PlayerEntity player;

  public CellForgeScreen(CellForgeScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
    this.backgroundWidth = 195;
    this.backgroundHeight = 201;
    this.player = inventory.player;
  }

  @Override
  protected void init() {
    super.init();
    int x = (this.width - this.backgroundWidth) / 2;
    int y = (this.height - this.backgroundHeight) / 2;
    this.forgeButton = new ForgeButtonWidget(
      x + 166,
      y + 85,
      20,
      20,
      Text.empty(),
      CellForgeScreenHandler::onForgeButtonClicked,
      new ForgeButtonTooltipSupplier(),
      this.handler
    );
    this.addDrawableChild(forgeButton);
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    this.fillGradient(matrices, 0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    CellForgeRecipe recipe = this.getScreenHandler().getSelectedRecipe();
    if (recipe != null) {
      drawInput(recipe, matrices, this.x + 54, this.y + 87);
      for (Slot slot : this.handler.blueprintSlots) {
        if (this.handler.getSelectedRecipeSlotIndex() == slot.id) {
          this.drawSlotSelection(matrices, this.x + slot.x, this.y + slot.y);
        }
      }
    }
  }

  @Override
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    this.textRenderer.draw(matrices, this.title, 8.0F, 7.0F, 0xFF373737);
    this.textRenderer.draw(matrices, this.playerInventoryTitle, 8.0F, 108.0F, 0xFF373737);
  }

  private void drawSlotSelection(MatrixStack matrices, int x, int y) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    this.drawTexture(matrices, x - 2, y - 2, 225, 0, 20, 20);
  }

  protected void drawInput(CellForgeRecipe recipe, MatrixStack matrices, int x, int y) {
    int cells = recipe.getCells();
    int color = ((PlayerEntityAccessor) this.player).getCells() >= cells ? 0xFF4ABD46 : 0xFFE82828;
    DrawableHelper.drawCenteredText(matrices, this.textRenderer, String.valueOf(cells), x - 34, y - 10, color);
    for (int i = 0; i < recipe.getInput().size(); i++) {
      ItemStack stack = recipe.getInput().get(i);
      this.itemRenderer.renderInGui(stack, x + i * 18, y);
      this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, x + i * 18, y);
      RenderSystem.disableDepthTest();
      DrawableHelper.fill(matrices, x + i * 18, y, x + i * 18 + 16, y + 16, 0x80C6C6C6);
      RenderSystem.enableDepthTest();
    }
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    this.forgeButton.active = this.getScreenHandler().canForge();
    super.render(matrices, mouseX, mouseY, delta);
    this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    MineCellsClient.cellAmountHud.renderInInventory(matrices);
  }

  private final class ForgeButtonTooltipSupplier implements ButtonWidget.TooltipSupplier {
    @Override
    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
      renderTooltip(matrices, Text.of("Forge"), mouseX, mouseY);
    }
  }
}
