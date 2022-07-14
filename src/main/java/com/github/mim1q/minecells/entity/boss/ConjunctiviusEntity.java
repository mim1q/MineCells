package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConjunctiviusEntity extends MineCellsBossEntity {

  public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private Vec3d spawnPos = Vec3d.ZERO;

  private int dashCooldown = 0;

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new ConjunctiviusMoveControl(this);
    this.navigation = new BirdNavigation(this, this.world);
    this.setNoGravity(true);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    this.spawnPos = this.getPos();
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(DASH_RELEASING, false);
    this.dataTracker.startTracking(DASH_CHARGING, false);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(1, new TimedDashGoal.Builder<>(this)
      .cooldownSetter((cooldown) -> this.dashCooldown = cooldown)
      .cooldownGetter(() -> this.dashCooldown)
      .stateSetter(this::switchDashState)
      .chargeSound(SoundRegistry.SHIELDBEARER_CHARGE)
      .releaseSound(SoundRegistry.SHIELDBEARER_RELEASE)
      .speed(1.0F)
      .damage(20.0F)
      .defaultCooldown(100)
      .actionTick(40)
      .chance(0.5F)
      .length(80)
      .build()
    );

    this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
  }

  @Override
  public void tick() {
    super.tick();
    if (this.world.isClient()) {
      if (this.dataTracker.get(DASH_CHARGING) || this.dataTracker.get(DASH_RELEASING)) {
        this.spikeOffset.setupTransitionTo(0.0F, 20.0F);
      } else {
        this.spikeOffset.setupTransitionTo(5.0F, 40.0F);
      }
    } else {
      for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox().contract(0.25D))) {
        if (e instanceof LivingEntity) {
          this.tryAttack(e);
          e.pushAwayFrom(this);
        }
      }
      if (this.getPos().distanceTo(this.spawnPos) > 1.0D) {
        //this.move(MovementType.SELF, this.spawnPos.subtract(this.getPos()).normalize());
      }
      if (!this.dataTracker.get(DASH_RELEASING) && this.getTarget() != null) {
        this.getLookControl().lookAt(this.getTarget(), 5.0F, 5.0F);
      }
      if (!this.world.getBlockState(this.getBlockPos().down()).isAir()) {
        this.move(MovementType.SELF, new Vec3d(0.0D, 0.1D, 0.0D));
      }
    }
    this.bodyYaw = this.headYaw;
    this.prevBodyYaw = this.prevHeadYaw;
  }

  @Override
  protected void decrementCooldowns() {
    this.dashCooldown = Math.max(0, this.dashCooldown - 1);
  }

  protected void switchDashState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(DASH_CHARGING, value);
      case RELEASE -> this.dataTracker.set(DASH_RELEASING, value);
    }
  }

  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 32.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 32.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  //region flying

  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
  }

  public void travel(Vec3d movementInput) {
    if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
      this.updateVelocity(0.02F, movementInput);
      this.move(MovementType.SELF, this.getVelocity());
      this.setVelocity(this.getVelocity().multiply(0.9D));
    }
  }

  public boolean isClimbing() {
    return false;
  }
  //endregion


  @Override
  public int getMaxHeadRotation() {
    return 360;
  }

  protected static class ConjunctiviusMoveControl extends MoveControl {

    private final int collisionCheckCooldown = 0;

    public ConjunctiviusMoveControl(ConjunctiviusEntity entity) {
      super(entity);
    }

    public void tick() {
      if (this.state == State.MOVE_TO) {
        Vec3d vec3d = new Vec3d(this.targetX - this.entity.getX(), this.targetY - this.entity.getY(), this.targetZ - this.entity.getZ());
        double d = vec3d.length();
        vec3d = vec3d.normalize();
        if (!this.willCollide(vec3d, MathHelper.ceil(d))) {
          this.state = State.WAIT;
        }
      }
    }

    private boolean willCollide(Vec3d direction, int steps) {
      Box box = this.entity.getBoundingBox().withMinY(this.entity.getY() - 1.0D);
      for(int i = 1; i < steps; ++i) {
        box = box.offset(direction);
        if (!this.entity.world.isSpaceEmpty(this.entity, box)) {
          return false;
        }
      }
      return true;
    }
  }
}
