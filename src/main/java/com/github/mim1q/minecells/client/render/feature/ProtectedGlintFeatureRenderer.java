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

public class ProtectedGlintFeatureRenderer<E extends LivingEntity, M extends EntityModel<E>> extends FeatureRenderer<E, M> {

  public ProtectedGlintFeatureRenderer(FeatureRendererContext<E, M> context) {
    super(context);
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, E entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
    if (((LivingEntityAccessor) entity).getMineCellsFlag(MineCellsEffectFlags.PROTECTED)) {
      matrices.push();
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityGlint());
      this.getContextModel().render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.pop();
    }
  }
}