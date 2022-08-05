package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SewersTentacleEntity extends MineCellsEntity {

  private static final EntityDimensions DIMENSIONS_BURIED = new EntityDimensions(0.7F, 0.1F, false);
  private static final EntityDimensions DIMENSIONS_UNBURIED = new EntityDimensions(0.7F, 2.0F, false);

  public final AnimationProperty belowGround = new AnimationProperty(-2.0F, AnimationProperty.EasingType.IN_OUT_QUAD);
  public final AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private static final TrackedData<Boolean> BURIED = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private int buriedTicks = 0;

  public SewersTentacleEntity(EntityType<SewersTentacleEntity> entityType, World world) {
    super(entityType, world);
    this.stepHeight = 1.0F;
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    float chance = this.random.nextFloat();
    int variant = 0;
    if (chance > 0.5F) {
      if (chance > 0.83F) {
        variant = 2;
      } else {
        variant = 1;
      }
    }
    this.dataTracker.startTracking(VARIANT, variant);
    this.dataTracker.startTracking(BURIED, true);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new TentacleAttackGoal(this, 1.2D));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));
  }

  @Override
  public void tick() {
    super.tick();
    if (this.world.isClient()) {
      this.spawnMovingParticles();
      if (this.isBuried()) {
        this.belowGround.setupTransitionTo(-2.0F, 20.0F);
        this.wobble.setupTransitionTo(0.0F, 15.0F);
      } else {
        this.belowGround.setupTransitionTo(0.0F, 10.0F);
        this.wobble.setupTransitionTo(1.0F, 20.0F);
      }
    } else {
      this.buriedTicks = this.isBuried() ? this.buriedTicks + 1 : 0;
    }
  }

  protected void spawnMovingParticles() {
    BlockState blockState = this.world.getBlockState(new BlockPos(this.getPos().subtract(0.0F, 0.01F, 0.0F)));
    if (blockState != null && blockState.isOpaque()) {
      ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState);
      ParticleUtils.addInBox(
        (ClientWorld) this.world,
        particle,
        Box.of(this.getPos().add(0.0D, 0.125D, 0.0D), 1.0D, 0.25D, 1.0D),
        this.isBuried() ? 10 : 5,
        new Vec3d(-0.01D, -0.01D, -0.01D)
      );
    }
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    if (damageSource.isExplosive() || damageSource.isOutOfWorld() || damageSource.isSourceCreativePlayer()) {
      return false;
    }
    if ((this.isBuried() && this.buriedTicks > 20) || damageSource == DamageSource.IN_WALL) {
      return true;
    }
    return super.isInvulnerableTo(damageSource);
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  @Override
  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  public static DefaultAttributeContainer.Builder createSewersTentacleAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 2.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
  }

  public int getVariant() {
    return this.dataTracker.get(VARIANT);
  }

  public void setVariant(int variant) {
    this.dataTracker.set(VARIANT, variant);
  }

  public boolean isBuried() {
    return this.dataTracker.get(BURIED);
  }

  public void setBuried(boolean buried) {
    this.dataTracker.set(BURIED, buried);
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("variant", this.getVariant());
    nbt.putBoolean("buried", this.isBuried());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.setVariant(nbt.getInt("variant"));
    this.setBuried(nbt.getBoolean("buried"));
  }

  public static class TentacleAttackGoal extends MeleeAttackGoal {
    private int ticks = 0;
    private boolean attacking = false;

    public TentacleAttackGoal(SewersTentacleEntity mob, double speed) {
      super(mob, speed, true);
    }

    @Override
    public void tick() {
      if (attacking) {
        if (ticks > 10) {
          ((SewersTentacleEntity) this.mob).setBuried(false);
          for (PlayerEntity player : this.mob.world.getPlayers(TargetPredicate.DEFAULT, this.mob, this.mob.getBoundingBox().expand(1.2D, 0.0D, 1.2D))) {
            this.mob.tryAttack(player);
          }
          if (ticks > 80) {
            this.attacking = false;
          }
        }
        ticks++;
      } else {
        ((SewersTentacleEntity) this.mob).setBuried(true);
        if (((SewersTentacleEntity) this.mob).buriedTicks > 20) {
          super.tick();
          if (this.mob.getTarget() != null && this.mob.getTarget().distanceTo(this.mob) <= 1.0D) {
            this.attacking = true;
            this.ticks = 0;
          }
        }
      }
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
      if (this.attacking) {
        super.attack(target, squaredDistance);
      }
    }

    @Override
    public void stop() {
      super.stop();
      this.ticks = 0;
      this.attacking = false;
      ((SewersTentacleEntity)this.mob).setBuried(true);
    }
  }
}
