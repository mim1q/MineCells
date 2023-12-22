package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.FlyEntityModel;
import com.github.mim1q.minecells.entity.FlyEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FlyEntityRenderer<T extends FlyEntity> extends MobEntityRenderer<T, FlyEntityModel<T>> {
  private final Identifier texture;

  public FlyEntityRenderer(EntityRendererFactory.Context context, String textureName) {
    super(context, new FlyEntityModel<>(context.getPart(MineCellsRenderers.FLY_LAYER)), 0.25F);
    this.texture = MineCells.createId("textures/entity/fly/" + textureName + ".png");
    var glowTexture = MineCells.createId("textures/entity/fly/" + textureName + "_glow.png");
    this.addFeature(new GlowFeatureRenderer<>(this, glowTexture));
  }

  @Override
  public Identifier getTexture(T entity) {
    return texture;
  }
}
