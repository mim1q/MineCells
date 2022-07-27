package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.client.render.conjunctivius.ConjunctiviusEyeRenderer;
import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedDashGoal;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class ConjunctiviusEntity extends MineCellsBossEntity {

  public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  public static final TrackedData<BlockPos> ANCHOR_TOP = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_LEFT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
  public static final TrackedData<BlockPos> ANCHOR_RIGHT = DataTracker.registerData(ConjunctiviusEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

  private Vec3d spawnPos = Vec3d.ZERO;
  private Direction direction;
  private float spawnRot = 0.0F;
  private BlockBox roomBox = null;
  private int dashCooldown = 0;
  private int auraCooldown = 0;

  public ConjunctiviusEntity(EntityType<? extends HostileEntity> entityType, World world) {
    super(entityType, world);
    this.moveControl = new ConjunctiviusMoveControl(this);
    this.navigation = new BirdNavigation(this, this.world);
    this.setNoGravity(true);
    this.ignoreCameraFrustum = true;
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
    System.out.println(topAnchor + " " + leftAnchor + " " + rightAnchor);
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
    this.dataTracker.startTracking(ANCHOR_TOP, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_LEFT, this.getBlockPos());
    this.dataTracker.startTracking(ANCHOR_RIGHT, this.getBlockPos());
  }

  @Override
  protected void initGoals() {
    this.goalSelector.add(1, new ConjunctiviusDashGoal.Builder(this)
      .cooldownSetter((cooldown) -> this.dashCooldown = cooldown)
      .cooldownGetter(() -> this.dashCooldown)
      .stateSetter(this::switchDashState)
      .chargeSound(SoundRegistry.SHIELDBEARER_CHARGE)
      .releaseSound(SoundRegistry.SHIELDBEARER_RELEASE)
      .soundVolume(2.0F)
      .speed(1.25F)
      .damage(20.0F)
      .defaultCooldown(100)
      .actionTick(30)
      .chance(0.5F)
      .length(80)
      .noRotation()
      .margin(0.5D)
      .build()
    );
    this.goalSelector.add(3, new TimedAuraGoal.Builder<>(this)
      .cooldownGetter(() -> this.auraCooldown)
      .cooldownSetter((cooldown) -> this.auraCooldown = cooldown)
      .stateSetter(this::switchAuraState)
      .chargeSound(SoundRegistry.SHOCKER_CHARGE)
      .releaseSound(SoundRegistry.SHOCKER_RELEASE)
      .soundVolume(2.0F)
      .damage(10.0F)
      .radius(8.0D)
      .defaultCooldown(100)
      .actionTick(40)
      .chance(0.5F)
      .length(80)
      .build()
    );
    this.goalSelector.add(10, new ConjunctiviusReturnGoal(this));

    this.targetSelector.add(0, new ConjunctiviusTargetGoal(this));
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
      for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox().contract(0.25D))) {
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

  public ConjunctiviusEyeRenderer.EyeState getEyeState() {
    if (this.getDashState() != TimedActionGoal.State.IDLE) {
      return ConjunctiviusEyeRenderer.EyeState.YELLOW;
    }
    if (this.getAuraState() != TimedActionGoal.State.IDLE) {
      return ConjunctiviusEyeRenderer.EyeState.SHAKING;
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
      .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 2.0D);
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
      (int)this.spawnPos.x,
      (int)this.spawnPos.y,
      (int)this.spawnPos.z
    });
    nbt.putIntArray("roomBox", new int[]{
      this.roomBox.getMinX(),
      this.roomBox.getMinY(),
      this.roomBox.getMinZ(),
      this.roomBox.getMaxX(),
      this.roomBox.getMaxY(),
      this.roomBox.getMaxZ()
    });
    nbt.putFloat("spawnRot", this.spawnRot);
    nbt.putIntArray("anchors", new int[] {
      this.getTopAnchor().getX(), this.getTopAnchor().getY(), this.getTopAnchor().getZ(),
      this.getLeftAnchor().getX(), this.getLeftAnchor().getY(), this.getLeftAnchor().getZ(),
      this.getRightAnchor().getX(), this.getRightAnchor().getY(), this.getRightAnchor().getZ(),
    });
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
    protected void release() {
      super.release();
      BlockPos.iterateOutwards(this.entity.getBlockPos(), 3, 3, 3).forEach((blockPos) -> {
        if (this.entity.getRoomBox().contains(blockPos) && this.entity.world.getBlockState(blockPos).isSolidBlock(this.entity.world, blockPos)) {
          this.entity.world.breakBlock(blockPos, false);
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

  protected static class ConjunctiviusReturnGoal extends Goal {

    private final ConjunctiviusEntity entity;

    public ConjunctiviusReturnGoal(ConjunctiviusEntity entity) {
      this.entity = entity;
      this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
      return this.entity.spawnPos != null
        && this.entity.spawnPos.distanceTo(this.entity.getPos()) > 2.0D;
    }

    @Override
    public boolean shouldContinue() {
      return this.canStart();
    }

    @Override
    public void tick() {
      this.entity.setVelocity(
        this.entity.getVelocity().add(
          this.entity.spawnPos.subtract(this.entity.getPos()).normalize().multiply(0.05D))
      );
    }

    @Override
    public boolean shouldRunEveryTick() {
      return true;
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
