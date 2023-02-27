package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.particle.colored.ColoredParticle;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShieldbearerEntity extends MineCellsEntity {

  private int dashCooldown = 100;

  private static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ShieldbearerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ShieldbearerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  public ShieldbearerEntity(EntityType<ShieldbearerEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    final TimedDashGoal<ShieldbearerEntity> dashGoal = new TimedDashGoal.Builder<>(this)
      .cooldownSetter((cooldown) -> this.dashCooldown = cooldown)
      .cooldownGetter(() -> this.dashCooldown)
      .stateSetter(this::switchDashState)
      .chargeSound(MineCellsSounds.SHIELDBEARER_CHARGE)
      .releaseSound(MineCellsSounds.SHIELDBEARER_RELEASE)
      .speed(0.5F)
      .onGround()
      .damage(8.0F)
      .defaultCooldown(120)
      .actionTick(20)
      .alignTick(18)
      .chance(0.2F)
      .length(60)
      .margin(0.25D)
      .particle(ColoredParticle.create(MineCellsParticles.SPECKLE, 0xFF0000))
      .build();
    super.initGoals();

    this.goalSelector.add(0, dashGoal);
    this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.0D, true, 1.0D));
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(DASH_CHARGING, false);
    this.dataTracker.startTracking(DASH_RELEASING, false);
  }

  @Override
  public void tick() {
    super.tick();
    if (this.dataTracker.get(DASH_CHARGING) && this.world.isClient()) {
      for (int i = 0; i < 5; i++) {
        ParticleUtils.addParticle((ClientWorld) this.world, MineCellsParticles.CHARGE, this.getPos().add(0.0D, this.getHeight() * 0.5D, 0.0D), Vec3d.ZERO);
      }
    }
    this.dashCooldown = Math.max(0, --this.dashCooldown);
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    EntityData result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    this.setLeftHanded(false);
    this.setStackInHand(Hand.MAIN_HAND, Items.SHIELD.getDefaultStack());
    return result;
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    Vec3d pos = damageSource.getPosition();
    if (pos != null) {
      Vec3d diff = pos.subtract(this.getPos());
      float angle = (float) MathHelper.atan2(diff.z, diff.x) * MathHelper.DEGREES_PER_RADIAN + 90.0F;
      if (MathHelper.angleBetween(this.bodyYaw, angle) > 110.0F) {
        this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 1.0F);
        return true;
      }
    }
    return super.isInvulnerableTo(damageSource);
  }

  @Override
  public boolean collidesWith(Entity other) {
    return super.collidesWith(other) && !this.dataTracker.get(DASH_RELEASING);
  }

  protected void switchDashState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(DASH_CHARGING, value);
      case RELEASE -> this.dataTracker.set(DASH_RELEASING, value);
    }
  }

  public static DefaultAttributeContainer.Builder createShieldbearerAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D)
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
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
}
