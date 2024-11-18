package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.util.RenderUtils;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

import java.util.List;

public record EmiBlockIngredient(
  BlockState state
) implements EmiIngredient {
  @Override
  public List<EmiStack> getEmiStacks() {
    return List.of(EmiStack.of(state.getBlock()));
  }

  @Override
  public EmiIngredient copy() {
    return new EmiBlockIngredient(state);
  }

  @Override
  public long getAmount() {
    return 0;
  }

  @Override
  public EmiIngredient setAmount(long amount) {
    return this;
  }

  @Override
  public float getChance() {
    return 1f;
  }

  @Override
  public EmiIngredient setChance(float chance) {
    return this;
  }

  @Override
  public void render(DrawContext draw, int x, int y, float delta, int flags) {
    var matrices = draw.getMatrices();
    var blockRenderer = MinecraftClient.getInstance().getBlockRenderManager();
    var model = blockRenderer.getModel(state);

    matrices.push();

    matrices.translate(x + 8, y + 8, 200);
    matrices.scale(10, -10, 10);
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30f));
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45f));

    RenderUtils.renderBakedModel(
      model,
      Random.create(),
      0xF000F0,
      matrices,
      draw.getVertexConsumers().getBuffer(RenderLayer.getCutout())
    );

    matrices.pop();
  }

  @Override
  public List<TooltipComponent> getTooltip() {
    ItemStack stack = state.getBlock().asItem().getDefaultStack();
    var tooltip = Screen.getTooltipFromItem(MinecraftClient.getInstance(), stack);
    return tooltip.stream().map(it -> TooltipComponent.of(it.asOrderedText())).toList();
  }
}
