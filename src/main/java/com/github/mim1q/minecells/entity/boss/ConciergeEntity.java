package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.ai.goal.*;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal.ShockwaveType;
import com.github.mim1q.minecells.entity.ai.goal.concierge.ConciergePunchGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.animation.AnimationProperty.EasingFunction;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN;

public class ConciergeEntity extends MineCellsBossEntity {
  private static final TrackedData<Boolean> LEAP_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> LEAP_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private static final TrackedData<Boolean> SHOCKWAVE_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> SHOCKWAVE_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private static final TrackedData<Boolean> PUNCH_CHARGING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> PUNCH_RELEASING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private int leapCooldown = 0;
  private int shockwaveCooldown = 0;
  private int auraCooldown = 0;
  private int punchCooldown = 0;
  private int sharedCooldown = 100;

  private BlockPos spawnPos = null;

  public AnimationProperty leapChargeAnimation = new AnimationProperty(0.0F, MathUtils::easeOutQuad);
  public AnimationProperty leapReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveChargeAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty punchChargeAnimation = new AnimationProperty(0.0F);
  public AnimationProperty punchReleaseAnimation = new AnimationProperty(0.0F);

  public ConciergeEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
    goalSelector.add(3, new WalkTowardsTargetGoal(this, 1.0, false, 2.0));

    goalSelector.add(0, new TimedDashGoal<>(this, settings -> {
      settings.onGround = false;
      settings.cooldownGetter = () -> leapCooldown + sharedCooldown;
      settings.cooldownSetter = cooldown -> {
        leapCooldown = getAdjustedCooldown(cooldown);
        sharedCooldown = getAdjustedCooldown(100);
      };
      settings.defaultCooldown = 8 * 20;
      settings.stateSetter = handleStateChange(LEAP_CHARGING, LEAP_RELEASING);
      settings.chargeSound = MineCellsSounds.CONCIERGE_LEAP_CHARGE;
      settings.landSound = MineCellsSounds.CONCIERGE_LEAP_LAND;
      settings.chance = 0.3F;
      settings.alignTick = 35;
      settings.actionTick = 40;
      settings.minDistance = 5.0;
      settings.overshoot = 1.5;
      settings.length = 60;
      settings.margin = 1.0;
      settings.particle = MineCellsParticles.SPECKLE.get(0xFF4000);
    }, e -> e.getTarget() != null && e.distanceTo(e.getTarget()) > 8 || getHealth() / getMaxHealth() <= 0.25));

    goalSelector.add(0, new ShockwaveGoal<>(this, settings -> {
      settings.cooldownGetter = () -> shockwaveCooldown + sharedCooldown;
      settings.cooldownSetter = cooldown -> {
        shockwaveCooldown = getAdjustedCooldown(cooldown);
        sharedCooldown = getAdjustedCooldown(100);
      };
      settings.defaultCooldown = 8 * 20;
      settings.stateSetter = handleStateChange(SHOCKWAVE_CHARGING, SHOCKWAVE_RELEASING);
      settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME;
      settings.shockwaveRadius = 20;
      settings.shockwaveInterval = 2;
      settings.shockwaveType = ShockwaveType.CIRCLE;
      settings.chargeSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_CHARGE;
      settings.releaseSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_RELEASE;
      settings.chance = 0.2F;
      settings.length = 50;
      settings.actionTick = 30;
    }, null));

    goalSelector.add(0, new TimedAuraGoal<>(this, settings -> {
      settings.cooldownGetter = () -> auraCooldown + sharedCooldown;
      settings.cooldownSetter = cooldown -> {
        auraCooldown = getAdjustedCooldown(cooldown);
        sharedCooldown = getAdjustedCooldown(40);
      };
      settings.defaultCooldown = 30 * 20;
      settings.stateSetter = handleStateChange(AURA_CHARGING, AURA_RELEASING);
      settings.chargeSound = MineCellsSounds.CONCIERGE_AURA_CHARGE;
      settings.endSound = MineCellsSounds.CONCIERGE_AURA_RELEASE;
      settings.length = 120;
      settings.actionTick = 60;
      settings.radius = 4.0;
      settings.damage = 6.0F;
      settings.chance = 0.02F;
    }, e -> e.getWorld().getClosestPlayer(e, 5.0) != null));

