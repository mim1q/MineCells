package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class CellEntityRenderer extends EntityRenderer<CellEntity> {

    public static final Identifier TEXTURE = MineCells.createId("textures/entity/cell.png");
    public static final RenderLayer LAYER = RenderLayer.getEntityTranslucentEmissive(TEXTURE);

    public CellEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(CellEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        this.renderCell(entity, tickDelta, matrixStack, vertexConsumerProvider, light, Vec3f.ZERO);
    }

    protected void renderCell(CellEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Vec3f offset) {
        matrixStack.push();
        float deltaY = (MathHelper.sin((entity.age + tickDelta) * 0.1F) + 1.0F) * 0.2F;
        matrixStack.translate(0.0F + offset.getX(), 0.25F + deltaY, 0.0F + offset.getZ());
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F, -0.5F, 0.0F, 0.0F, 1.0F, 0xFF);
        RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light,  0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0xFF);
        RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light,  0.5F,  0.5F, 0.0F, 1.0F, 0.0F, 0xFF);
        RenderUtils.produceVertex(vertexConsumer, matrix4f, matrix3f, light, -0.5F,  0.5F, 0.0F, 0.0F, 0.0F, 0xFF);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(CellEntity entity) {
        return TEXTURE;
    }
}
