package com.github.mim1q.minecells.client.render.model.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.BigGrenadeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;

public class BigGrenadeEntityModel extends AbstractGrenadeEntityModel<BigGrenadeEntity> {

  public BigGrenadeEntityModel(ModelPart root) {
    super(root);
  }

  public static TexturedModelData getTexturedModelData() {
    return AbstractGrenadeEntityModel.getTexturedModelData(12.0F);
  }
}
