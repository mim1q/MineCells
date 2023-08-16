package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlyEntity extends MineCellsEntity {
  private int biteCooldown = 0;
  public static final TrackedData<Boolean> BITE_CHARGING = DataTracker.registerData(FlyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> BITE_RELEASING = DataTracker.registerData(FlyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  public final AnimationProperty bite = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

  public FlyEntity(EntityType<? extends FlyEntity> entityType, World world) {
    super(entityType, world);

    this.setNoGravity(true);
    this.moveControl = new FlightMoveControl(this, 20, true);
  }

  @Override
  protected void initGoals() {
    super.initGoals();

    this.goalSelector.add(0, new BiteAttack(
      this,
      settings -> {
        settings.cooldownGetter = () -> this.biteCooldown;
        settings.cooldownSetter = (cooldown) -> this.biteCooldown = cooldown * (int)(1F + this.random.nextFloat() * 0.25F);
        settings.stateSetter = (state, value) -> this.handleStateChange(state, value, BITE_CHARGING, BITE_RELEASING);
        settings.actionTick = 15;
        settings.length = 25;
        settings.defaultCooldown = 50;
        settings.chance = 0.2F;
        settings.chargeSound = MineCellsSounds.FLY_CHARGE;
        settings.releaseSound = MineCellsSounds.FLY_RELEASE;
      },
      null
    ));

    this.goalSelector.add(4, new FlyGoal(this, 1.0D));
    this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 3.0D, false));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(BITE_CHARGING, false);
    this.dataTracker.startTracking(BITE_RELEASING, false);
  }

  @Override
  public void tick() {
    super.tick();
    if (this.getWorld().isClient) {
      if (this.dataTracker.get(BITE_CHARGING)) {
        bite.setupTransitionTo(1.0F, 8.0F, MathUtils::easeOutBack);
      } else if (this.dataTracker.get(BITE_RELEASING)) {
        bite.setupTransitionTo(0.0F, 4.0F, MathUtils::easeInOutQuad);
      }
    } else {
      this.biteCooldown = Math.max(0, this.biteCooldown - 1);
    }
  }

  @Override
  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  @Override
  public boolean isPushable() {
    return !this.dataTracker.get(BITE_CHARGING);
  }

  @Nullable
  @Override
  protected SoundEvent getAmbientSound() {
    return MineCellsSounds.FLY_FLY;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("biteCooldown", this.biteCooldown);
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.biteCooldown = nbt.getInt("biteCooldown");
  }

  @Override
  protected EntityNavigation createNavigation(World world) {
    BirdNavigation birdNavigation = new BirdNavigation(this, world) {
      @Override
      public boolean isValidPosition(BlockPos pos) {
        return !this.world.getBlockState(pos.down()).isAir();
      }

      @Override
      public Path findPathTo(Entity entity, int distance) {
        return this.findPathTo(entity.getX(), entity.getY() + 1.5, entity.getZ(), distance);
      }
    };
    birdNavigation.setCanPathThroughDoors(false);
    birdNavigation.setCanSwim(false);
    birdNavigation.setCanEnterOpenDoors(true);
    return birdNavigation;
  }

  public static DefaultAttributeContainer.Builder createFlyAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
      .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.33D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0D);
  }

  private static class BiteAttack extends TimedActionGoal<FlyEntity> {
    protected BiteAttack(FlyEntity entity, Consumer<TimedActionSettings> settingsConsumer, Predicate<FlyEntity> predicate) {
      super(entity, settingsConsumer, predicate);
      this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
      var target = entity.getTarget();
      return super.canStart() && target != null && target.squaredDistanceTo(entity) < 4.0D;
    }

    @Override
    public void start() {
      super.start();
      this.entity.lookAtEntity(this.entity.getTarget(), 180.0F, 180.0F);
      this.entity.getNavigation().stop();
    }

    @Override
    protected void runAction() {
      var target = entity.getTarget();
      if (target != null && target.squaredDistanceTo(entity) < 6.0D) {
        target.damage(target.getWorld().getDamageSources().mobAttack(entity), (float) entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
      }
    }
  }
}
