package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class ConjunctiviusEntity extends MineCellsBossEntity {
  public final AnimationProperty spikeOffset = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 16.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 8.0D);
  }

  @Override
  public void tick() {
    super.tick();
    if (this.age % 80 == 0) {
      this.spikeOffset.setupTransitionTo(0.0F, 3.0F);
    } else if (this.age % 40 == 0) {
      this.spikeOffset.setupTransitionTo(5.0F, 10.0F);
    }
  }
}
