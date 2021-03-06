package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
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
    if (this.world.isClient()) {
      this.spawnParticles();
    } else {
      if (this.age > 200) {
        this.discard();
      }

      EntityHitResult hitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));
      if (hitResult != null) {
        this.onEntityHit(hitResult);
      }
    }
  }

  protected void spawnParticles() {
    if (this.age == 1) {
      ParticleUtils.addAura((ClientWorld) this.world, this.getPos().add(0.0D, 0.25D, 0.0D), ParticleTypes.END_ROD, 15, 0.0D, 0.5D);
    }
    ParticleUtils.addAura((ClientWorld) this.world, this.getPos().add(0.0D, 0.25D, 0.0D), ParticleTypes.END_ROD, 3, 0.5D, 0.0D);
    ParticleUtils.addAura((ClientWorld) this.world, this.getPos().add(0.0D, 0.25D, 0.0D), ParticleTypes.END_ROD, 3, 0.0D, 0.0D);
  }

  protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
    return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()), this::canHit);
  }

  @Override
  protected void onEntityHit(EntityHitResult entityHitResult) {
    Entity entity = entityHitResult.getEntity();

    if (entity instanceof PlayerEntity) {
      DamageSource damageSource = this.getOwner() == null
        ? DamageSource.MAGIC
        : DamageSource.mob((LivingEntity) this.getOwner());
      entity.damage(damageSource, 5.0F);
      this.kill();
    }
  }

  @Override
  protected void initDataTracker() {
  }
}
