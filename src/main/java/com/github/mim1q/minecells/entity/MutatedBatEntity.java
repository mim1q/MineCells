package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MutatedBatEntity extends MineCellsEntity {

  private static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(MutatedBatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(MutatedBatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private int dashCooldown = 80;

  public MutatedBatEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new FlightMoveControl(this, 0, true);
    this.setNoGravity(true);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    this.setPosition(this.getPos().add(0.0D, 3.0D, 0.0D));
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  @Override
  public void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(DASH_CHARGING, false);
    this.dataTracker.startTracking(DASH_RELEASING, false);
  }

  @Override
  protected void initGoals() {
    final var dashGoal = new TimedDashGoal<>(this, (s) -> {
      s.cooldownSetter = (cooldown) -> this.dashCooldown = cooldown;
      s.cooldownGetter = () -> this.dashCooldown;
      s.stateSetter = this::switchDashState;
      s.chargeSound = MineCellsSounds.MUTATED_BAT_CHARGE;
      s.releaseSound = MineCellsSounds.MUTATED_BAT_RELEASE;
      s.speed = 0.8F;
      s.damage = 6.0F;
      s.defaultCooldown = 80;
      s.actionTick = 20;
      s.alignTick = 19;
      s.chance = 0.075F;
      s.length = 60;
      s.margin = 0.25D;
      s.particle = MineCellsParticles.SPECKLE.get(0xFF0000);
    }, mob -> mob.distanceTo(mob.getTarget()) < 8.0D);

    this.goalSelector.add(4, new FlyGoal(this, 1.0D));
    this.goalSelector.add(0, dashGoal);
    this.goalSelector.add(1, new MeleeAttackGoal(this, 3.0D, false));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));
  }

  @Override
  public void tick() {
    super.tick();
    if (this.dataTracker.get(DASH_CHARGING) && getWorld().isClient()) {
      for (int i = 0; i < 5; i++) {
        ParticleUtils.addParticle((ClientWorld) getWorld(), MineCellsParticles.CHARGE, this.getPos().add(0.0D, 0.2D, 0.0D), Vec3d.ZERO);
      }
    }
    this.dashCooldown = Math.max(0, --this.dashCooldown);
  }

  @Override
  protected EntityNavigation createNavigation(World world) {
    return new MutatedBatNavigation(this, world);
  }

  @Override
  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  protected void switchDashState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(DASH_CHARGING, value);
      case RELEASE -> this.dataTracker.set(DASH_RELEASING, value);
    }
  }

  public static DefaultAttributeContainer.Builder createMutatedBatAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
      .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0D);
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("dashCooldown", dashCooldown);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    dashCooldown = nbt.getInt("dashCooldown");
  }

  public static class MutatedBatNavigation extends BirdNavigation {
    public MutatedBatNavigation(HostileEntity host, World world) {
      super(host, world);
    }

    @Override
    public Path findPathTo(Entity entity, int distance) {
      return this.findPathTo(entity.getBlockPos().add(0, 2, 0), distance);
    }
  }
}
