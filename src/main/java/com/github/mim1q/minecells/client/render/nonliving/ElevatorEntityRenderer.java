package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.ElevatorEntityModel;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class ElevatorEntityRenderer extends EntityRenderer<ElevatorEntity> {

    private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/elevator.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    private final ElevatorEntityModel model;

    public ElevatorEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ElevatorEntityModel(ElevatorEntityModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier getTexture(ElevatorEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ElevatorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        if (entity.getIsRotated()) {
            matrices.multiply(Quaternion.fromEulerYxz(MathHelper.HALF_PI, 0.0F, 0.0F));
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
