package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ShockerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShockerEntityModel extends AnimatedGeoModel<ShockerEntity> {
    @Override
    public Identifier getModelLocation(ShockerEntity object) {
        return new Identifier(MineCells.MOD_ID, "geo/entity/shocker.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ShockerEntity object) {
        return new Identifier(MineCells.MOD_ID, "textures/entity/shocker.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ShockerEntity object) {
        return new Identifier(MineCells.MOD_ID, "animations/entity/shocker.animation.json");
    }
}
