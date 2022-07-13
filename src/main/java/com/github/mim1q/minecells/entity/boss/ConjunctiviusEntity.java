package com.github.mim1q.minecells.entity.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class ConjunctiviusEntity extends MineCellsBossEntity {
  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }



  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 8.0D);
  }
}
