package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
    this.updateRotation();
  }

  public void updateRotation() {
    double e = this.getVelocity().x;
    double f = this.getVelocity().y;
    double g = this.getVelocity().z;
    double l = this.getVelocity().horizontalLength();
    this.setYaw((float)(-MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN));
    this.setPitch((float)(-MathHelper.atan2(f, l) * MathHelper.DEGREES_PER_RADIAN));
    this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
  }

  public static void spawn(World world, Vec3d pos, Vec3d target) {
    ConjunctiviusProjectileEntity projectile = new ConjunctiviusProjectileEntity(EntityRegistry.CONJUNCTIVIUS_PROJECTILE, world);
    projectile.setPosition(pos);
    projectile.setVelocity(target.subtract(pos).normalize().multiply(1.0));
    projectile.updateRotation();
    world.spawnEntity(projectile);
  }

  @Override
  protected float getDamage() {
    return 10.0F;
  }

  @Override
  protected void spawnParticles() {

  }
}
