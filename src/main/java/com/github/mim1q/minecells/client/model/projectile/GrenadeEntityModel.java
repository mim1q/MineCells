package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
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
