package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.RancidRatEntityModel;
import com.github.mim1q.minecells.entity.RancidRatEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RancidRatEntityRenderer extends MobEntityRenderer<RancidRatEntity, RancidRatEntityModel> {

  private static final Identifier TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/rancid_rat/rancid_rat.png");
  private static final Identifier GLOW_TEXTURE = new Identifier(MineCells.MOD_ID, "textures/entity/rancid_rat/rancid_rat_glow.png");

  public RancidRatEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new RancidRatEntityModel(context.getPart(RendererRegistry.RANCID_RAT_LAYER)), 0.35f);
    if (MineCellsClient.CLIENT_CONFIG.rendering.rancidRatGlow) {
      this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }
  }

  @Override
  public Identifier getTexture(RancidRatEntity entity) {
    return TEXTURE;
  }
}
