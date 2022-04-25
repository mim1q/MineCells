package com.github.mim1q.minecells.client.model.projectile;

import net.minecraft.client.model.*;

public class BigGrenadeEntityModel extends GrenadeEntityModel {

    private static final float SIZE = 12.0F;

    public BigGrenadeEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body",
                ModelPartBuilder.create().uv(0, 0).cuboid(-SIZE / 2.0F, 0.0F, -SIZE / 2.0F, SIZE, SIZE, SIZE),
                ModelTransform.NONE
        );
        return TexturedModelData.of(modelData, (int)SIZE * 4, (int)SIZE * 2);
    }
}
