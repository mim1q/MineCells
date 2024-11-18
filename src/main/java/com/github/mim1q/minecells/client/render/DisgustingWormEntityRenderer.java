package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.DisgustingWormEntityModel;
import com.github.mim1q.minecells.entity.DisgustingWormEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class DisgustingWormEntityRenderer extends MineCellsEntityRenderer<DisgustingWormEntity, DisgustingWormEntityModel> {

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/disgusting_worm/disgusting_worm.png");
  private static final Identifier GLOW_TEXTURE = MineCells.createId("textures/entity/disgusting_worm/disgusting_worm_glow.png");

  public DisgustingWormEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new DisgustingWormEntityModel(ctx.getPart(MineCellsRenderers.DISGUSTING_WORM_LAYER)), 0.75F);
    if (MineCellsClient.CLIENT_CONFIG.rendering().disgustingWormGlow) {
      this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }
  }

  @Override
  public Identifier getTexture(DisgustingWormEntity entity) {
    return TEXTURE;
  }
}
