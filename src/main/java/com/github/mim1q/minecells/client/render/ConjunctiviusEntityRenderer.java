package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.ConjunctiviusEntityModel;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ConjunctiviusEntityRenderer extends MobEntityRenderer<ConjunctiviusEntity, ConjunctiviusEntityModel> {

  public static final Identifier TEXTURE = MineCells.createId("textures/entity/conjunctivius/conjunctivius.png");

  public ConjunctiviusEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new ConjunctiviusEntityModel(context.getPart(RendererRegistry.CONJUNCTIVIUS_MAIN_LAYER)), 1.5F);
  }

  @Override
  public Identifier getTexture(ConjunctiviusEntity entity) {
    return TEXTURE;
  }
}
