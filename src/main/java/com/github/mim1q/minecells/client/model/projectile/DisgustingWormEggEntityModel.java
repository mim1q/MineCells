package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.DisgustingWormEggEntity;
import net.minecraft.client.model.*;

public class DisgustingWormEggEntityModel extends AbstractGrenadeEntityModel<DisgustingWormEggEntity> {

    public DisgustingWormEggEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return AbstractGrenadeEntityModel.getTexturedModelData(6.0F);
    }
}

