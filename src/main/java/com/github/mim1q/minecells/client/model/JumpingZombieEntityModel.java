package com.github.mim1q.minecells.client.model;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class JumpingZombieEntityModel extends AnimatedGeoModel<JumpingZombieEntity>
{
    @Override
    public Identifier getModelLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "geo/entity/jumping_zombie.geo.json");
    }

    @Override
    public Identifier getTextureLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "textures/entity/jumping_zombie.png");
    }

    @Override
    public Identifier getAnimationFileLocation(JumpingZombieEntity object)
    {
        return new Identifier(MineCells.MOD_ID, "animations/entity/jumping_zombie.animation.json");
    }
}
