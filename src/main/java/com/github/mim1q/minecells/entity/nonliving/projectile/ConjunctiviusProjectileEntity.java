package com.github.mim1q.minecells.entity.nonliving.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
  public ConjunctiviusProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public void tick() {
    this.baseTick();
//    super.tick();
//    if (!this.world.isClient() && this.horizontalCollision || this.verticalCollision) {
//      this.kill();
//    }
  }

  @Override
  protected void spawnParticles() {

  }
}