    goalSelector.add(0, new ConciergePunchGoal(this, settings -> {
      settings.cooldownGetter = () -> punchCooldown;
      settings.cooldownSetter = cooldown -> punchCooldown = getAdjustedCooldown(cooldown);
      settings.defaultCooldown = 4 * 20;
      settings.stateSetter = handleStateChange(PUNCH_CHARGING, PUNCH_RELEASING);
      settings.chargeSound = MineCellsSounds.CONCIERGE_PUNCH_CHARGE;
      settings.releaseSound = MineCellsSounds.CONCIERGE_PUNCH_RELEASE;
      settings.length = 30;
      settings.actionTick = 20;
      settings.damage = 10.0F;
    }));

    targetSelector.add(0, new TargetRandomPlayerGoal<>(this));
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    dataTracker.startTracking(LEAP_CHARGING, false);
    dataTracker.startTracking(LEAP_RELEASING, false);
    dataTracker.startTracking(SHOCKWAVE_CHARGING, false);
    dataTracker.startTracking(SHOCKWAVE_RELEASING, false);
    dataTracker.startTracking(AURA_CHARGING, false);
    dataTracker.startTracking(AURA_RELEASING, false);
    dataTracker.startTracking(PUNCH_CHARGING, false);
    dataTracker.startTracking(PUNCH_RELEASING, false);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    spawnPos = this.getBlockPos();
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient()) {
      var pos = getPos().add(0.0, 1.0, 0.0);
      if (dataTracker.get(AURA_CHARGING)) {
        ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 5, 4.5D, -0.03D);
      } else if (dataTracker.get(AURA_RELEASING)) {
        ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 40, 4.0D, 0.01D);
        ParticleUtils.addAura((ClientWorld) getWorld(), pos, MineCellsParticles.AURA, 10, 1.0D, 0.3D);
      }
    }
  }

  @Override
  protected void decrementCooldowns() {
    leapCooldown--;
    shockwaveCooldown--;
    auraCooldown--;
    punchCooldown--;
    if (sharedCooldown > 0) sharedCooldown--;
  }

  @Override
  protected void processAnimations() {
    setupTransition(LEAP_CHARGING, leapChargeAnimation, 8F, 3F, MathUtils::lerp);
    setupTransition(LEAP_RELEASING, leapReleaseAnimation, 12F, 20F, MathUtils::lerp);
    setupTransition(SHOCKWAVE_CHARGING, waveChargeAnimation, 10F, 30F, MathUtils::lerp);
    setupTransition(SHOCKWAVE_RELEASING, waveReleaseAnimation, 10F, 20F, MathUtils::easeOutBack);
    setupTransition(PUNCH_CHARGING, punchChargeAnimation, 10F, 3F, MathUtils::easeOutQuad);
    setupTransition(PUNCH_RELEASING, punchReleaseAnimation, 10F, 6F, MathUtils::easeOutBack);
  }

  private void setupTransition(
    TrackedData<Boolean> data,
    AnimationProperty animation,
    float onDuration,
    float offDuration,
    EasingFunction onEasing
  ) {
    if (dataTracker.get(data)) {
      animation.setupTransitionTo(1F, onDuration, onEasing);
    } else {
      animation.setupTransitionTo(0F, offDuration);
    }
  }

  @Override
  public void onDamaged(DamageSource damageSource) {
    super.onDamaged(damageSource);
    limbAnimator.setSpeed(0.5F);
  }

  private int getAdjustedCooldown(int cooldown) {
    var healthPercent = getHealth() / getMaxHealth();
    if (healthPercent > 0.75) {
      return cooldown;
    }
    if (healthPercent > 0.5) {
      return (int) (cooldown * 0.75);
    }
    if (healthPercent > 0.25) {
      return cooldown / 2;
    }
    return cooldown / 4;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("leapCooldown", leapCooldown);
    nbt.putInt("shockwaveCooldown", shockwaveCooldown);
    nbt.putInt("auraCooldown", auraCooldown);
    nbt.putInt("punchCooldown", punchCooldown);
    nbt.putInt("sharedCooldown", sharedCooldown);
    if (spawnPos != null) {
      nbt.putLong("spawnPos", spawnPos.asLong());
    }
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    leapCooldown = nbt.getInt("leapCooldown");
    shockwaveCooldown = nbt.getInt("shockwaveCooldown");
    auraCooldown = nbt.getInt("auraCooldown");
    punchCooldown = nbt.getInt("punchCooldown");
    sharedCooldown = nbt.getInt("sharedCooldown");
    if (nbt.contains("spawnPos")) {
      spawnPos = BlockPos.fromLong(nbt.getLong("spawnPos"));
    }
  }

  public static DefaultAttributeContainer.Builder createConciergeAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
  }
}
