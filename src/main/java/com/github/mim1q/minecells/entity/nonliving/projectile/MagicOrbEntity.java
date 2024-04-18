package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.entity.nonliving.SimpleProjectileEntity;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class MagicOrbEntity extends SimpleProjectileEntity {

  public MagicOrbEntity(EntityType<? extends SimpleProjectileEntity> entityType, World world) {
    super(entityType, world);
  }

  public MagicOrbEntity(EntityType<? extends SimpleProjectileEntity> entityType, World world, LivingEntity owner) {
    super(entityType, world, owner);
  }

  @Override
  public void tick() {
    super.tick();

    if (getWorld().isClient()) {
      this.spawnParticles();
    } else {
      if (this.age > 300 || this.getVelocity().lengthSquared() < 0.01D) {
        this.discard();
      }
    }
  }

  protected void spawnParticles() {
    var particle = MineCellsParticles.SPECKLE.get(0xFFAAFF);
    if (this.age == 1) {
      ParticleUtils.addAura((ClientWorld) getWorld(), this.getPos().add(0.0D, 0.25D, 0.0D), particle, 15, 0.0D, 0.5D);
    }
    ParticleUtils.addAura((ClientWorld) getWorld(), this.getPos().add(0.0D, 0.25D, 0.0D), particle, 3, 0.5D, 0.0D);
    ParticleUtils.addAura((ClientWorld) getWorld(), this.getPos().add(0.0D, 0.25D, 0.0D), particle, 3, 0.0D, 0.0D);
  }

  public float getDamage() {
    return 4.0F;
  }
//
//  @Override public void onSpawnPacket(EntitySpawnS2CPacket packet) {
//    super.onSpawnPacket(packet);
//
//    var vx = packet.getVelocityX();
//    var vy = packet.getVelocityY();
//    var vz = packet.getVelocityZ();
//
//    this.setVelocity(vx, vy, vz);
//  }
}
