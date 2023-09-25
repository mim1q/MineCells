package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.ConciergeEntityModel;
import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class ConciergeEntityRenderer extends LivingEntityRenderer<ConciergeEntity, ConciergeEntityModel> {
  public static final Identifier TEXTURE = MineCells.createId("textures/entity/concierge/concierge.png");
  public static final Identifier TEXTURE_GLOW = MineCells.createId("textures/entity/concierge/concierge_glow.png");

  public ConciergeEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new ConciergeEntityModel(ctx.getPart(MineCellsRenderers.CONCIERGE_LAYER)), 0.75F);
    this.addFeature(new GlowFeatureRenderer<>(this, TEXTURE_GLOW));
  }

  @Override
  public Identifier getTexture(ConciergeEntity entity) {
    return TEXTURE;
  }
}
