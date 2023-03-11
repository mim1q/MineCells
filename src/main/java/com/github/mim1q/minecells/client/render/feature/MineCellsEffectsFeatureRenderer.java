package com.github.mim1q.minecells.client.render.feature;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class MineCellsEffectsFeatureRenderer<E extends LivingEntity, M extends EntityModel<E>> extends FeatureRenderer<E, M> {

  public MineCellsEffectsFeatureRenderer(FeatureRendererContext<E, M> context) {
    super(context);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, E entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    LivingEntityAccessor entityAccessor = (LivingEntityAccessor) entity;
    M model = this.getContextModel();
    if (entityAccessor.getMineCellsFlag(MineCellsEffectFlags.PROTECTED)) {
      matrices.push();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityGlint());
      model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.pop();
    }
    if (entityAccessor.getMineCellsFlag(MineCellsEffectFlags.FROZEN)) {
      matrices.push();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
      model.render(matrices, vertexConsumer, light, OverlayTexture.getUv(0.5F, false), 0.0F, 1.0F, 1.0F, 0.5F);
      matrices.pop();
    }
  }
}