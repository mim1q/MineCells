package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShockwaveGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.util.MathUtils.radians;
import static java.lang.Math.abs;

public class SweeperEntity extends MineCellsEntity {
  private int sweepCooldown = 0;
  private Vec3d gauntletPosition = this.getPos();
  private static final Vec3d GAUNTLET_OFFSET = new Vec3d(-0.75D, 0.2D, -0.9D);

  public final AnimationProperty sweepCharge = new AnimationProperty(0F);
  public final AnimationProperty sweepRelease = new AnimationProperty(0F, MathUtils::easeOutBack);

  private static final TrackedData<Boolean> SWEEP_CHARGING = DataTracker.registerData(SweeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> SWEEP_RELEASING = DataTracker.registerData(SweeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  public SweeperEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new SweeperMoveControl(this);
  }

  @Override
  protected void initGoals() {
    super.initGoals();
    goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.0, false, 5.0));
    goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 16));
    goalSelector.add(0, new ShockwaveGoal<>(
      this,
      settings -> {
        settings.cooldownGetter = () -> {
          if (this.getTarget() != null && this.squaredDistanceTo(this.getTarget()) < 9.0) {
            return sweepCooldown - 20;
          }
          return sweepCooldown;
        };
        settings.cooldownSetter = (cooldown) -> sweepCooldown = cooldown;
        settings.defaultCooldown = 40;
        settings.chance = 0.1F;
        settings.stateSetter = (state, value) -> this.handleStateChange(state, value, SWEEP_CHARGING, SWEEP_RELEASING);
        settings.actionTick = 20;
        settings.length = 40;
        settings.chargeSound = MineCellsSounds.SWEEPER_CHARGE;
        settings.releaseSound = MineCellsSounds.SWEEPER_RELEASE;
        settings.shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME;
        settings.shockwaveDamage = 6.0F;
        settings.shockwaveInterval = 1F;
      },
      e -> e.getTarget() != null
        && abs(e.getTarget().getY() - e.getY()) < 2.0
        && e.navigation.getCurrentPath() != null
        && e.navigation.getCurrentPath().reachesTarget()
    ));
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    dataTracker.startTracking(SWEEP_CHARGING, false);
    dataTracker.startTracking(SWEEP_RELEASING, false);
  }

  @Override
  public void tick() {
    super.tick();

    this.sweepCooldown--;

    if (getWorld().isClient) {
      var lastGauntletPosition = gauntletPosition;
      gauntletPosition = getPos().add(MathUtils.vectorRotateY(GAUNTLET_OFFSET, radians(this.getBodyYaw())));
      spawnGauntletParticles(lastGauntletPosition.squaredDistanceTo(gauntletPosition) > 0.0001D);

      if (dataTracker.get(SWEEP_CHARGING)) {
        sweepCharge.setupTransitionTo(1F, 10F);
      } else {
        sweepCharge.setupTransitionTo(0F, 8F);
      }
      if (dataTracker.get(SWEEP_RELEASING)) {
        sweepRelease.setupTransitionTo(1F, 15F, MathUtils::easeOutBack);
      } else {
        sweepRelease.setupTransitionTo(0F, 20F, MathUtils::easeInOutQuad);
      }
    }
  }

  private void spawnGauntletParticles(boolean moving) {
    var chance = moving ? 1F : 0.1F;
    if (random.nextFloat() < chance) {
      var randomOffset = new Vec3d(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).multiply(0.5);
      var pos = gauntletPosition.add(randomOffset);
      getWorld().addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0.01, 0);
    }
    if (moving) {
      var randomOffset = new Vec3d(random.nextDouble() - 0.5, 0.0, random.nextDouble() - 0.5).multiply(0.5);
      var pos = gauntletPosition.add(randomOffset);
      getWorld().addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0, 0.01, 0);
    }
  }

  public static DefaultAttributeContainer.Builder createSweeperAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.19D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D);
  }

  private static class SweeperMoveControl extends MoveControl {
    public SweeperMoveControl(MobEntity entity) {
      super(entity);
    }

    @Override
    public void tick() {
      if (this.entity.age % 30 == 0 && this.state == State.MOVE_TO) {
        this.entity.playSound(SoundEvents.BLOCK_GRINDSTONE_USE, 1.0F, 1.0F);
      }
      if (this.entity.age % 30 <= 15) {
        super.tick();
      } else {
        this.entity.setMovementSpeed(0F);
      }
    }
  }
}
