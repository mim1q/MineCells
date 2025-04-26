package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.projectile.MagicOrbEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

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
    matrixStack.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
    MatrixStack.Entry entry = matrixStack.peek();
    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
    produceVertex(vertexConsumer, entry, 0xF0, 0.0F, 0, 0, 1);
    produceVertex(vertexConsumer, entry, 0xF0, 1.0F, 0, 1, 1);
    produceVertex(vertexConsumer, entry, 0xF0, 1.0F, 1, 1, 0);
    produceVertex(vertexConsumer, entry, 0xF0, 0.0F, 1, 0, 0);
    matrixStack.pop();
  }

  public static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry entry, int light, float x, int y, int textureU, int textureV) {
    vertexConsumer.vertex(entry, x - 0.5F, (float) y - 0.25F, 0.0F).color(255, 255, 255, 255).texture((float) textureU, (float) textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, 1.0F, 0.0F);
  }

  @Override
  public Identifier getTexture(MagicOrbEntity entity) {
    return null;
  }

}
