package com.github.mim1q.minecells.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;

public class ShieldbearerEntity extends MineCellsEntity {
    public ShieldbearerEntity(EntityType<ShieldbearerEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createShieldbearerAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }
}
