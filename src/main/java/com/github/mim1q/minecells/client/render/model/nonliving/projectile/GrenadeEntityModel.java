package com.github.mim1q.minecells.client.render.model.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;

public class GrenadeEntityModel extends AbstractGrenadeEntityModel<GrenadeEntity> {

    public GrenadeEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        return AbstractGrenadeEntityModel.getTexturedModelData();
    }
}
