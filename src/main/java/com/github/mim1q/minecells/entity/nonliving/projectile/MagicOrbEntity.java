package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicOrbEntity extends ProjectileEntity {

  public MagicOrbEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
    super(entityType, world);
    this.noClip = true;
  }

  @Override
  public void tick() {
    super.tick();
    this.move(MovementType.SELF, this.getVelocity());
    if (getWorld().isClient()) {
      this.spawnParticles();
    } else {
      if (this.age > 300 || this.getVelocity().lengthSquared() < 0.01D) {
        this.discard();
      }

      EntityHitResult hitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));
      if (hitResult != null) {
        this.onEntityHit(hitResult);
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
      DamageSource damageSource = this.getOwner() == null
        ? getDamageSources().magic()
        : getDamageSources().indirectMagic(this, this.getOwner());
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
}
