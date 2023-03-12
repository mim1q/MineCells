package com.github.mim1q.minecells.client.render.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class MineCellsEffectsFeatureRenderer<E extends LivingEntity, M extends EntityModel<E>> extends FeatureRenderer<E, M> {
  public static final Identifier STUNNED_STAR = MineCells.createId("textures/entity/effect/stunned.png");

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
    if (entityAccessor.getMineCellsFlag(MineCellsEffectFlags.STUNNED)) {
      VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(STUNNED_STAR));
      float theta = animationProgress * 20F;
      for (int i = 0; i < 3; i++) {
        float dy = MathHelper.sin(MathUtils.radians(-theta));
        float dx = MathHelper.cos(MathUtils.radians(-theta));
        drawBillboard(entity, consumer, matrices, new Vec3d(dx * 0.2, dy * 0.2, 0));
        theta += 120.0F;
      }
    }
  }

  private void drawBillboard(E entity, VertexConsumer consumer, MatrixStack matrices, Vec3d offset) {
    matrices.push();
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F - entity.getYaw(MinecraftClient.getInstance().getTickDelta())));
    matrices.translate(0.0D, 1.0D - entity.getHeight(), 0.0D);
    matrices.scale(-1F, -1F, 1F);
    matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
    matrices.translate(offset.getX(), offset.getY(), offset.getZ());
    RenderUtils.drawBillboard(consumer, matrices, 0xF000F0, 0.5F, 0.5F, 255);
    matrices.pop();
  }
}