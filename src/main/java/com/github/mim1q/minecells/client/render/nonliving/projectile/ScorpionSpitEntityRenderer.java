package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.projectile.ScorpionSpitEntity;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ScorpionSpitEntityRenderer extends EntityRenderer<ScorpionSpitEntity> {

  public static final Identifier TEXTURE = MineCells.createId("textures/entity/scorpion/spit.png");
  public static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);

  public ScorpionSpitEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public void render(ScorpionSpitEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
    matrixStack.push();
    matrixStack.translate(0.0F, 0.25F, 0.0F);
    matrixStack.scale(0.5F, 0.5F, 0.5F);
    matrixStack.multiply(this.dispatcher.getRotation());
    matrixStack.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 0xFF);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0xFF);
    matrixStack.pop();
  }

  @Override
  public Identifier getTexture(ScorpionSpitEntity entity) {
    return TEXTURE;
  }
}
