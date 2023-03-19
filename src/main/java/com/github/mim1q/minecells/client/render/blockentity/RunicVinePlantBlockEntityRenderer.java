package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;

import static com.github.mim1q.minecells.block.RunicVineBlock.TOP;

public class RunicVinePlantBlockEntityRenderer implements BlockEntityRenderer<RunicVinePlantBlockEntity> {
  private static final BlockState BLOCK = MineCellsBlocks.RUNIC_VINE_PLANT.getDefaultState();
  private static final BlockState VINE_BLOCK = MineCellsBlocks.RUNIC_VINE.getDefaultState().with(TOP, false);
  private final BakedModel model;
  private final BakedModel vineModel;
  private final BlockModelRenderer blockRenderer;

  public RunicVinePlantBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    blockRenderer =  ctx.getRenderManager().getModelRenderer();
    model = ctx.getRenderManager().getModel(BLOCK);
    vineModel = ctx.getRenderManager().getModel(VINE_BLOCK);
  }

  @Override
  public void render(RunicVinePlantBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    if (entity.getWorld() == null) return;
    matrices.push();
    var consumer = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(VINE_BLOCK));
    if (entity.getCachedState().get(RunicVinePlantBlock.ACTIVATED)) {
      blockRenderer.render(matrices.peek(), consumer, VINE_BLOCK, vineModel, 1.0F, 1.0F, 1.0F, light, overlay);
    }
    float time = entity.getWorld().getTime() + tickDelta;
    entity.wobble.update(time);
    float wobble = entity.wobble.getValue() * 0.2F;
    float vScale = 1.25F + (float) Math.sin(time * 0.2F) * 0.075F;
    float hScale = 1.25F + (float) Math.cos(time * 0.2F) * 0.05F;
    matrices.translate(0.5D, 0.0D, 0.5D);
    matrices.scale(hScale - wobble, vScale + wobble, hScale - wobble);
    matrices.translate(-0.51D, 0.0D, -0.5D);
    var consumer2 = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(BLOCK));
    blockRenderer.render(matrices.peek(), consumer2, BLOCK, model, 1.0F, 1.0F, 1.0F, light, overlay);
    matrices.pop();
  }
}
