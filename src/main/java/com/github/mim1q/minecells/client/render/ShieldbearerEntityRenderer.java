package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.client.render.model.ShieldbearerEntityModel;
import com.github.mim1q.minecells.entity.ShieldbearerEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ShieldbearerEntityRenderer extends MobEntityRenderer<ShieldbearerEntity, ShieldbearerEntityModel> {

  public static final Identifier TEXTURE = new Identifier("minecells:textures/entity/shieldbearer.png");

  public ShieldbearerEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new ShieldbearerEntityModel(ctx.getPart(RendererRegistry.SHIELDBEARER_LAYER)), 0.35f);
    this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
  }

  @Override
  public Identifier getTexture(ShieldbearerEntity entity) {
    return TEXTURE;
  }

  @Override
  public void render(ShieldbearerEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
    super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
  }
}
