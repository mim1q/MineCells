package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.ConjunctiviusProjectileEntityModel;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ConjunctiviusProjectileEntityRenderer extends EntityRenderer<ConjunctiviusProjectileEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/entity/conjunctivius/projectile.png");
  private static final RenderLayer LAYER = RenderLayer.getEyes(TEXTURE);
  private final EntityModel<ConjunctiviusProjectileEntity> model;

  public ConjunctiviusProjectileEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
    this.model = new ConjunctiviusProjectileEntityModel(ctx.getPart(MineCellsRenderers.CONJUNCTIVIUS_PROJECTILE_LAYER));
  }

  @Override
  public void render(ConjunctiviusProjectileEntity entity, float entityYaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    final RenderLayer LAYER = RenderLayer.getEntityTranslucentEmissive(TEXTURE);
    VertexConsumer vertices = vertexConsumers.getBuffer(LAYER);
    //

    matrices.push();
    this.model.setAngles(entity, 0.0F, 0.0F, entity.age + tickDelta, 0.0F, 0.0F);
    this.model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    matrices.pop();
  }

  @Override
  public Identifier getTexture(ConjunctiviusProjectileEntity entity) {
    return TEXTURE;
  }
}
