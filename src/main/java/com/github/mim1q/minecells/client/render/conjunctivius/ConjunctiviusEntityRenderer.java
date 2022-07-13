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
    this.addFeature(new ConjunctiviusEyeRenderer(this, context.getPart(RendererRegistry.CONJUNCTIVIUS_EYE_LAYER)));
    ConjunctiviusTentacleRenderer tentacles = new ConjunctiviusTentacleRenderer(this, context.getPart(RendererRegistry.CONJUNCTIVIUS_TENTACLE_LAYER));
    tentacles.addPosRotScale( 0.75F, 1.9F,  0.725F, 0.0F,   45.0F,  0.0F,  0.5F);
    tentacles.addPosRotScale(-0.75F, 1.9F,  0.725F, 0.0F,  -45.0F,  0.0F,  0.5F);
    tentacles.addPosRotScale( 1.1F,  1.5F,  0.55F,  15.0F,  60.0F, -10.0F, 0.75F);
    tentacles.addPosRotScale(-1.1F,  1.5F,  0.55F,  15.0F, -60.0F,  10.0F, 0.75F);
    tentacles.addPosRotScale( 1.0F,  1.75F, 0.25F,  5.0F,   80.0F, -5.0F,  0.66F);
    tentacles.addPosRotScale(-1.0F,  1.75F, 0.25F, -5.0F,  -80.0F, -5.0F,  0.66F);
    this.addFeature(tentacles);
    ConjunctiviusSpikeRenderer spikes = new ConjunctiviusSpikeRenderer(this, context.getPart(RendererRegistry.CONJUNCTIVIUS_SPIKE_LAYER));
    spikes.addPosRotScale(1.2F, -0.15F, -0.25F, 0.0F, 25.0F, 80.0F,  1.0F);
    spikes.addPosRotScale(1.0F, -0.5F,  -0.6F,  0.0F, 15.0F, 60.0F,  1.0F);
    spikes.addPosRotScale(1.35F, 0.4F,  -0.6F,  0.0F, 35.0F, 90.0F,  1.0F);
    spikes.addPosRotScale(1.05F, 0.8F,  -0.8F,  0.0F, 45.0F, 110.0F, 1.0F);
    spikes.addPosRotScale(1.0F,  1.2F,  -0.6F,  0.0F, 35.0F, 125.0F, 1.0F);
    spikes.addPosRotScale(-1.2F, -0.15F, -0.25F, 0.0F, -25.0F, -80.0F,  1.0F);
    spikes.addPosRotScale(-1.0F, -0.5F,  -0.6F,  0.0F, -15.0F, -60.0F,  1.0F);
    spikes.addPosRotScale(-1.35F, 0.4F,  -0.6F,  0.0F, -35.0F, -90.0F,  1.0F);
    spikes.addPosRotScale(-1.05F, 0.8F,  -0.8F,  0.0F, -45.0F, -110.0F, 1.0F);
    spikes.addPosRotScale(-1.0F,  1.2F,  -0.6F,  0.0F, -35.0F, -125.0F, 1.0F);
    this.addFeature(spikes);
  }

  @Override
  public Identifier getTexture(ConjunctiviusEntity entity) {
    return TEXTURE;
  }
}
