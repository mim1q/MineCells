package com.github.mim1q.minecells.client.render.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class CustomArrowEntityRenderer extends EntityRenderer<CustomArrowEntity> {
  protected CustomArrowEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public Identifier getTexture(CustomArrowEntity entity) {
    return null;
  }
}
