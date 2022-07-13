package com.github.mim1q.minecells.client.render.conjunctivius;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.conjunctivius.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ConjunctiviusEntityRenderer extends MobEntityRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  public static final Identifier TEXTURE = MineCells.createId("textures/entity/conjunctivius/conjunctivius.png");

  public ConjunctiviusEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new ConjunctiviusEntityModel(context.getPart(RendererRegistry.CONJUNCTIVIUS_MAIN_LAYER)), 1.5F);
    this.addFeature(new ConjunctiviusEyeFeatureRenderer(this, context.getPart(RendererRegistry.CONJUNCTIVIUS_EYE_LAYER)));
    ConjunctiviusTentacleFeatureRenderer tentacles = new ConjunctiviusTentacleFeatureRenderer(this, context.getPart(RendererRegistry.CONJUNCTIVIUS_TENTACLE_LAYER));
    tentacles.addPosRotScale(0.0F, 2.0F, 0.75F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    this.addFeature(tentacles);
  }

  @Override
  public Identifier getTexture(ConjunctiviusEntity entity) {
    return TEXTURE;
  }
}
