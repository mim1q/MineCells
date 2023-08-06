package com.github.mim1q.minecells.client.render.blockentity.statue;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.DecorativeStatueBlock;
import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class DecorativeStatueBlockEntityRenderer implements BlockEntityRenderer<DecorativeStatueBlockEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/statue/king.png");
  private final KingStatueModel model;

  public DecorativeStatueBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new KingStatueModel(loader.getModelPart(MineCellsRenderers.KING_STATUE_LAYER));
  }

  @Override
  public void render(DecorativeStatueBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    float rotation = entity.getCachedState().get(DecorativeStatueBlock.ROTATION) * 22.5F;
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
    matrices.push();
    matrices.scale(1.0F, -1.0F, -1.0F);
    matrices.translate(0.5F, -1.5F, -0.5F);
    matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(180.0F + rotation)));
    this.model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }
}
