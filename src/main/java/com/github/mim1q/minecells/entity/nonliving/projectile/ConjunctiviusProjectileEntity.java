package com.github.mim1q.minecells.entity.nonliving.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
  public ConjunctiviusProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.world.isClient() && this.horizontalCollision || this.verticalCollision) {
      this.kill();
    }
    double e = this.getVelocity().x;
    double f = this.getVelocity().y;
    double g = this.getVelocity().z;
    double l = this.getVelocity().horizontalLength();
    this.setYaw((float)(-MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN));
    this.setPitch((float)(-MathHelper.atan2(f, l) * MathHelper.DEGREES_PER_RADIAN));
  }

  @Override
  protected float getDamage() {
    return 10.0F;
  }

  @Override
  protected void spawnParticles() {

  }
}
