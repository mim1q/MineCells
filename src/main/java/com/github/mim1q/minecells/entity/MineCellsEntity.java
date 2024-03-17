package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal.State;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Objects;
import java.util.function.BiConsumer;

import static net.minecraft.entity.data.DataTracker.registerData;
import static net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN;

public class MineCellsEntity extends HostileEntity {
  public static final float ELITE_SCALE = 1.25F;
  private static final TrackedData<Boolean> IS_ELITE = registerData(MineCellsEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> ELITE_AURA_CHARGING = registerData(MineCellsEntity.class, BOOLEAN);
  private static final TrackedData<Boolean> ELITE_AURA_RELEASING = registerData(MineCellsEntity.class, BOOLEAN);
  public static final TrackedData<Boolean> FOR_DISPLAY = registerData(MineCellsEntity.class, BOOLEAN);

  public BlockPos spawnRunePos = null;
  private boolean recalculatedDimensions = false;
  private Identifier additionalLootTable = null;

  private int eliteAttackCooldown = 100;

  protected MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(10, new LookAroundGoal(this));
    this.goalSelector.add(8, new WanderAroundGoal(this, 1.0D));
    if (isElite()) {
      this.goalSelector.add(
        0,
        new TimedAuraGoal<>(this, settings -> {
          settings.radius = 2.6D;
          settings.defaultCooldown = 80;
          settings.cooldownGetter = () -> eliteAttackCooldown;
          settings.cooldownSetter = value -> eliteAttackCooldown = value + this.random.nextInt(40);
          settings.stateSetter = handleStateChange(ELITE_AURA_CHARGING, ELITE_AURA_RELEASING);
          settings.chargeSound = MineCellsSounds.SHOCKER_CHARGE;
          settings.releaseSound = MineCellsSounds.SHOCKER_RELEASE;
          settings.length = 80;
          settings.actionTick = 40;
          settings.damage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
          settings.chance = 0.05F;
        }, Objects::nonNull)
      );
    }

    this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
    this.targetSelector.add(0, new RevengeGoal(this));
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(IS_ELITE, false);
    this.dataTracker.startTracking(ELITE_AURA_CHARGING, false);
    this.dataTracker.startTracking(ELITE_AURA_RELEASING, false);
    this.dataTracker.startTracking(FOR_DISPLAY, false);
  }

  @Override
  public void tick() {
    super.tick();
    if (!recalculatedDimensions) {
      recalculatedDimensions = true;
      calculateDimensions();
      calculateBoundingBox();
    }
    if (getWorld().isClient) {
      processAnimations();
      spawnAuraParticles();
    } else {
      decrementCooldowns();
      eliteAttackCooldown--;
    }
  }

  private void spawnAuraParticles() {
    var amount = 0;
    var speed = 0.0;
    if (this.dataTracker.get(ELITE_AURA_CHARGING)) {
      amount = 5;
      speed = -0.05;
    } else if (this.dataTracker.get(ELITE_AURA_RELEASING)) {
      amount = 20;
      speed = 0.25;
    }
    ParticleUtils.addAura(
      (ClientWorld) getWorld(),
      getPos().add(0.0D, getHeight() * 0.5D, 0.0D),
      MineCellsParticles.AURA,
      amount,
      2.5,
      0.0
    );
    ParticleUtils.addAura(
      (ClientWorld) getWorld(),
      getPos().add(0.0D, getHeight() * 0.5D, 0.0D),
      MineCellsParticles.AURA,
      amount,
      getHeight() * 0.5D,
      speed
    );
  }

  @Override
  public EntityDimensions getDimensions(EntityPose pose) {
    var result = super.getDimensions(pose);
    if (isElite()) {
      return new EntityDimensions(result.width * ELITE_SCALE, result.height * ELITE_SCALE, result.fixed);
    }
    return result;
  }

  public float getPathfindingFavor(BlockPos pos, WorldView world) {
    return 0.0F;
  }

