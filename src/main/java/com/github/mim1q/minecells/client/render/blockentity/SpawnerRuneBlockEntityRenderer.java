package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class SpawnerRuneBlockEntityRenderer implements BlockEntityRenderer<SpawnerRuneBlockEntity> {
  public static final Identifier TEXTURE = MineCells.createId("textures/blockentity/spawner_rune.png");

  public SpawnerRuneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) { }

  @Override
  public void render(SpawnerRuneBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    matrices.push();
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE));
    Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
    Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
    RenderUtils.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 0xFF0000, 0.0F, 0.05F, 0.0F, 0.0F, 0.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 0xFF0000, 0.0F, 0.05F, 1.0F, 0.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 0xFF0000, 1.0F, 0.05F, 1.0F, 1.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 0xFF0000, 1.0F, 0.05F, 0.0F, 1.0F, 0.0F, 255);
    matrices.pop();
  }
}
