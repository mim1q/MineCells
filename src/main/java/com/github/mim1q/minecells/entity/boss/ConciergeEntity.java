package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal.ShockwaveType;
import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
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

  public AnimationProperty leapChargeAnimation = new AnimationProperty(0.0F, MathUtils::easeOutQuad);
  public AnimationProperty leapReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveChargeAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveReleaseAnimation = new AnimationProperty(0.0F);

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
      settings.alignTick = 20;
      settings.actionTick = 40;
      settings.length = 60;
      settings.margin = 1.0;
      settings.particle = MineCellsParticles.SPECKLE.get(0xFF4000);
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
      settings.length = 50;
      settings.actionTick = 30;
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
    if (sharedCooldown > 0) sharedCooldown--;
  }

  @Override
  protected void processAnimations() {
    if (dataTracker.get(LEAP_CHARGING))
      leapChargeAnimation.setupTransitionTo(1F, 8F);
    else
      leapChargeAnimation.setupTransitionTo(0F, 3F);

    if (dataTracker.get(LEAP_RELEASING))
      leapReleaseAnimation.setupTransitionTo(1F, 12F);
    else
      leapReleaseAnimation.setupTransitionTo(0F, 20F);

    if (dataTracker.get(SHOCKWAVE_CHARGING))
      waveChargeAnimation.setupTransitionTo(1F, 10F);
    else
      waveChargeAnimation.setupTransitionTo(0F, 30F);

    if (dataTracker.get(SHOCKWAVE_RELEASING))
      waveReleaseAnimation.setupTransitionTo(1F, 10F, MathUtils::easeOutBack);
    else
      waveReleaseAnimation.setupTransitionTo(0F, 20F);
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
