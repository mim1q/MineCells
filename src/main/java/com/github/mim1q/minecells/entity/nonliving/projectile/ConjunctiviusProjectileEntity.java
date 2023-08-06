package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
  public ConjunctiviusProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  public void tick() {
    this.updateRotation();
    super.tick();
    if (!getWorld().isClient() && getWorld().getBlockCollisions(this, this.getBoundingBox()).iterator().hasNext()) {
      this.kill();
    }
  }

  public void updateRotation() {
    double e = this.getVelocity().x;
    double f = this.getVelocity().y;
    double g = this.getVelocity().z;
    double l = this.getVelocity().horizontalLength();
    this.setYaw((float)(-MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN));
    this.setPitch((float)(-MathHelper.atan2(f, l) * MathHelper.DEGREES_PER_RADIAN));
  }

  public static void spawn(World world, Vec3d pos, Vec3d target, ConjunctiviusEntity owner) {
    ConjunctiviusProjectileEntity projectile = MineCellsEntities.CONJUNCTIVIUS_PROJECTILE.create(world);
    if (projectile != null) {
      projectile.setPosition(pos);
      projectile.setVelocity(target.subtract(pos).normalize());
      projectile.updateRotation();
      projectile.setOwner(owner);
      world.spawnEntity(projectile);
    }
  }

  @Override
  protected float getDamage() {
    return 10.0F;
  }

  @Override
  protected void spawnParticles() {

  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
    this.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
    this.updateRotation();
  }
}
