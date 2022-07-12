package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class MagicOrbEntityRenderer extends EntityRenderer<MagicOrbEntity> {

  public static final Identifier TEXTURE = MineCells.createId("textures/particle/magic_orb.png");
  public static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);

  public MagicOrbEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public void render(MagicOrbEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
    matrixStack.push();
    matrixStack.translate(0.0F, 0.25F, 0.0F);
    matrixStack.scale(0.5F, 0.5F, 0.5F);
    matrixStack.multiply(this.dispatcher.getRotation());
    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
    produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 0.0F, 0, 0, 1);
    produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 1.0F, 0, 1, 1);
    produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 1.0F, 1, 1, 0);
    produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 0.0F, 1, 0, 0);
    matrixStack.pop();
  }

  public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV) {
    vertexConsumer.vertex(positionMatrix, x - 0.5F, (float) y - 0.25F, 0.0F).color(255, 255, 255, 255).texture((float) textureU, (float) textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
  }

  @Override
  public Identifier getTexture(MagicOrbEntity entity) {
    return null;
  }

}
