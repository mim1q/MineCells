package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GrenadeEntityRenderer<E extends GrenadeEntity> extends EntityRenderer<E> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/grenades/grenade.png");
    private final GrenadeEntityModel MODEL = new GrenadeEntityModel(GrenadeEntityModel.getTexturedModelData().createModel());

    public GrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(E entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        int overlay = OverlayTexture.DEFAULT_UV;
        if (entity.getFuse() < 15 && entity.getFuse() / 2 % 2 == 0 ) {
            overlay = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.MODEL.getLayer(TEXTURE));
        this.MODEL.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer vertexConsumerGlowing = vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE));
        this.MODEL.render(matrices, vertexConsumerGlowing, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(GrenadeEntity entity) {
        return TEXTURE;
    }
}
