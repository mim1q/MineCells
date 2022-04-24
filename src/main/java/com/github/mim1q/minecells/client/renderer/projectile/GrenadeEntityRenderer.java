package com.github.mim1q.minecells.client.renderer.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.model.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GrenadeEntityRenderer extends EntityRenderer<GrenadeEntity> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/projectiles/grenade.png");
    private final GrenadeEntityModel model = new GrenadeEntityModel(GrenadeEntityModel.getTexturedModelData().createModel());

    public GrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(GrenadeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(GrenadeEntity entity) {
        return TEXTURE;
    }
}
