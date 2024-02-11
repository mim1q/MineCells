package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.RunicVinePlantBlock;
import com.github.mim1q.minecells.block.blockentity.RunicVinePlantBlockEntity;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Math;

import static com.github.mim1q.minecells.block.RunicVineBlock.TOP;
import static com.github.mim1q.minecells.util.RenderUtils.getGlobalAnimationProgress;

public class RunicVinePlantBlockEntityRenderer implements BlockEntityRenderer<RunicVinePlantBlockEntity> {
  private static final BlockState BLOCK = MineCellsBlocks.RUNIC_VINE_PLANT.getDefaultState();
  private static final BlockState VINE_BLOCK = MineCellsBlocks.RUNIC_VINE.getDefaultState().with(TOP, false);
  private final BakedModel model;
  private final BakedModel vineModel;
  private final BlockModelRenderer blockRenderer;
  private final AdvancementHintRenderer hintRenderer;

  public RunicVinePlantBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    blockRenderer = ctx.getRenderManager().getModelRenderer();
    model = ctx.getRenderManager().getModel(BLOCK);
    vineModel = ctx.getRenderManager().getModel(VINE_BLOCK);

    this.hintRenderer = new AdvancementHintRenderer(
      MineCells.createId("vine_rune"),
      ctx.getItemRenderer(),
      0xFFF0FFD0,
      MineCellsItems.VINE_RUNE
    );
  }

  @Override
  public void render(RunicVinePlantBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    if (entity.getWorld() == null) return;

    float time = getGlobalAnimationProgress();

    matrices.push();
    {
      var consumer = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(VINE_BLOCK));
      if (entity.getCachedState().get(RunicVinePlantBlock.ACTIVATED)) {
        blockRenderer.render(matrices.peek(), consumer, VINE_BLOCK, vineModel, 1.0F, 1.0F, 1.0F, light, overlay);
      }
      float wobble = entity.wobble.update(time) * 0.2F;
      float vScale = 1.25F + Math.sin(time * 0.2F) * 0.075F;
      float hScale = 1.25F + Math.cos(time * 0.2F) * 0.05F;
      matrices.translate(0.5D, 0.0D, 0.5D);
      matrices.scale(hScale - wobble, vScale + wobble, hScale - wobble);
      matrices.translate(-0.51D, 0.0D, -0.5D);
      var consumer2 = vertexConsumers.getBuffer(RenderLayers.getBlockLayer(BLOCK));
      blockRenderer.render(matrices.peek(), consumer2, BLOCK, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }
    matrices.pop();

    matrices.push();
    {
      matrices.translate(0.5f, 1f, 0.5f);
      hintRenderer.render(matrices, vertexConsumers, time);
    }
    matrices.pop();
  }
}
