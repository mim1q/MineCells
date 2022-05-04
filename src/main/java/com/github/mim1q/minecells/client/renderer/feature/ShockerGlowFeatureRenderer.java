package com.github.mim1q.minecells.client.renderer.feature;

import com.github.mim1q.minecells.client.model.ShockerEntityModel;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShockerGlowFeatureRenderer extends GlowFeatureRenderer<ShockerEntity, ShockerEntityModel> {
    protected final RenderLayer glowLayerAngry;

    public ShockerGlowFeatureRenderer(FeatureRendererContext<ShockerEntity, ShockerEntityModel> context, Identifier glowTexture, Identifier glowTextureAngry) {
        super(context, glowTexture);
        this.glowLayerAngry = RenderLayer.getEyes(glowTextureAngry);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ShockerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(entity.getAttackState().equals("none") ? this.glowLayer : this.glowLayerAngry);
        this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
