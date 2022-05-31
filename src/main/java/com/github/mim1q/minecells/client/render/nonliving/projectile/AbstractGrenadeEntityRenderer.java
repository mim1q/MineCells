package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public abstract class AbstractGrenadeEntityRenderer <E extends GrenadeEntity> extends EntityRenderer<E> {

    private final Identifier texture;
    @Nullable
    private final Identifier glowTexture;
    private final EntityModel<?> model;

    public AbstractGrenadeEntityRenderer(EntityRendererFactory.Context ctx, Identifier textureId, @Nullable Identifier glowTextureId, EntityModel<?> model) {
        super(ctx);
        this.texture = textureId;
        this.glowTexture = glowTextureId;
        this.model = model;
    }

    @Override
    public void render(E entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        int overlay = OverlayTexture.DEFAULT_UV;
        if (entity.getFuse() < 10 && entity.getFuse() / 2 % 2 == 0) {
            overlay = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.texture));
        this.model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        if (glowTexture != null) {
            VertexConsumer vertexConsumerGlowing = vertexConsumers.getBuffer(RenderLayer.getEyes(this.glowTexture));
            this.model.render(matrices, vertexConsumerGlowing, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
        matrices.pop();
    }

    @Override
    public Identifier getTexture(GrenadeEntity entity) {
        return this.texture;
    }
}
