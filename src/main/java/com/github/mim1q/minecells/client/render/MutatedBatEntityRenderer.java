package com.github.mim1q.minecells.client.render;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.MutatedBatEntityModel;
import com.github.mim1q.minecells.entity.MutatedBatEntity;
import com.github.mim1q.minecells.registry.RendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class MutatedBatEntityRenderer extends MobEntityRenderer<MutatedBatEntity, MutatedBatEntityModel> {

  public static Identifier TEXTURE = MineCells.createId("textures/entity/mutated_bat.png");

  public MutatedBatEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new MutatedBatEntityModel(context.getPart(RendererRegistry.MUTATED_BAT_LAYER)), 0.3F);
  }

  @Override
  public Identifier getTexture(MutatedBatEntity entity) {
    return TEXTURE;
  }
}
