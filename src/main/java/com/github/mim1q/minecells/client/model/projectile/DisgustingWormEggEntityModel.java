package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.DisgustingWormEggEntity;
import net.minecraft.client.model.*;

public class DisgustingWormEggEntityModel extends AbstractGrenadeEntityModel<DisgustingWormEggEntity> {

    public DisgustingWormEggEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-3.0F, 0.0F, -3.0F,
                                6.0F, 6.0F, 6.0F),
                ModelTransform.NONE
        );
        return TexturedModelData.of(modelData, 18, 12);
    }
}

