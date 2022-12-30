package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.google.common.collect.HashMultimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SewersTentacleEntity extends MineCellsEntity {

  public final AnimationProperty belowGround = new AnimationProperty(-2.0F, AnimationProperty.EasingType.IN_OUT_QUAD);
  public final AnimationProperty wobble = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private static final TrackedData<Boolean> BURIED = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> SPAWNED_BY_BOSS = DataTracker.registerData(SewersTentacleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private static final HashMultimap<EntityAttribute, EntityAttributeModifier> bossModifiers = HashMultimap.create();

  static {
    bossModifiers.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier("SewersTentacleEntity.boss_armor", 5.0D, EntityAttributeModifier.Operation.ADDITION));
    bossModifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("SewersTentacleEntity.boss_damage", 4.0D, EntityAttributeModifier.Operation.ADDITION));
    bossModifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("SewersTentacleEntity.boss_speed", 0.15D, EntityAttributeModifier.Operation.ADDITION));
  }

  private int buriedTicks = 0;

  public SewersTentacleEntity(EntityType<SewersTentacleEntity> entityType, World world) {
    super(entityType, world);
    this.stepHeight = 1.0F;
    this.updateAttributeModifiers();
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(VARIANT, 0);
    this.dataTracker.startTracking(BURIED, true);
    this.dataTracker.startTracking(SPAWNED_BY_BOSS, false);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(0, new TentacleAttackGoal(this, 1.2D));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    this.updateAttributeModifiers();
    float chance = this.random.nextFloat();
    int variant = 0;
    if (chance > 0.5F) {
      if (chance > 0.83F) {
        variant = 2;
      } else {
        variant = 1;
      }
    }
    this.setVariant(variant);
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  @Override
  public void tick() {
    super.tick();
    if (this.world.isClient()) {
      this.spawnMovingParticles();
      if (this.isAlive()) {
        if (this.isBuried()) {
          this.belowGround.setupTransitionTo(-2.0F, 20.0F);
          this.wobble.setupTransitionTo(0.0F, 15.0F);
        } else {
          this.belowGround.setupTransitionTo(0.0F, 10.0F);
          this.wobble.setupTransitionTo(1.0F, 20.0F);
        }
      }
    }
    this.buriedTicks = this.isBuried() ? this.buriedTicks + 1 : 0;
  }

  @Override
  protected void updatePostDeath() {
    this.belowGround.setupTransitionTo(-2.0F, 10.0F);
    super.updatePostDeath();
  }

  private void updateAttributeModifiers() {
    this.getAttributes().removeModifiers(bossModifiers);
    if (this.isSpawnedByBoss()) {
      this.getAttributes().addTemporaryModifiers(bossModifiers);
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
  public boolean collidesWith(Entity other) {
    if (other instanceof SewersTentacleEntity) {
      return true;
    }
    if (this.isBuried()) {
      return false;
    }
    return super.collidesWith(other);
  }

  @Override
  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  public boolean isInvisible() {
    return this.isBuried() && (this.buriedTicks > 20 || this.age <= 20);
  }

  public static DefaultAttributeContainer.Builder createSewersTentacleAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 2.5D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 2.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
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

  public boolean isSpawnedByBoss() {
    return this.dataTracker.get(SPAWNED_BY_BOSS);
  }

  public void setSpawnedByBoss(boolean spawned) {
    this.dataTracker.set(SPAWNED_BY_BOSS, spawned);
    this.updateAttributeModifiers();
  }

  @Override
  protected SoundEvent getDeathSound() {
    return MineCellsSounds.SEWERS_TENTACLE_DEATH;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("variant", this.getVariant());
    nbt.putBoolean("buried", this.isBuried());
    nbt.putBoolean("spawnedByBoss", this.isSpawnedByBoss());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.setVariant(nbt.getInt("variant"));
    this.setBuried(nbt.getBoolean("buried"));
    this.setSpawnedByBoss(nbt.getBoolean("spawnedByBoss"));
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
        if (ticks > 15) {
          ((SewersTentacleEntity) this.mob).setBuried(false);
          for (PlayerEntity player : this.mob.world.getPlayers(TargetPredicate.DEFAULT, this.mob, this.mob.getBoundingBox().expand(0.75D, 0.0D, 0.75D))) {
            this.attack(player, player.squaredDistanceTo(this.mob));
          }
          if (ticks > 80) {
            this.attacking = false;
          }
        }
        ticks++;
      } else {
        ((SewersTentacleEntity) this.mob).setBuried(true);
        int maxBuriedTicks = ((SewersTentacleEntity) this.mob).isSpawnedByBoss() ? 40 : 20;
        if (((SewersTentacleEntity) this.mob).buriedTicks > maxBuriedTicks) {
          if (this.mob.getTarget() != null) {
            double x = this.mob.getTarget().getX() + (this.mob.getRandom().nextDouble() - 0.5D) * 3.0D;
            double z = this.mob.getTarget().getZ() + (this.mob.getRandom().nextDouble() - 0.5D) * 3.0D;
            this.mob.getMoveControl().moveTo(x, this.mob.getTarget().getY(), z, 1.0D);
            if (this.mob.getTarget().distanceTo(this.mob) <= 1.0D) {
              this.mob.playSound(MineCellsSounds.CHARGE, 1.0F, 1.0F);
              this.attacking = true;
              this.ticks = 0;
            }
          }
        }
      }
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
      if (this.attacking) {
        float damage = (float) this.mob.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        target.damage(DamageSource.mob(this.mob), damage);
        Vec3d diffNorm = this.mob.getPos().subtract(target.getPos()).normalize();
        target.takeKnockback(1.0D, diffNorm.x, diffNorm.z);
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
