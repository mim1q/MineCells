package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.InquisitorEntityModel;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class InquisitorEntityRenderer extends MobEntityRenderer<InquisitorEntity, InquisitorEntityModel> {

  public static final Identifier ORB_TEXTURE = MineCells.createId("textures/particle/magic_orb.png");
  public static final Identifier TEXTURE = MineCells.createId("textures/entity/inquisitor.png");
  public static final RenderLayer ORB_LAYER = RenderLayer.getEntityCutout(ORB_TEXTURE);

  public InquisitorEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new InquisitorEntityModel(ctx.getPart(RendererRegistry.INQUISITOR_LAYER)), 0.35F);
  }

  @Override
  public void render(InquisitorEntity entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
    super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

    if (entity.isAlive()) {
      entity.targetOffset = (entity.isShootCharging() || entity.isShootReleasing()) ? 0.3F : 0.0F;
      entity.offset = MathHelper.lerp(0.01F, entity.offset, entity.targetOffset);
      VertexConsumer vertexConsumer = bufferIn.getBuffer(ORB_LAYER);
      renderOrb(stack, vertexConsumer, entity.headYaw, entity.age, new Vec3f(-0.25F, 2.25F, 0.0F));
      renderOrb(stack, vertexConsumer, entity.bodyYaw, entity.age, new Vec3f(0.6F, 1.0F + entity.offset, 0.4F + entity.offset));
      renderOrb(stack, vertexConsumer, entity.bodyYaw, entity.age, new Vec3f(0.6F, 1.0F + entity.offset, -0.4F - entity.offset));
    }
  }

  public void renderOrb(MatrixStack matrixStack, VertexConsumer vertexConsumer, float yaw, int age, Vec3f offset) {
    matrixStack.push();
    offset = MathUtils.vectorRotateY(offset, yaw * MathHelper.PI / 180.0F);
    matrixStack.translate(offset.getX(), offset.getY() + MathHelper.sin((float) age * MathHelper.PI / 45.0F) * 0.1F, offset.getZ());
    matrixStack.scale(0.375F, 0.375F, 0.375F);
    matrixStack.multiply(this.dispatcher.getRotation());
    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF0, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 255);
    matrixStack.pop();
  }

  @Override
  public Identifier getTexture(InquisitorEntity entity) {
    return TEXTURE;
  }
}
