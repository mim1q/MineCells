package com.github.mim1q.minecells.client.render.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.entity.SweeperEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SweeperEntityRenderer extends MobEntityRenderer<SweeperEntity, SweeperEntityModel> {
  private static final Identifier TEXTURE = MineCells.createId("textures/entity/sweeper/sweeper.png");
  private static final Identifier GLOW_TEXTURE = MineCells.createId("textures/entity/sweeper/sweeper_glow.png");

  public SweeperEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new SweeperEntityModel(context.getPart(MineCellsRenderers.SWEEPER_LAYER)), 0.5F);
    addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
  }

  @Override
  public Identifier getTexture(SweeperEntity entity) {
    return TEXTURE;
  }
}
