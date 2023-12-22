package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DisgustingWormEntity extends MineCellsEntity {

  private int soundCountdown = 0;

  public DisgustingWormEntity(EntityType<DisgustingWormEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    this.goalSelector.add(3, new LookAroundGoal(this));
    this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
    this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

    this.goalSelector.add(0, new MeleeAttackGoal(this, 1.75D, false));
  }

  @Override
  public void tick() {
    super.tick();
    if (!getWorld().isClient() && this.isAttacking()) {
      if (this.soundCountdown > 0) {
        this.soundCountdown--;
      } else {
        getWorld().playSound(null, getX(), getY(), getZ(), MineCellsSounds.DISGUSTING_WORM_ATTACK, SoundCategory.HOSTILE, 1.0F, 1.0F);
        this.soundCountdown = 60 + this.random.nextInt(100);
      }
    }
  }

  @Override
  public void onDeath(DamageSource source) {
    for (int i = 0; i < 6; i++) {
      Vec3d velocity = new Vec3d(this.random.nextDouble() - 0.5D, 0.7D + this.random.nextDouble() * 0.5D, this.random.nextDouble() - 0.5D).multiply(0.3D);

      DisgustingWormEggEntity grenade = new DisgustingWormEggEntity(MineCellsEntities.DISGUSTING_WORM_EGG, getWorld());
      grenade.setPosition(this.getPos());
      grenade.shoot(velocity);
      grenade.setFuse(15 + i * 5 + this.random.nextInt(5));

      getWorld().spawnEntity(grenade);
    }
  }

  @Override
  protected SoundEvent getDeathSound() {
    return MineCellsSounds.DISGUSTING_WORM_DEATH;
  }

  public static DefaultAttributeContainer.Builder createDisgustingWormAttributes() {
    return createLivingAttributes()
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D)
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5D);
  }
}
