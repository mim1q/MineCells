package com.github.mim1q.minecells.entity.nonliving.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class DisgustingWormEggEntity extends GrenadeEntity {
    public DisgustingWormEggEntity(EntityType<DisgustingWormEggEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void explode(float power) {
        super.explode(2.0F);
    }
}
