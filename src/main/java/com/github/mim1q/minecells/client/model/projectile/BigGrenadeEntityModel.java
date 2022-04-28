package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.BigGrenadeEntity;
import net.minecraft.client.model.*;

public class BigGrenadeEntityModel extends AbstractGrenadeEntityModel<BigGrenadeEntity> {

    public BigGrenadeEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return AbstractGrenadeEntityModel.getTexturedModelData(12.0F);
    }
}
