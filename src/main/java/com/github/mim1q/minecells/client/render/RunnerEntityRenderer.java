package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.feature.GlowFeatureRenderer;
import com.github.mim1q.minecells.client.render.model.RunnerEntityModel;
import com.github.mim1q.minecells.entity.RunnerEntity;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RunnerEntityRenderer extends MobEntityRenderer<RunnerEntity, RunnerEntityModel> {

  public static Identifier TEXTURE = MineCells.createId("textures/entity/runner/runner.png");
  public static Identifier GLOW_TEXTURE = MineCells.createId("textures/entity/runner/runner_glow.png");

  public RunnerEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new RunnerEntityModel(context.getPart(MineCellsRenderers.RUNNER_LAYER)), 0.4f);
    addFeature(new GlowFeatureRenderer<>(this, GLOW_TEXTURE));
  }

  @Override
  public Identifier getTexture(RunnerEntity entity) {
    return TEXTURE;
  }
}
