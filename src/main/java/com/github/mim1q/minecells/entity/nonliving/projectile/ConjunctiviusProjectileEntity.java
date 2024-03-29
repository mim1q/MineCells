package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConjunctiviusProjectileEntity extends MagicOrbEntity {
  public ConjunctiviusProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
    super(entityType, world);
  }

  public ConjunctiviusProjectileEntity(World world, Vec3d position, Vec3d velocity) {
    this(MineCellsEntities.CONJUNCTIVIUS_PROJECTILE, world);
    this.setPosition(position.x, position.y, position.z);
    this.setVelocity(velocity.x, velocity.y, velocity.z);
  }

  @Override
  public void tick() {
    super.tick();

    if (this.age <= 1) {
      MineCells.LOGGER.info(this.getId() + " -> " + this.getVelocity().toString());
    }

    if (!getWorld().isClient() && getWorld().getBlockCollisions(this, this.getBoundingBox()).iterator().hasNext()) {
      this.kill();
    }
  }

  public void updateRotation() {
    this.prevYaw = this.getYaw();
    this.prevPitch = this.getPitch();

    double e = this.getVelocity().x;
    double f = this.getVelocity().y;
    double g = this.getVelocity().z;
    double l = this.getVelocity().horizontalLength();
    this.setYaw((float) (-MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN));
    this.setPitch((float) (-MathHelper.atan2(f, l) * MathHelper.DEGREES_PER_RADIAN));
  }

  public static void spawn(World world, Vec3d pos, Vec3d target, ConjunctiviusEntity owner) {
    var velocity = target.subtract(pos).normalize();
    ConjunctiviusProjectileEntity projectile = new ConjunctiviusProjectileEntity(world, pos, velocity);
    projectile.updateRotation();
    projectile.setOwner(owner);

    world.spawnEntity(projectile);
  }

  @Override
  protected float getDamage() {
    if (this.getOwner() instanceof ConjunctiviusEntity owner) {
      return owner.getDamage(1f);
    }
    return 8.0f;
  }

  @Override
  protected void spawnParticles() {
    getWorld().addParticle(
      MineCellsParticles.SPECKLE.get(0x00FF40),
      this.getPos().x,
      this.getPos().y,
      this.getPos().z,
      0.0D,
      0.0D,
      0.0D
    );
  }

  @Override
  public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);
    this.updateRotation();
  }
}
