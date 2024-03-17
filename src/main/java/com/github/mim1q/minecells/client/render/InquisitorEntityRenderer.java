package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.InquisitorEntityModel;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InquisitorEntityRenderer extends MineCellsEntityRenderer<InquisitorEntity, InquisitorEntityModel> {

  public static final Identifier ORB_TEXTURE = MineCells.createId("textures/particle/magic_orb.png");
  public static final Identifier TEXTURE = MineCells.createId("textures/entity/inquisitor.png");
  public static final RenderLayer ORB_LAYER = RenderLayer.getEntityCutout(ORB_TEXTURE);

  public InquisitorEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new InquisitorEntityModel(ctx.getPart(MineCellsRenderers.INQUISITOR_LAYER)), 0.35F);
  }

  @Override
  public void render(InquisitorEntity entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
    super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

    if (entity.isAlive()) {
      float offset = entity.armUpProgress.getValue() * 0.3F;
      VertexConsumer vertexConsumer = bufferIn.getBuffer(ORB_LAYER);
      var forDisplay = entity.isForDisplay();
      var progress = (entity.age + partialTicks) * 0.2F;
      renderOrb(stack, vertexConsumer, entity.headYaw, progress, new Vector3f(-0.25F, 2.25F, 0.0F), forDisplay);
      renderOrb(stack, vertexConsumer, entity.bodyYaw, progress, new Vector3f(0.6F, 1.0F + offset, 0.4F + offset), forDisplay);
      renderOrb(stack, vertexConsumer, entity.bodyYaw, progress, new Vector3f(0.6F, 1.0F + offset, -0.4F - offset), forDisplay);
    }
  }

  public void renderOrb(MatrixStack matrixStack, VertexConsumer vertexConsumer, float yaw, float age, Vector3f offset, boolean forDisplay) {
    matrixStack.push();
    offset = MathUtils.vectorRotateY(offset, yaw * MathHelper.PI / 180.0F);
    matrixStack.translate(offset.x(), offset.y() + Math.sin(age) * 0.1F, offset.z());
    matrixStack.scale(0.375F, 0.375F, 0.375F);
    if (forDisplay) {
      matrixStack.multiply(new Quaternionf().rotationZ(MathUtils.radians(1F)));
      matrixStack.multiply(new Quaternionf().rotationY(MathUtils.radians(30F)));
    } else {
      matrixStack.multiply(this.dispatcher.getRotation());
      matrixStack.multiply(new Quaternionf().rotationY(MathUtils.radians(180F)));
    }
    MatrixStack.Entry entry = matrixStack.peek();
    Matrix4f matrix4f = entry.getPositionMatrix();
    Matrix3f matrix3f = entry.getNormalMatrix();
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF000F0, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF000F0, 0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF000F0, 0.5F, 0.5F, 0.0F, 1.0F, 0.0F, 255);
    RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, 0xF000F0, -0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 255);
    matrixStack.pop();
  }

  @Override
  public Identifier getTexture(InquisitorEntity entity) {
    return TEXTURE;
  }
}
