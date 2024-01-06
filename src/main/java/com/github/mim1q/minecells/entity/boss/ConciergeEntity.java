package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.ai.goal.*;
import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal.ShockwaveType;
import com.github.mim1q.minecells.entity.ai.goal.concierge.ConciergePunchGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.animation.AnimationProperty.EasingFunction;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

import static java.lang.Math.min;
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

  private static final TrackedData<Boolean> SCREAMING = DataTracker.registerData(ConciergeEntity.class, BOOLEAN);

  private int leapCooldown = 0;
  private int shockwaveCooldown = 0;
  private int auraCooldown = 0;
  private int punchCooldown = 0;
  private int sharedCooldown = 100;

  private BlockPos spawnPos = null;
  private int stage = 0;
  private int stageTimer = 20000;

  private boolean canAttack = false;
  private boolean goalsInit = false;

  public AnimationProperty leapChargeAnimation = new AnimationProperty(0.0F, MathUtils::easeOutQuad);
  public AnimationProperty leapReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveChargeAnimation = new AnimationProperty(0.0F);
  public AnimationProperty waveReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty punchChargeAnimation = new AnimationProperty(0.0F);
  public AnimationProperty punchReleaseAnimation = new AnimationProperty(0.0F);
  public AnimationProperty deathStartAnimation = new AnimationProperty(0.0F);
  public AnimationProperty deathFallAnimation = new AnimationProperty(0.0F);
  public AnimationProperty screamAnimation = new AnimationProperty(0.0F);

  private final Set<AnimationProperty> aliveAnimations = Set.of(
    leapChargeAnimation, leapReleaseAnimation, waveChargeAnimation, waveReleaseAnimation, punchChargeAnimation,
    punchReleaseAnimation, screamAnimation
  );

  public ConciergeEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    goalsInit = true;

    targetSelector.add(1, new RevengeGoal(this));
    targetSelector.add(0, new TargetRandomPlayerGoal<>(this));

    if (stage == 0) return;

    goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
    goalSelector.add(3, new WalkTowardsTargetGoal(this, 1.0 + stage * 0.15, false, 2.0));

    if (stage > 1) {
      goalSelector.add(0, new TimedDashGoal<>(this, settings -> {
          settings.onGround = true;
          settings.jumpHeight = 0.6;
          settings.cooldownGetter = () -> leapCooldown + sharedCooldown;
          settings.cooldownSetter = cooldown -> {
            leapCooldown = getStageAdjustedValue(8 * 20, 6 * 20, 4 * 20);
            sharedCooldown = getStageAdjustedValue(80, 60, 20);
          };
          settings.defaultCooldown = 8 * 20;
          settings.stateSetter = handleStateChange(LEAP_CHARGING, LEAP_RELEASING);
          settings.chargeSound = MineCellsSounds.CONCIERGE_LEAP_CHARGE;
          settings.landSound = MineCellsSounds.CONCIERGE_LEAP_LAND;
          settings.chance = 0.3F;
          settings.alignTick = getStageAdjustedValue(30, 30, 25);
          settings.actionTick = getStageAdjustedValue(40, 35, 25);
          settings.speed = 1.25F;
          settings.minDistance = 5.0;
          settings.overshoot = 1.5;
          settings.length = 60;
          settings.margin = 1.0;
          settings.damage = 10.0F;
          settings.particle = MineCellsParticles.SPECKLE.get(0xFF4000);
        }, e -> e.canAttack()
          && e.getTarget() != null
          && e.distanceTo(e.getTarget()) > 8 || stage == 3)
      );
    }

    goalSelector.add(0, new ShockwaveGoal<>(this, settings -> {
      settings.cooldownGetter = () -> shockwaveCooldown + sharedCooldown;
      settings.cooldownSetter = cooldown -> {
        shockwaveCooldown = getStageAdjustedValue(10 * 20, 9 * 20, 8 * 20);
        sharedCooldown = getStageAdjustedValue(100, 80, 20);
      };
      settings.defaultCooldown = 8 * 20;
      settings.stateSetter = handleStateChange(SHOCKWAVE_CHARGING, SHOCKWAVE_RELEASING);
      settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME;
      settings.shockwaveRadius = 20;
      settings.shockwaveInterval = getStageAdjustedValue(2.0F, 1.5F, 1.0F);
      settings.shockwaveType = ShockwaveType.CIRCLE;
      settings.chargeSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_CHARGE;
      settings.releaseSound = MineCellsSounds.CONCIERGE_SHOCKWAVE_RELEASE;
      settings.chance = 0.2F;
      settings.length = 50;
      settings.shockwaveDamage = 6.0F;
      settings.actionTick = 30;
    }, ConciergeEntity::canAttack));

    goalSelector.add(0, new TimedAuraGoal<>(this, settings -> {
      settings.cooldownGetter = () -> auraCooldown + sharedCooldown;
      settings.cooldownSetter = cooldown -> {
        auraCooldown = getStageAdjustedValue(20 * 20, 16 * 20, 8 * 20);
        sharedCooldown = getStageAdjustedValue(40, 40, 0);
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
      settings.cooldownSetter = cooldown -> punchCooldown = getStageAdjustedValue(80, 40, 10);
      settings.defaultCooldown = 4 * 20;
      settings.stateSetter = handleStateChange(PUNCH_CHARGING, PUNCH_RELEASING);
      settings.chargeSound = MineCellsSounds.CONCIERGE_PUNCH_CHARGE;
      settings.releaseSound = MineCellsSounds.CONCIERGE_PUNCH_RELEASE;
      settings.length = 30;
      settings.actionTick = 20;
      settings.damage = 10.0F;
      settings.knockback = getStageAdjustedValue(2.0, 3.0, 4.0);
    }));
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
    dataTracker.startTracking(SCREAMING, false);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    spawnPos = this.getBlockPos();
    setYaw(180F);
    setBodyYaw(180F);
    prevYaw = prevBodyYaw = prevHeadYaw = 180F;
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
    } else {
      if (spawnPos != null && getTarget() == null && canChangeStage()) {
        getNavigation().startMovingTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 1.3);
      }
      stageTimer++;
      if (stage == 0 && getTarget() != null) setStage(1);
      if (stage == 1 && getHealth() / getMaxHealth() <= 0.66 && canChangeStage()) setStage(2);
      if (stage == 2 && getHealth() / getMaxHealth() <= 0.33 && canChangeStage()) setStage(3);
      dataTracker.set(SCREAMING, stageTimer <= 60);
      canAttack = true;
      if (stageTimer <= 80) {
        getNavigation().stop();
        var nearestPlayer = getWorld().getClosestPlayer(this, 100);
        if (nearestPlayer != null) {
          getLookControl().lookAt(nearestPlayer, 360F, 360F);
        }
        canAttack = false;
      } else if (!goalsInit && isAlive()) {
        this.initGoals();
      }
    }
  }

  private void setStage(int stage) {
    if (this.stage == stage) return;
    this.stage = stage;
    this.stageTimer = 0;
    clearGoals();
    addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.PROTECTED, 100, 0, false, false, false));
    playSound(MineCellsSounds.CONCIERGE_SHOUT, 2F, 1F);
  }

  private void clearGoals() {
    this.goalsInit = false;
    goalSelector.getGoals().forEach(PrioritizedGoal::stop);
    goalSelector.clear(Objects::nonNull);
  }

  @Override
  protected void updatePostDeath() {
    clearGoals();
    ++this.deathTime;
    if (this.getWorld().isClient()) {
      aliveAnimations.forEach(animation -> animation.setupTransitionTo(0, 5, MathHelper::lerp));
      deathStartAnimation.setupTransitionTo(1, 30);
      if (this.deathTime >= 60) {
        deathFallAnimation.setupTransitionTo(1, 60);
      }
      if (this.deathTime >= 155) {
        getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() - 1.0, getZ(), 0.0D, 0.0D, 0.0D);
      }
    } else {
      if (this.deathTime == 15) playSound(MineCellsSounds.CONCIERGE_LEAP_LAND, 0.8F, 1.1F);
      if (this.deathTime == 85) playSound(MineCellsSounds.CONCIERGE_LEAP_LAND, 1F, 0.1F);
      if (this.deathTime >= 160 && !this.isRemoved()) {
        playSound(MineCellsSounds.CONJUNCTIVIUS_DEATH, 0.8F, 0.9F);
        this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
        this.remove(Entity.RemovalReason.KILLED);
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
    if (isDead()) return;

    setupTransition(LEAP_CHARGING, leapChargeAnimation, 8F, 3F, MathUtils::lerp);
    setupTransition(LEAP_RELEASING, leapReleaseAnimation, 12F, 20F, MathUtils::lerp);
    setupTransition(SHOCKWAVE_CHARGING, waveChargeAnimation, 10F, 30F, MathUtils::lerp);
    setupTransition(SHOCKWAVE_RELEASING, waveReleaseAnimation, 10F, 20F, MathUtils::easeOutBack);
    setupTransition(PUNCH_CHARGING, punchChargeAnimation, 10F, 3F, MathUtils::easeOutQuad);
    setupTransition(PUNCH_RELEASING, punchReleaseAnimation, 10F, 6F, MathUtils::easeOutBack);
    setupTransition(SCREAMING, screamAnimation, 5F, 20F, MathUtils::easeOutQuad);
  }

  private boolean canChangeStage() {
    return !dataTracker.get(LEAP_CHARGING)
      && !dataTracker.get(LEAP_RELEASING)
      && !dataTracker.get(SHOCKWAVE_CHARGING)
      && !dataTracker.get(SHOCKWAVE_RELEASING)
      && !dataTracker.get(PUNCH_CHARGING)
      && !dataTracker.get(PUNCH_RELEASING);
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

  @Override
  public boolean damage(DamageSource source, float amount) {
    var percentage = getStageAdjustedValue(1.0F, 0.75F, 0.5F);
    return super.damage(source, amount * percentage);
  }

  public boolean canAttack() {
    return canAttack && !isDead();
  }

  @SafeVarargs
  private <T> T getStageAdjustedValue(T... stages) {
    return stages[min(stage, stages.length - 1)];
  }

  @Override
  protected void playStepSound(BlockPos pos, BlockState state) {
    super.playStepSound(pos, state);
    playSound(MineCellsSounds.CONCIERGE_STEP, 0.8F, random.nextFloat() * 0.2F + 0.8F);
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    super.onDeath(damageSource);
    if (getWorld().isClient || getWorld().getServer() == null) {
      return;
    }

    var server = getWorld().getServer();
    var advancement = server.getAdvancementLoader().get(MineCells.createId("concierge"));

    if (advancement == null) {
      return;
    }

    getWorld().getEntitiesByClass(PlayerEntity.class, Box.of(getPos(), 128, 128, 128), Objects::nonNull).forEach(
      player -> ((ServerPlayerEntity) player).getAdvancementTracker().grantCriterion(advancement, "concierge_killed")
    );
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
    nbt.putInt("stage", stage);
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
    stage = nbt.getInt("stage");
  }

  public static DefaultAttributeContainer.Builder createConciergeAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
  }
}
