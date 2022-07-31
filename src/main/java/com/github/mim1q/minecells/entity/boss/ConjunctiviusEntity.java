package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.entity.nonliving.projectile.ConjunctiviusProjectileEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.registry.StatusEffectRegistry;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
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
  public static final TrackedData<Boolean> EYE_SHAKING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

  private Vec3d spawnPos = Vec3d.ZERO;
  private Direction direction;
  private float spawnRot = 0.0F;
  private BlockBox roomBox = null;
  private int dashCooldown = 0;
  private int auraCooldown = 0;
  private int barrageCooldown = 0;
  protected boolean isMoving = false;
  private boolean betweenStages = false;
  private int stageTicks = 1;
  private final Set<Integer> tentacleIds = new HashSet<>();
  private int lastStage = 0;


  private final ConjunctiviusDashGoal dashGoal;
  private final ConjunctiviusAuraGoal auraGoal;

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new ConjunctiviusMoveControl(this);
    this.navigation = new BirdNavigation(this, this.world);
    this.setNoGravity(true);
    this.ignoreCameraFrustum = true;

    this.dashGoal = ((ConjunctiviusDashGoal.Builder) new ConjunctiviusDashGoal.Builder(this)
      .cooldownSetter((cooldown) -> this.dashCooldown = cooldown)
      .cooldownGetter(() -> this.dashCooldown)
      .stateSetter(this::switchDashState)
      .chargeSound(SoundRegistry.SHIELDBEARER_CHARGE)
      .releaseSound(SoundRegistry.SHIELDBEARER_RELEASE)
      .soundVolume(2.0F)
      .speed(1.25F)
      .damage(20.0F)
      .defaultCooldown(400)
      .actionTick(30)
      .chance(0.15F)
      .length(50)
      .noRotation()
      .margin(0.5D))
        .build();

    this.auraGoal = ((ConjunctiviusAuraGoal.Builder) new ConjunctiviusAuraGoal.Builder(this)
      .cooldownGetter(() -> this.auraCooldown)
      .cooldownSetter((cooldown) -> this.auraCooldown = cooldown)
      .stateSetter(this::switchAuraState)
      .chargeSound(SoundRegistry.SHOCKER_CHARGE)
      .releaseSound(SoundRegistry.SHOCKER_RELEASE)
      .soundVolume(2.0F)
      .damage(10.0F)
      .radius(8.0D)
      .defaultCooldown(200)
      .actionTick(40)
      .chance(0.15F)
      .length(80))
        .build();
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

    return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
  }

  private BlockPos getBoxWall(BlockPos position, Vec3i offset) {
    for (int i = 0; i < 100; i++) {
      position = position.add(offset);
      if (!this.world.getBlockState(position).isAir()) {
        return position.subtract(offset);
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
    this.dataTracker.startTracking(EYE_SHAKING, false);
  }

  @Override
  protected void initGoals() {
    this.targetSelector.add(0, new ConjunctiviusTargetGoal(this));
  }

  protected void setupGoals(int stage) {
    this.goalSelector.clear();
    if (stage == 1) {
      this.goalSelector.add(2, this.dashGoal);
      this.goalSelector.add(9, this.auraGoal);
      this.goalSelector.add(10, new ConjunctiviusMoveAroundGoal(this));
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal(this));
    } else {
      this.goalSelector.add(2, this.dashGoal);
      this.goalSelector.add(9, this.auraGoal);
      this.goalSelector.add(10, new ConjunctiviusMoveAroundGoal(this));
      this.goalSelector.add(2, new ConjunctiviusBarrageGoal(this));
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
      int stage = this.getStage();
      if (stage != this.lastStage) {

      }
      this.lastStage = stage;
    } else {
      for (Entity e : this.world.getOtherEntities(this, Box.from(this.getRoomBox()).expand(8.0D))) {
        if (e instanceof EnderPearlEntity) {
          e.kill();
        }
      }
      for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox())) {
        if (e instanceof LivingEntity livingEntity) {
          this.tryAttack(livingEntity);
          this.knockback(livingEntity);
        }
      }
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

      if (this.betweenStages) {
        Set<Integer> aliveTentacles = this.tentacleIds
          .stream()
          .filter(id -> this.world.getEntityById(id) != null)
          .collect(Collectors.toSet());
        this.tentacleIds.clear();
        this.tentacleIds.addAll(aliveTentacles);

        if (this.stageTicks > 30 && aliveTentacles.isEmpty()) {
          this.betweenStages = false;
          this.stageTicks = 0;
          this.setStage(this.getStage() + 1);
        } else {
          this.addStatusEffect(new StatusEffectInstance(StatusEffectRegistry.PROTECTED, 20, 0));
        }
      }
      this.dataTracker.set(EYE_SHAKING, this.stageTicks > 0 && this.stageTicks < 30);
      this.stageTicks++;
    }
  }

  @Override
  protected void updatePostDeath() {
    ++this.deathTime;
    if (this.deathTime >= 60 && !this.world.isClient()) {
      this.remove(RemovalReason.KILLED);
    }
  }

  protected void switchStages(int stage) {
    if (!this.betweenStages) {
      if (stage == 0 && this.getTarget() != null) {
        this.stageTicks = 0;
        this.setStage(1);
        this.setupGoals(1);
        return;
      }
      float healthPercent = this.getHealth() / this.getMaxHealth();
      if (stage == 1 && healthPercent <= 0.66F) {
        this.spawnTentacles();
        return;
      }
      if (stage == 2 && healthPercent <= 0.33F) {
        this.spawnTentacles();
      }
    }
  }

  protected void spawnTentacles() {
    this.betweenStages = true;
    this.stageTicks = 0;
    for (int i = 0; i < 4; i++) {
      SewersTentacleEntity tentacle = new SewersTentacleEntity(EntityRegistry.SEWERS_TENTACLE, this.world);
      this.tentacleIds.add(tentacle.getId());
      tentacle.setPosition(this.getX(), this.getY(), this.getZ());
      this.world.spawnEntity(tentacle);
    }
  }

  protected void spawnParticles() {
    Vec3d pos = this.getPos().add(0.0D, this.getHeight() * 0.5D, 0.0D);
    if (this.getAuraState() == TimedActionGoal.State.CHARGE) {
      ParticleUtils.addAura((ClientWorld) this.world, pos, ParticleRegistry.AURA, 2, 7.5D, -0.01D);
    } else if (this.getAuraState() == TimedActionGoal.State.RELEASE) {
      ParticleUtils.addAura((ClientWorld) this.world, pos, ParticleRegistry.AURA, 50, 7.0D, 0.01D);
      ParticleUtils.addAura((ClientWorld) this.world, pos, ParticleRegistry.AURA, 10, 1.0D, 0.5D);
    }
  }

  @Override
  protected void decrementCooldowns() {
    this.dashCooldown = Math.max(0, this.dashCooldown - 1);
    this.auraCooldown = Math.max(0, this.auraCooldown - 1);
    this.barrageCooldown = Math.max(0, this.barrageCooldown - 1);
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

  public void setStage(int stage) {
    this.dataTracker.set(STAGE, stage);
  }

  public ConjunctiviusEyeRenderer.EyeState getEyeState() {
    if (this.dataTracker.get(EYE_SHAKING) || !this.isAlive()) {
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
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 250.0D)
      .add(EntityAttributes.GENERIC_ARMOR, 16.0D)
      .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 16.0D)
      .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 4.0D);
  }

  public BlockBox getRoomBox() {
    return this.roomBox;
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
    this.dataTracker.set(STAGE, nbt.getInt("stage"));
    int[] tentacleIds = nbt.getIntArray("tentacleIds");
    for (int id : tentacleIds) {
      this.tentacleIds.add(id);
    }
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
    private boolean blocksDestroyed = false;

    public ConjunctiviusDashGoal(Builder builder) {
      super(builder);
    }

    @Override
    public boolean canStart() {
      return super.canStart()
        && !this.entity.betweenStages
        && !this.entity.isMoving;
    }

    @Override
    public boolean shouldContinue() {
      boolean hitWall = !this.blocksDestroyed && (this.entity.horizontalCollision || this.entity.verticalCollision);
      return super.shouldContinue() && !hitWall;
    }

    @Override
    protected void release() {
      super.release();
      this.blocksDestroyed = false;
      BlockPos.iterateOutwards(this.entity.getBlockPos(), 3, 4, 3).forEach((blockPos) -> {
        if (this.entity.getRoomBox().contains(blockPos) && this.entity.world.getBlockState(blockPos).isSolidBlock(this.entity.world, blockPos)) {
          this.entity.world.breakBlock(blockPos, false);
          this.blocksDestroyed = true;
        }
      });
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
        && !this.entity.betweenStages
        && !this.entity.isMoving;
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

  protected static class ConjunctiviusMoveAroundGoal extends Goal {

    protected final ConjunctiviusEntity entity;
    protected double speed = 0.05D;
    private int cooldown = 0;
    private Vec3d targetPos;

    public ConjunctiviusMoveAroundGoal(ConjunctiviusEntity entity) {
      this.entity = entity;
      this.targetPos = this.entity.getPos();
      this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
      return !this.entity.betweenStages
        && this.entity.spawnPos != null
        && this.entity.roomBox != null;
    }

    @Override
    public boolean shouldContinue() {
      return this.canStart();
    }

    @Override
    public void start() {
      super.start();
      this.targetPos = this.entity.spawnPos;
    }

    @Override
    public void tick() {
      this.entity.isMoving = this.isMoving();
      if (this.cooldown == 0) {
        this.entity.setVelocity(this.entity.getVelocity().add(this.targetPos.subtract(this.entity.getPos()).normalize().multiply(this.speed)));
        if (this.targetPos.distanceTo(this.entity.getPos()) < 2.0D || this.entity.horizontalCollision || this.entity.verticalCollision) {
          this.targetPos = this.getRandomTargetPos();
          this.cooldown = this.getNextCooldown();
        }
      } else {
        this.cooldown--;
      }
    }

    @Override
    public void stop() {
      this.cooldown = 0;
      this.entity.isMoving = false;
      this.targetPos = this.entity.spawnPos;
    }

    public boolean isMoving() {
      return this.cooldown == 0;
    }

    private Vec3d getRandomTargetPos() {
      Vec3d center = this.entity.spawnPos;
      return center.add(
        (this.entity.getRandom().nextDouble() - 0.5D) * 5.0D,
        (this.entity.getRandom().nextDouble() - 0.5D) * 5.0D,
        (this.entity.getRandom().nextDouble() - 0.5D) * 5.0D
      );
    }

    protected int getNextCooldown() {
      return this.entity.getRandom().nextInt(40) + 40;
    }

    @Override
    public boolean shouldRunEveryTick() {
      return true;
    }
  }

  protected static class ConjunctiviusBarrageGoal extends ConjunctiviusMoveAroundGoal {

    private int ticks = 0;
    private Entity target;

    public ConjunctiviusBarrageGoal(ConjunctiviusEntity entity) {
      super(entity);
      this.speed = 0.15D;
    }

    @Override
    public boolean canStart() {
      this.target = entity.getTarget();
      return super.canStart()
        && this.entity.barrageCooldown == 0
        && this.target != null
        && this.entity.isMoving
        && !this.entity.betweenStages
        && this.entity.getRandom().nextFloat() < 1.0F;
    }

    @Override
    public boolean shouldContinue() {
      this.target = entity.getTarget();
      return this.target != null && this.ticks < 260;
    }

    @Override
    public void start() {
      super.start();
      this.entity.dataTracker.set(BARRAGE_ACTIVE, true);
    }

    @Override
    public void tick() {
      if (this.ticks > 60) {
        super.tick();
        if (this.ticks % 2 == 0) {
          this.shoot(this.entity, this.target);
        }
      }
      this.ticks++;
    }

    private void shoot(ConjunctiviusEntity entity, Entity target) {
      if (target != null) {
        Vec3d offset = new Vec3d(0.0D, 2.0D, 2.0D).rotateY(MathUtils.radians(entity.spawnRot));
        Vec3d targetPos = target.getPos().add(
          (entity.getRandom().nextDouble() - 0.5D) * 5.0D,
          (entity.getRandom().nextDouble() - 0.5D) * 5.0D,
          (entity.getRandom().nextDouble() - 0.5D) * 5.0D
        );
        ConjunctiviusProjectileEntity.spawn(entity.world, entity.getPos().add(offset), targetPos);
      }
    }

    @Override
    protected int getNextCooldown() {
      return 10;
    }

    @Override
    public void stop() {
      this.ticks = 0;
      this.entity.barrageCooldown = 500;
      this.entity.dataTracker.set(BARRAGE_ACTIVE, false);
      super.stop();
    }
  }

  protected static class ConjunctiviusTargetGoal extends TargetRandomPlayerGoal<ConjunctiviusEntity> {

    public ConjunctiviusTargetGoal(ConjunctiviusEntity entity) {
      super(entity);
    }

    @Override
    protected List<PlayerEntity> getTargetablePlayers() {
      Box box = Box.from(this.entity.getRoomBox());
      return this.entity.world.getPlayers(TargetPredicate.DEFAULT, this.entity, box);
    }
  }
}
