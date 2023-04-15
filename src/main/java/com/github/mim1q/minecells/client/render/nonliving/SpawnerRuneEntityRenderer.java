package com.github.mim1q.minecells.client.render.nonliving;

import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SpawnerRuneEntityRenderer extends EntityRenderer<SpawnerRuneEntity> {
  public SpawnerRuneEntityRenderer(EntityRendererFactory.Context ctx) {
    super(ctx);
  }

  @Override
  public Identifier getTexture(SpawnerRuneEntity entity) {
    return null;
  }
}
