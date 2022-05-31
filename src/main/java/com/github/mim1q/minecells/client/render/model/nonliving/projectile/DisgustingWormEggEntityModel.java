package com.github.mim1q.minecells.client.render.model.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;

public class DisgustingWormEggEntityModel extends AbstractGrenadeEntityModel<DisgustingWormEggEntity> {

    public DisgustingWormEggEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return AbstractGrenadeEntityModel.getTexturedModelData(6.0F);
    }
}