  @Override
  public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
    int worldY = world.getTopY(Heightmap.Type.WORLD_SURFACE, this.getBlockX(), this.getBlockZ());
    if (this.getRandom().nextFloat() < 0.9F || MathHelper.abs(worldY - this.getBlockY()) > 5) {
      return false;
    }
    if (world.getClosestPlayer(this, 64.0D) != null) {
      return false;
    }
    BlockState blockBelow = world.getBlockState(this.getBlockPos().down());
    return blockBelow.isSideSolidFullSquare(world, this.getBlockPos().down(), Direction.UP) && blockBelow.getBlock() != Blocks.BEDROCK;
  }

  @Override
  protected void dropXp() {
    super.dropXp();
    if (additionalLootTable != null) {
      var server = this.getWorld().getServer();
      if (server == null) return;

      var lootTable = server.getLootManager().getLootTable(additionalLootTable);
      var damageSource = this.getRecentDamageSource();
      if (damageSource == null) return;

      LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) this.getWorld())
        .add(LootContextParameters.THIS_ENTITY, this)
        .add(LootContextParameters.ORIGIN, this.getPos())
        .add(LootContextParameters.DAMAGE_SOURCE, damageSource)
        .addOptional(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker())
        .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());

      if (getLastAttacker() != null && getLastAttacker().isPlayer() && this.attackingPlayer != null) {
        builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
      }

      LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
      lootTable.generateLoot(lootContextParameterSet, this::dropStack);
    }
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    if (damageSource.isOf(MineCellsDamageSource.GRENADE.key)) {
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

  protected State getStateFromTrackedData(TrackedData<Boolean> charging, TrackedData<Boolean> releasing) {
    if (this.dataTracker.get(charging)) {
      return State.CHARGE;
    }
    if (this.dataTracker.get(releasing)) {
      return State.RELEASE;
    }
    return State.IDLE;
  }

  protected void decrementCooldowns() {
  }

  protected void processAnimations() {
  }

  public boolean isElite() {
    return dataTracker.get(IS_ELITE);
  }

  @Override
  protected SoundEvent getDeathSound() {
    return MineCellsSounds.LEAPING_ZOMBIE_DEATH;
  }

  public boolean isForDisplay() {
    return this.dataTracker.get(FOR_DISPLAY);
  }


  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);

    if (spawnRunePos != null)
      nbt.putLong("spawnRunePos", spawnRunePos.asLong());
    nbt.putBoolean("isElite", dataTracker.get(IS_ELITE));
    if (additionalLootTable != null)
      nbt.putString("additionalLootTable", additionalLootTable.toString());
    nbt.putBoolean("forDisplay", this.dataTracker.get(FOR_DISPLAY));
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);

    if (nbt.contains("spawnRunePos"))
      spawnRunePos = BlockPos.fromLong(nbt.getLong("spawnRunePos"));
    dataTracker.set(IS_ELITE, nbt.getBoolean("isElite"));
    if (nbt.contains("additionalLootTable"))
      additionalLootTable = Identifier.tryParse(nbt.getString("additionalLootTable"));
    if (nbt.contains("forDisplay")) this.dataTracker.set(FOR_DISPLAY, nbt.getBoolean("forDisplay"));

    this.calculateDimensions();
    this.calculateBoundingBox();
    this.goalSelector.clear(Objects::nonNull);
    this.targetSelector.clear(Objects::nonNull);
    this.initGoals();
  }

  protected void handleStateChange(State state, boolean value, TrackedData<Boolean> charging, TrackedData<Boolean> releasing) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(charging, value);
      case RELEASE -> this.dataTracker.set(releasing, value);
    }
  }

  protected BiConsumer<State, Boolean> handleStateChange(TrackedData<Boolean> charging, TrackedData<Boolean> releasing) {
    return (state, value) -> this.handleStateChange(state, value, charging, releasing);
  }

  public float getDamage(float scale) {
    return (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * scale;
  }
}
