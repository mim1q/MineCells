package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.client.render.model.nonliving.ObeliskEntityModel;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class ObeliskEntityRenderer extends EntityRenderer<ObeliskEntity> {
  private final Identifier texture;
  private final ObeliskEntityModel model;
  private final AdvancementHintRenderer hintRenderer;

  public ObeliskEntityRenderer(EntityRendererFactory.Context ctx, String texture) {
    super(ctx);
    this.model = new ObeliskEntityModel(ctx.getPart(MineCellsRenderers.OBELISK_LAYER));
    this.texture = MineCells.createId("textures/entity/obelisk/" + texture + ".png");
    this.hintRenderer = new AdvancementHintRenderer(null, ctx.getItemRenderer(), 0xFFFF4E3A, null);
  }

  @Override
  public void render(ObeliskEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    matrices.push();
    {
      matrices.scale(1.0F, -1.0F, -1.0F);
      matrices.translate(0.0F, -1.5F, 0.0F);
      matrices.multiply(new Quaternionf().rotationY(MathUtils.radians(yaw)));
      float animationProgress = entity.age + tickDelta;
      this.model.setAngles(entity, 0.0F, 0.0F, animationProgress, 0.0F, 0.0F);
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
      this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      VertexConsumer glowVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(texture));
      var glow = entity.glow.update(animationProgress);
      this.model.renderGlow(matrices, glowVertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, glow);
    }
    matrices.pop();
    if (entity.bury.getValue() < 0.1F) {
      matrices.push();
      {
        matrices.translate(0.0, 2.75, 0.0);
        hintRenderer.render(matrices, vertexConsumers, entity.age + tickDelta);
      }
      matrices.pop();
    }
  }

  @Override
  public Identifier getTexture(ObeliskEntity entity) {
    return texture;
  }
}
