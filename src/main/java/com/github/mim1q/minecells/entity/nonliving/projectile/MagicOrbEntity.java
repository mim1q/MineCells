package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicOrbEntity extends ThrownEntity {

  public MagicOrbEntity(EntityType<? extends ThrownEntity> entityType, World world) {
    super(entityType, world);
    this.noClip = false;
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

  protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
    return ProjectileUtil.getEntityCollision(getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(0.1D), this::canHit);
  }

  @Override
  protected void onEntityHit(EntityHitResult entityHitResult) {
    Entity entity = entityHitResult.getEntity();

    if (entity instanceof PlayerEntity) {
      DamageSource damageSource = this.getOwner() instanceof LivingEntity owner
        ? getDamageSources().mobAttack(owner)
        : getDamageSources().generic();
      entity.damage(damageSource, this.getDamage());
      this.kill();
    }
  }

  protected float getDamage() {
    return 3.0F;
  }

  @Override
  protected void initDataTracker() {
  }

  @Override
  protected float getGravity() {
    return 0.0F;
  }

  @Override public void onSpawnPacket(EntitySpawnS2CPacket packet) {
    super.onSpawnPacket(packet);

    var vx = packet.getVelocityX();
    var vy = packet.getVelocityY();
    var vz = packet.getVelocityZ();

    this.setVelocity(vx, vy, vz);
  }
}
