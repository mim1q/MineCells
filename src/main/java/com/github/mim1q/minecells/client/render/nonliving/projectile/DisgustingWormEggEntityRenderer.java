package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.DisgustingWormEggEntityModel;
import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class DisgustingWormEggEntityRenderer extends AbstractGrenadeEntityRenderer<DisgustingWormEggEntity> {

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/grenades/disgusting_worm_egg.png");
  private static final DisgustingWormEggEntityModel MODEL = new DisgustingWormEggEntityModel(DisgustingWormEggEntityModel.getTexturedModelData().createModel());

  public DisgustingWormEggEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, TEXTURE, null, MODEL);
  }
}
