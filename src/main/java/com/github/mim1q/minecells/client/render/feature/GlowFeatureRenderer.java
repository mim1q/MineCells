package com.github.mim1q.minecells.client.render.feature;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class GlowFeatureRenderer<E extends Entity, M extends EntityModel<E>> extends FeatureRenderer<E, M> {

  protected final RenderLayer glowLayer;

  public GlowFeatureRenderer(FeatureRendererContext<E, M> context, Identifier glowTexture) {
    super(context);
    this.glowLayer = RenderLayer.getEyes(glowTexture);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, E entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(glowLayer);
    this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
  }
}
