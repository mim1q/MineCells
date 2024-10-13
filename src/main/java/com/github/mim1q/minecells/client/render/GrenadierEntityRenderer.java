package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.GrenadierEntityModel;
import com.github.mim1q.minecells.entity.GrenadierEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class GrenadierEntityRenderer extends MineCellsEntityRenderer<GrenadierEntity, GrenadierEntityModel> {

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/grenadier/grenadier.png");
  private static final Identifier GLOW_TEXTURE = MineCells.createId("textures/entity/grenadier/grenadier_glow.png");

  public GrenadierEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new GrenadierEntityModel(ctx.getPart(MineCellsRenderers.GRENADIER_LAYER)), 0.35F);
    if (MineCellsClient.CLIENT_CONFIG.rendering().grenadierGlow) {
      this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }
  }

  @Override
  public Identifier getTexture(GrenadierEntity entity) {
    return TEXTURE;
  }
}
