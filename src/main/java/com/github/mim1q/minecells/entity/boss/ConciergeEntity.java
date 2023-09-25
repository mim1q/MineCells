package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal.ShockwaveType;
import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import static net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN;

public class ConciergeEntity extends MineCellsBossEntity {
  private static final TrackedData<Boolean> LEAP_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> LEAP_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private static final TrackedData<Boolean> SHOCKWAVE_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> SHOCKWAVE_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private int leapCooldown = 0;
  private int shockwaveCooldown = 0;
  private int sharedCooldown = 100;

  public AnimationProperty leapAnimation = new AnimationProperty(0.0F);
  public AnimationProperty shockwaveAnimation = new AnimationProperty(0.0F);

  public ConciergeEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
    goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false));

    goalSelector.add(0, new TimedDashGoal<>(this, settings -> {
      settings.onGround = false;
      settings.cooldownGetter = () -> leapCooldown + sharedCooldown;
      settings.cooldownSetter = (cooldown) -> {
        leapCooldown = cooldown;
        sharedCooldown = 100;
      };
      settings.stateSetter = handleStateChange(LEAP_CHARGING, LEAP_RELEASING);
      settings.chargeSound = MineCellsSounds.CONCIERGE_LEAP_CHARGE;
      settings.landSound = MineCellsSounds.CONCIERGE_LEAP_LAND;
      settings.chance = 0.05F;
    }, null));

    goalSelector.add(0, new ShockwaveGoal<>(this, settings -> {
      settings.cooldownGetter = () -> shockwaveCooldown + sharedCooldown;
      settings.cooldownSetter = (cooldown) -> {
        shockwaveCooldown = cooldown;
        sharedCooldown = 100;
      };
      settings.stateSetter = handleStateChange(SHOCKWAVE_CHARGING, SHOCKWAVE_RELEASING);
      settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME;
      settings.shockwaveRadius = 20;
      settings.shockwaveInterval = 2;
      settings.shockwaveType = ShockwaveType.CIRCLE;
      settings.chargeSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_CHARGE;
      settings.releaseSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_RELEASE;
      settings.chance = 0.05F;
    }, null));

    targetSelector.add(0, new TargetRandomPlayerGoal<>(this));
    targetSelector.add(0, new ActiveTargetGoal<>(this, PigEntity.class, true));
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    dataTracker.startTracking(LEAP_CHARGING, false);
    dataTracker.startTracking(LEAP_RELEASING, false);
    dataTracker.startTracking(SHOCKWAVE_CHARGING, false);
    dataTracker.startTracking(SHOCKWAVE_RELEASING, false);
  }

  @Override
  public void tick() {
    super.tick();
  }

  @Override
  protected void decrementCooldowns() {
    leapCooldown--;
    shockwaveCooldown--;
    sharedCooldown--;
  }

  @Override
  protected void processAnimations() {
    if (dataTracker.get(LEAP_CHARGING)) {
      leapAnimation.setupTransitionTo(1F, 20F);
    } else {
      leapAnimation.setupTransitionTo(0F, 20F);
    }
    if (dataTracker.get(SHOCKWAVE_CHARGING)) {
      shockwaveAnimation.setupTransitionTo(1F, 20F);
    } else {
      shockwaveAnimation.setupTransitionTo(0F, 20F);
    }
  }

  @Override
  public void onDamaged(DamageSource damageSource) {
    super.onDamaged(damageSource);
    limbAnimator.setSpeed(0.5F);
  }

  public static DefaultAttributeContainer.Builder createConciergeAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
  }
}
