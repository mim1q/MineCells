package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.LeapingZombieEntityModel;
import com.github.mim1q.minecells.entity.LeapingZombieEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class LeapingZombieEntityRenderer extends MobEntityRenderer<LeapingZombieEntity, LeapingZombieEntityModel> {

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/leaping_zombie/leaping_zombie.png");
  private static final Identifier GLOW_TEXTURE = MineCells.createId("textures/entity/leaping_zombie/leaping_zombie_glow.png");

  public LeapingZombieEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, new LeapingZombieEntityModel(ctx.getPart(MineCellsRenderers.LEAPING_ZOMBIE_LAYER)), 0.35F);
    if (MineCellsClient.CLIENT_CONFIG.rendering.leapingZombieGlow) {
      this.addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
    }
  }

  @Override
  public Identifier getTexture(LeapingZombieEntity entity) {
    return TEXTURE;
  }
}