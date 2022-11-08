package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MineCellsEntity extends HostileEntity {

  protected MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(10, new LookAroundGoal(this));
    this.goalSelector.add(8, new WanderAroundGoal(this, 1.0D));

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));
  }

  @Override
  public void tick() {
    super.tick();
    if (!this.world.isClient) {
      this.decrementCooldowns();
    }
  }

  public void setCellAmountAndChance(int amount, float chance) {
    ((LivingEntityAccessor) this).mixinSetCellAmountAndChance(amount, chance);
  }

  public float getPathfindingFavor(BlockPos pos, WorldView world) {
    return 0.0F;
  }

  @Override
  public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
    BlockState blockBelow = world.getBlockState(this.getBlockPos().down());
    return blockBelow.isSideSolidFullSquare(world, this.getBlockPos().down(), Direction.UP)
      && blockBelow.getBlock() != Blocks.BEDROCK;
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    if (damageSource instanceof GrenadeEntity.GrenadeDamageSource) {
      return true;
    }
    return super.isInvulnerableTo(damageSource);
  }

  protected void decrementCooldown(TrackedData<Integer> cooldown) {
    int current = this.dataTracker.get(cooldown);
    if (current > 0) {
      this.dataTracker.set(cooldown, current - 1);
    }
  }

  protected TimedActionGoal.State getStateFromTrackedData(TrackedData<Boolean> charging, TrackedData<Boolean> releasing) {
    if (this.dataTracker.get(charging)) {
      return TimedActionGoal.State.CHARGE;
    }
    if (this.dataTracker.get(releasing)) {
      return TimedActionGoal.State.RELEASE;
    }
    return TimedActionGoal.State.IDLE;
  }

  protected void decrementCooldowns() {
  }

  @Override
  protected SoundEvent getDeathSound() {
    return MineCellsSounds.LEAPING_ZOMBIE_DEATH;
  }
}
