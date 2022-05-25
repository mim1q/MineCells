package com.github.mim1q.minecells.client.renderer;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.ProtectorEntityModel;
import com.github.mim1q.minecells.client.renderer.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.renderer.feature.ProtectedGlintFeatureRenderer;
import com.github.mim1q.minecells.entity.ProtectorEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import com.github.mim1q.minecells.util.RenderHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class ProtectorEntityRenderer extends MobEntityRenderer<ProtectorEntity, ProtectorEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector.png");
    private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector_glow.png");
    public static final Identifier CONNECTION_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/protector/protector_connection.png");
    public static final RenderLayer CONNECTION_LAYER = RenderLayer.getEntityShadow(CONNECTION_TEXTURE);

    public ProtectorEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new ProtectorEntityModel(ctx.getPart(RendererRegistry.PROTECTOR_LAYER)), 0.35F);
        this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
        this.addFeature(new ProtectedGlintFeatureRenderer<>(this));
    }

    @Override
    public void render(ProtectorEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(CONNECTION_LAYER);
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        if (mobEntity.isActive()) {
            for (Entity e : mobEntity.trackedEntities) {
                float v = ((mobEntity.age / 8.0F) + e.getId()) % 0.875F;
                renderConnection(vertexConsumer, matrix4f, matrix3f, e.getPos().subtract(mobEntity.getPos()).add(0.0D, e.getHeight() / 2.0D, 0.0D), new Vec3d(v, 0.0D, 0.0D));
            }
        }
        matrixStack.pop();

    }

    public static void renderConnection(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, Vec3d pos0, Vec3d pos1) {
        float x = (float)pos0.x;
        float y = (float)pos0.y;
        float z = (float)pos0.z;
        float v = (float)pos1.x;

        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, 0.0F, 1.0F,     0.0F, 0, v + 0.125F);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, x,    y,        z,    1, v + 0.125F);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, x,    y + 0.5F, z,    1, v);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, 0.0F, 1.5F,     0.0F, 0, v);

        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, 0.0F, 1.5F,     0.0F, 0, v);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, x,    y + 0.5F, z,    1, v);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, x,    y,        z,    1, v + 0.125F);
        RenderHelper.produceVertex(vertexConsumer, positionMatrix, normalMatrix, 255, 0.0F, 1.0F,     0.0F, 0, v + 0.125F);
    }

    @Override
    public Identifier getTexture(ProtectorEntity entity) {
        return TEXTURE;
    }
}
