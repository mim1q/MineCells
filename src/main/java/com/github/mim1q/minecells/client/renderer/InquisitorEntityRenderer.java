package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.InquisitorEntityModel;
import com.github.mim1q.minecells.entity.InquisitorEntity;
import com.github.mim1q.minecells.util.MineCellsMathHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class InquisitorEntityRenderer extends GeoEntityRenderer<InquisitorEntity> {

    public static final Identifier ORB_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/particle/magic_orb.png");
    public static RenderLayer ORB_LAYER = RenderLayer.getEntityShadow(ORB_TEXTURE);

    float newOffset = 0.0F;
    float currentOffset = 0.0F;

    public InquisitorEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new InquisitorEntityModel());
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(InquisitorEntity entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

        newOffset = entity.getAttackState().equals("none") ? 0.0F : 0.3F;
        currentOffset = MathHelper.lerp(0.01F, currentOffset, newOffset);
        renderOrb(entity.headYaw, entity.age, new Vec3f(-0.25F, 2.1F, 0.0F), stack, bufferIn);
        renderOrb(entity.bodyYaw, entity.age, new Vec3f(0.5F, 0.8F + currentOffset, 0.5F + currentOffset), stack, bufferIn);
        renderOrb(entity.bodyYaw, entity.age, new Vec3f(0.5F, 0.8F + currentOffset, -0.5F - currentOffset), stack, bufferIn);
    }

    public void renderOrb(float yaw, int age, Vec3f offset, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        matrixStack.push();
        offset = MineCellsMathHelper.vectorRotateY(offset, yaw * MathHelper.PI / 180.0F);
        matrixStack.translate(offset.getX(), offset.getY() + MathHelper.sin((float)age * MathHelper.PI / 45.0F) * 0.1F, offset.getZ());
        matrixStack.scale(0.375F, 0.375F, 0.375F);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(ORB_LAYER);
        produceVertex(vertexConsumer, matrix4f, matrix3f, 255, 0.0F, 0, 0, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, 255, 1.0F, 0, 1, 1);
        produceVertex(vertexConsumer, matrix4f, matrix3f, 255, 1.0F, 1, 1, 0);
        produceVertex(vertexConsumer, matrix4f, matrix3f, 255, 0.0F, 1, 0, 0);
        matrixStack.pop();
    }

    public static void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV) {
        vertexConsumer.vertex(positionMatrix, x - 0.5F, (float)y - 0.25F, 0.0F).color(255, 255, 255, 255).texture((float)textureU, (float)textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
    }
}
