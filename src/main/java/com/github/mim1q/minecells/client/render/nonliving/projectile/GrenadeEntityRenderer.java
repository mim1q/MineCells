package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.render.model.nonliving.projectile.GrenadeEntityModel;
import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class GrenadeEntityRenderer extends AbstractGrenadeEntityRenderer<GrenadeEntity> {

  private static final Identifier TEXTURE = MineCells.createId("textures/entity/grenades/grenade.png");
  private static final GrenadeEntityModel MODEL = new GrenadeEntityModel(GrenadeEntityModel.getTexturedModelData().createModel());

  public GrenadeEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx, TEXTURE, TEXTURE, MODEL);
  }
}
