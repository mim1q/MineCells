package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusBarrageGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusMoveAroundGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusTargetGoal;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConjunctiviusEntity extends MineCellsBossEntity {

  public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> BARRAGE_ACTIVE = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<BlockPos> ANCHOR_TOP = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_LEFT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_RIGHT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<Integer> STAGE = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.INTEGER);

  // Stages:
  // 0 - has not seen any player yet
  // 1 - first full phase
  // 2 - first transition phase
  // 3 - second full phase
  // 4 - second transition phase
  // 5 - third full phase
  // 6 - third transition phase
  // 7 - fourth full phase

  private Vec3d spawnPos = Vec3d.ZERO;
  private Direction direction;
  private float spawnRot = 0.0F;
  private BlockBox roomBox = null;
  private int dashCooldown = 0;
  private int auraCooldown = 0;
  public int barrageCooldown = 0;
  public boolean moving = false;
  private int stageTicks = 1;
  private int lastStage = 0;
  private final Set<Integer> tentacleIds = new HashSet<>();

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new ConjunctiviusMoveControl(this);
    this.navigation = new BirdNavigation(this, this.world);
    this.setNoGravity(true);
    this.ignoreCameraFrustum = true;
    if (this.roomBox == null) {
      this.roomBox = BlockBox.create(this.getBlockPos(), this.getBlockPos());
    }
  }

  @Nullable
  @Override
  public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
    this.spawnPos = Vec3d.ofCenter(this.getBlockPos());
    this.roomBox = this.createBox();
    this.direction = this.determineDirection(this.getBlockPos(), this.roomBox);
    this.spawnRot = this.direction.asRotation();
    BlockPos topAnchor = this.getBoxWall(this.getBlockPos(), Direction.UP.getVector());
    BlockPos leftAnchor = this.getBoxWall(this.getBlockPos().up(2), direction.rotateYClockwise().getVector());
    BlockPos rightAnchor = this.getBoxWall(this.getBlockPos().up(2), direction.rotateYCounterclockwise().getVector());
    this.setAnchors(topAnchor, leftAnchor, rightAnchor);
    return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
  }

  protected BlockBox createBox() {
    BlockPos startPos = this.getBlockPos();
    int minY = this.getBoxWall(startPos, Direction.DOWN.getVector()).getY();
    int maxY = this.getBoxWall(startPos, Direction.UP.getVector()).getY();
    int minX = this.getBoxWall(startPos, Direction.WEST.getVector()).getX();
    int maxX = this.getBoxWall(startPos, Direction.EAST.getVector()).getX();
    int minZ = this.getBoxWall(startPos, Direction.NORTH.getVector()).getZ();
    int maxZ = this.getBoxWall(startPos, Direction.SOUTH.getVector()).getZ();

    return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ).expand(-1);
  }

  private BlockPos getBoxWall(BlockPos position, Vec3i offset) {
    for (int i = 0; i < 100; i++) {
      position = position.add(offset);
      if (!this.world.getBlockState(position).isAir()) {
        return position;
      }
    }
    return position;
  }

  protected Direction determineDirection(BlockPos spawnBlockPos, BlockBox box) {
    int southDistance = box.getMaxZ() - spawnBlockPos.getZ();
    int northDistance = spawnBlockPos.getZ() - box.getMinZ();
    int eastDistance = box.getMaxX() - spawnBlockPos.getX();
    int westDistance = spawnBlockPos.getX() - box.getMinX();

    int smallestDistance = Math.min(southDistance, Math.min(northDistance, Math.min(eastDistance, westDistance)));
    if (smallestDistance == southDistance) {
      return Direction.NORTH;
    }
    if (smallestDistance == northDistance) {
      return Direction.SOUTH;
    }
    if (smallestDistance == eastDistance) {
      return Direction.WEST;
    }
    return Direction.EAST;
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(DASH_RELEASING, false);
    this.dataTracker.startTracking(DASH_CHARGING, false);
    this.dataTracker.startTracking(AURA_RELEASING, false);
    this.dataTracker.startTracking(AURA_CHARGING, false);
    this.dataTracker.startTracking(BARRAGE_ACTIVE, false);
    this.dataTracker.startTracking(ANCHOR_TOP, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_LEFT, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_RIGHT, this.getBlockPos());
    this.dataTracker.startTracking(STAGE, 0);
  }

  @Override
  protected void initGoals() {
    final ConjunctiviusAuraGoal auraGoal = ((ConjunctiviusAuraGoal.Builder) new ConjunctiviusAuraGoal.Builder(this)
      .cooldownGetter(() -> this.auraCooldown)
      .cooldownSetter((cooldown) -> this.auraCooldown = this.stageAdjustedCooldown(cooldown))
      .stateSetter(this::switchAuraState)
      .chargeSound(MineCellsSounds.SHOCKER_CHARGE)
      .releaseSound(MineCellsSounds.SHOCKER_RELEASE)
      .soundVolume(2.0F)
      .damage(10.0F)
      .radius(8.0D)
      .defaultCooldown(300)
      .actionTick(40)
      .chance(0.025F)
      .length(80))
      .build();

    final ConjunctiviusDashGoal dashGoal = ((ConjunctiviusDashGoal.Builder) new ConjunctiviusDashGoal.Builder(this)
      .cooldownSetter((cooldown) -> this.dashCooldown = this.stageAdjustedCooldown(cooldown))
      .cooldownGetter(() -> this.dashCooldown)
      .stateSetter(this::switchDashState)
      .chargeSound(MineCellsSounds.CONJUNCTIVIUS_DASH_CHARGE)
      .releaseSound(MineCellsSounds.CONJUNCTIVIUS_DASH_RELEASE)
      .soundVolume(2.0F)
      .speed(1.25F)
      .damage(20.0F)
      .defaultCooldown(400)
      .actionTick(30)
      .chance(0.1F)
      .length(50)
      .noRotation()
      .margin(0.5D))
      .build();

    this.goalSelector.add(2, dashGoal);
    this.goalSelector.add(9, auraGoal);
    this.goalSelector.add(10, new ConjunctiviusMoveAroundGoal(this));

    this.targetSelector.add(0, new ConjunctiviusTargetGoal(this));
  }

  public void addStageGoals(int stage) {
    if (stage == 3) {
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Targeted(this, 0.15D, 0.1F));
      return;
    }
    if (stage == 7) {
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal.Around(this, 0.15D, 0.02F));
    }
  }

  @Override
  public void tick() {
    super.tick();
    if (this.world.isClient()) {
      if (this.getDashState() != TimedActionGoal.State.IDLE || this.getAuraState() == TimedActionGoal.State.RELEASE) {
        this.spikeOffset.setupTransitionTo(0.0F, 10.0F);
      } else {
        this.spikeOffset.setupTransitionTo(5.0F, 40.0F);
      }
      this.spawnParticles();
    } else {
      for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox())) {
        if (e instanceof LivingEntity livingEntity && !(e instanceof SewersTentacleEntity)) {
          this.tryAttack(livingEntity);
          this.knockback(livingEntity);
        }
      }
      BlockPos.iterateOutwards(this.getBlockPos(), 3, 4, 3).forEach((blockPos) -> {
        if (this.getRoomBox().contains(blockPos)) {
          this.world.breakBlock(blockPos, true);
        }
      });

      if (!this.world.getBlockState(this.getBlockPos().down(2)).isAir()) {
        this.move(MovementType.SELF, new Vec3d(0.0D, 0.1D, 0.0D));
      }
      this.bodyYaw = this.spawnRot;
      this.prevBodyYaw = this.spawnRot;
      this.setYaw(this.spawnRot);

      // Handle bossbar visibility
      boolean playersInArea = this.world.getPlayers(TargetPredicate.DEFAULT, this, Box.from(this.roomBox).expand(4.0D)).size() > 0;
      this.bossBar.setVisible(playersInArea);

      this.switchStages(this.getStage());

      if (!this.isInFullStage()) {
        Set<Integer> aliveTentacles = this.tentacleIds
          .stream()
          .filter(id -> this.world.getEntityById(id) != null)
          .collect(Collectors.toSet());
        this.tentacleIds.clear();
        this.tentacleIds.addAll(aliveTentacles);

        if (this.stageTicks > 30 && aliveTentacles.isEmpty() && this.getStage() != 0) {
          this.setStage(this.getStage() + 1);
        } else if (this.getStage() != 0) {
          this.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.PROTECTED, 20, 0, false, false));
        }
      }
    }

    int stage = this.getStage();
    if (stage != this.lastStage) {
      this.stageTicks = 0;
    }
    this.lastStage = stage;
    this.stageTicks++;
  }

  @Override
  protected void updatePostDeath() {
    if (this.world.isClient()) {
      int interval = this.deathTime >= 55 ? 1 : 10;
      if (this.deathTime % interval == 0) {
        this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 2.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
      }
    } else {
      if (this.deathTime == 1) {
        this.playSound(MineCellsSounds.CONJUNCTIVIUS_DYING, 2.0F, 1.0F);
      }
      if (this.deathTime == 60) {
        this.playSound(MineCellsSounds.CONJUNCTIVIUS_DEATH, 2.0F, 1.0F);
        this.remove(RemovalReason.KILLED);
      }
    }
    this.deathTime++;
  }

  protected void switchStages(int stage) {
    if (stage == 0 && this.getTarget() != null) {
      this.setStage(1);
      return;
    }
    float healthPercent = this.getHealth() / this.getMaxHealth();
    if (stage == 1 && healthPercent <= 0.8F) {
      this.spawnTentacles();
      this.setStage(2);
      return;
    }
    if (stage == 3 && healthPercent <= 0.55F) {
      this.spawnTentacles();
      this.setStage(4);
      return;
    }
    if (stage == 5 && healthPercent <= 0.3F) {
      this.spawnTentacles();
      this.setStage(6);
    }
  }

  protected void spawnTentacles() {
    int playerAmount = this.world.getPlayers(TargetPredicate.DEFAULT, this, Box.from(this.roomBox).expand(4.0D)).size();
    for (int i = 0; i < 2 + 2 * playerAmount; i++) {
      SewersTentacleEntity tentacle = MineCellsEntities.SEWERS_TENTACLE.create(this.world);
      if (tentacle != null) {
        tentacle.setPosition(this.getTentaclePos());
        tentacle.setSpawnedByBoss(true);
        this.tentacleIds.add(tentacle.getId());
        this.world.spawnEntity(tentacle);
      }
    }
  }

  private Vec3d getTentaclePos() {
    int x = this.random.nextBetween(this.roomBox.getMinX(), this.roomBox.getMaxX());
    int y = this.roomBox.getMinY();
    int z = this.random.nextBetween(this.roomBox.getMinZ(), this.roomBox.getMaxZ());
    return new Vec3d(x, y, z);
  }

  protected void spawnParticles() {
    Vec3d pos = this.getPos().add(0.0D, this.getHeight() * 0.5D, 0.0D);
    if (this.getAuraState() == TimedActionGoal.State.CHARGE) {
      ParticleUtils.addAura((ClientWorld) this.world, pos, MineCellsParticles.AURA, 2, 7.5D, -0.01D);
    } else if (this.getAuraState() == TimedActionGoal.State.RELEASE) {
      ParticleUtils.addAura((ClientWorld) this.world, pos, MineCellsParticles.AURA, 50, 7.0D, 0.01D);
      ParticleUtils.addAura((ClientWorld) this.world, pos, MineCellsParticles.AURA, 10, 1.0D, 0.5D);
    }

    int stage = this.getStage();
    if (this.isInFullStage() || stage == 0) {
      return;
    }
    if (this.stageTicks == 1) {
      Vec3d offset = switch (stage) {
        case 2 -> new Vec3d(2.2D, -0.85D, 0.0D);
        case 4 -> new Vec3d(-2.2D, -0.85D, 0.0D);
        default -> new Vec3d(0.0D, 1.75D, 0.0D);
      };
      Vec3d target = switch (stage) {
        case 2 -> Vec3d.ofCenter(this.getRightAnchor());
        case 4 -> Vec3d.ofCenter(this.getLeftAnchor());
        default -> Vec3d.ofCenter(this.getTopAnchor());
      };
      this.spawnChainBreakingParticles(offset, target);
    }
  }

  private void spawnChainBreakingParticles(Vec3d offset, Vec3d target) {
    ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, MineCellsBlocks.BIG_CHAIN.getDefaultState());
    Vec3d diff = target.subtract(this.getPos().add(offset));
    double length = diff.length();
    for (int i = 0; i < length; i++) {
      Vec3d pos = this.getPos().add(offset).add(diff.multiply(i / length));
      this.world.addParticle(particle, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
    }
  }

  @Override
  protected void decrementCooldowns() {
    this.dashCooldown = Math.max(0, this.dashCooldown - 1);
    this.auraCooldown = Math.max(0, this.auraCooldown - 1);
    this.barrageCooldown = Math.max(0, this.barrageCooldown - 1);
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    if (this.getTarget() == null) {
      return false;
    }
    if (source.isProjectile()) {
      return super.damage(source, amount * 0.33F);
    }
    return super.damage(source, amount);
  }

  public int stageAdjustedCooldown(int cooldown) {
    int stage = this.getStage();
    return switch (stage) {
      case 3 -> cooldown / 2;
      case 5 -> cooldown / 3;
      case 7 -> cooldown / 4;
      default -> cooldown;
    };
  }

  protected void switchDashState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(DASH_CHARGING, value);
      case RELEASE -> this.dataTracker.set(DASH_RELEASING, value);
    }
  }

  protected void switchAuraState(TimedActionGoal.State state, boolean value) {
    switch (state) {
      case CHARGE -> this.dataTracker.set(AURA_CHARGING, value);
      case RELEASE -> this.dataTracker.set(AURA_RELEASING, value);
    }
  }

  public TimedActionGoal.State getDashState() {
    return this.getStateFromTrackedData(DASH_CHARGING, DASH_RELEASING);
  }

  public TimedActionGoal.State getAuraState() {
    return this.getStateFromTrackedData(AURA_CHARGING, AURA_RELEASING);
  }

  public void setAnchors(BlockPos top, BlockPos left, BlockPos right) {
    this.dataTracker.set(ANCHOR_TOP, top);
    this.dataTracker.set(ANCHOR_LEFT, left);
    this.dataTracker.set(ANCHOR_RIGHT, right);
  }

  public BlockPos getTopAnchor() {
    return this.dataTracker.get(ANCHOR_TOP);
  }

  public BlockPos getLeftAnchor() {
    return this.dataTracker.get(ANCHOR_LEFT);
  }

  public BlockPos getRightAnchor() {
    return this.dataTracker.get(ANCHOR_RIGHT);
  }

  public int getStage() {
    return this.dataTracker.get(STAGE);
  }

  public boolean isInFullStage() {
    int stage = this.getStage();
    return stage == 1 || stage == 3 || stage == 5 || stage == 7;
  }

  public boolean canAttack() {
    return this.isInFullStage() && this.stageTicks > 40;
  }

  public void setStage(int stage) {
    if (stage != this.getStage()) {
      this.playSound(MineCellsSounds.CONJUNCTIVIUS_SHOUT, 2.0F, 1.0F);
      this.dataTracker.set(STAGE, stage);
      this.addStageGoals(stage);
    }
  }

  public ConjunctiviusEyeRenderer.EyeState getEyeState() {
    boolean stageBeginning = this.stageTicks > 0 && this.stageTicks < 30;
    if (stageBeginning || !this.isAlive()) {
      return ConjunctiviusEyeRenderer.EyeState.SHAKING;
    }
    if (this.dataTracker.get(BARRAGE_ACTIVE)) {
      return ConjunctiviusEyeRenderer.EyeState.GREEN;
    }
    if (this.getDashState() != TimedActionGoal.State.IDLE) {
      return ConjunctiviusEyeRenderer.EyeState.YELLOW;
    }
    return ConjunctiviusEyeRenderer.EyeState.PINK;
  }

  public static DefaultAttributeContainer.Builder createConjunctiviusAttributes() {
    return createHostileAttributes()
      .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0D)
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 10.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 5.0D);
  }

  public BlockBox getRoomBox() {
    return this.roomBox;
  }

  public Vec3d getSpawnPos() {
    return this.spawnPos;
  }

  @Override
  public boolean isPushable() {
    return false;
  }

  public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
    return false;
  }

  protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) { }

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

  @Override
  public int getMaxHeadRotation() {
    return 360;
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putInt("dashCooldown", this.dashCooldown);
    nbt.putIntArray("spawnPos", new int[] {
      (int)this.spawnPos.x, (int)this.spawnPos.y, (int)this.spawnPos.z
    });
    nbt.putIntArray("roomBox", new int[]{
      this.roomBox.getMinX(), this.roomBox.getMinY(), this.roomBox.getMinZ(),
      this.roomBox.getMaxX(), this.roomBox.getMaxY(), this.roomBox.getMaxZ()
    });
    nbt.putFloat("spawnRot", this.spawnRot);
    nbt.putIntArray("anchors", new int[] {
      this.getTopAnchor().getX(), this.getTopAnchor().getY(), this.getTopAnchor().getZ(),
      this.getLeftAnchor().getX(), this.getLeftAnchor().getY(), this.getLeftAnchor().getZ(),
      this.getRightAnchor().getX(), this.getRightAnchor().getY(), this.getRightAnchor().getZ(),
    });
    nbt.putInt("stage", this.dataTracker.get(STAGE));
    nbt.putInt("stageTicks", this.stageTicks);
    nbt.putIntArray("tentacleIds", this.tentacleIds.stream().toList());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.dashCooldown = nbt.getInt("dashCooldown");
    if (nbt.contains("spawnPos") && nbt.getIntArray("spawnPos").length == 3) {
      int[] posArray = nbt.getIntArray("spawnPos");
      this.spawnPos = new Vec3d(posArray[0], posArray[1], posArray[2]);
    }
    if (nbt.contains("roomBox") && nbt.getIntArray("roomBox").length == 6) {
      int[] boxArray = nbt.getIntArray("roomBox");
      this.roomBox = new BlockBox(boxArray[0], boxArray[1], boxArray[2], boxArray[3], boxArray[4], boxArray[5]);
    }
    this.spawnRot = nbt.getFloat("spawnRot");
    this.direction = Direction.fromRotation(this.spawnRot);
    if (nbt.contains("anchors") && nbt.getIntArray("anchors").length == 9) {
      int[] anchors = nbt.getIntArray("anchors");
      BlockPos anchorTop = new BlockPos(anchors[0], anchors[1], anchors[2]);
      BlockPos anchorLeft = new BlockPos(anchors[3], anchors[4], anchors[5]);
      BlockPos anchorRight = new BlockPos(anchors[6], anchors[7], anchors[8]);
      this.setAnchors(anchorTop, anchorLeft, anchorRight);
    }
    this.setStage(nbt.getInt("stage"));
    if (this.getStage() > 3) {
      this.addStageGoals(3);
    }
    this.stageTicks = nbt.getInt("stageTicks");
    int[] tentacleIds = nbt.getIntArray("tentacleIds");
    for (int id : tentacleIds) {
      this.tentacleIds.add(id);
    }
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource source) {
    return MineCellsSounds.CONJUNCTIVIUS_HIT;
  }

  protected static class ConjunctiviusMoveControl extends MoveControl {
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

  protected static class ConjunctiviusDashGoal extends TimedDashGoal<ConjunctiviusEntity> {

    public ConjunctiviusDashGoal(Builder builder) {
      super(builder);
    }

    @Override
    public boolean canStart() {
      return super.canStart()
        && this.entity.canAttack()
        && !this.entity.moving;
    }

    @Override
    public boolean shouldContinue() {
      boolean hitWall = (this.ticks() > (this.actionTick + 5) && (this.entity.horizontalCollision || this.entity.verticalCollision));
      return super.shouldContinue() && !hitWall;
    }

    @Override
    public void stop() {
      super.stop();
      if (this.entity.getSpawnPos() != null) {
        this.entity.setVelocity(this.entity.getSpawnPos().subtract(this.entity.getPos()).normalize());
      }
    }

    public static class Builder extends TimedDashGoal.Builder<ConjunctiviusEntity> {

      public Builder(ConjunctiviusEntity entity) {
        super(entity);
      }

      public ConjunctiviusDashGoal build() {
        return new ConjunctiviusDashGoal(this);
      }
    }
  }

  protected static class ConjunctiviusAuraGoal extends TimedAuraGoal<ConjunctiviusEntity> {

    public ConjunctiviusAuraGoal(Builder builder) {
      super(builder);
    }

    @Override
    public boolean canStart() {
      return super.canStart()
        && this.entity.canAttack()
        && !this.entity.moving;
    }

    public static class Builder extends TimedAuraGoal.Builder<ConjunctiviusEntity> {

      public Builder(ConjunctiviusEntity entity) {
        super(entity);
      }

      public ConjunctiviusAuraGoal build() {
        return new ConjunctiviusAuraGoal(this);
      }
    }
  }
}
