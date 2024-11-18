package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class CellCrafterBlockEntityRenderer implements BlockEntityRenderer<CellCrafterBlockEntity> {
  private final AdvancementHintRenderer advancementHintRenderer = new AdvancementHintRenderer(
    MineCells.createId("cell_crafter"),
    null,
    0xCCEFFF,
    null
  );

  @Override
  public void render(CellCrafterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    {
      matrices.translate(0.5, 1.5, 0.5);
      var time = entity.getWorld() == null ? 0 : entity.getWorld().getTime() + tickDelta;
      advancementHintRenderer.render(matrices, vertexConsumers, time);
    }
    matrices.pop();
  }
}
