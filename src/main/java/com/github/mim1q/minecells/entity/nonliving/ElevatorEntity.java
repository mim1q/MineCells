package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.network.PacketIdentifiers;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEntity extends Entity {

  private static final TrackedData<Boolean> MOVING = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> GOING_UP = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Boolean> ROTATED = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
  private static final TrackedData<Float> VELOCITY_MODIFIER = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.FLOAT);
  private static final TrackedData<Integer> MIN_Y = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private static final TrackedData<Integer> MAX_Y = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.INTEGER);

  protected double serverY;
  protected int interpolationSteps = 0;
  protected int stoppedTicks = 0;
  protected boolean setup = false;
  boolean wasMoving = false;

  boolean poweredTop = true;
  boolean poweredBottom = true;

  float maxSpeed = MineCells.COMMON_CONFIG.elevator.speed;
  float acceleration = MineCells.COMMON_CONFIG.elevator.acceleration;
  float damage = MineCells.COMMON_CONFIG.elevator.damage;

  protected ArrayList<LivingEntity> hitEntities = new ArrayList<>();

  public ElevatorEntity(EntityType<ElevatorEntity> type, World world) {
    super(type, world);
    this.intersectionChecked = true;
    this.ignoreCameraFrustum = true;
    this.noClip = true;
    this.serverY = this.getY();
  }

  public static void spawn(World world, int x, int z, int minY, int maxY, boolean isRotated, boolean isGoingUp) {
    ElevatorEntity elevator = new ElevatorEntity(MineCellsEntities.ELEVATOR, world);
    elevator.setPosition(x + 0.5D, isGoingUp ? maxY : minY, z + 0.5D);
    elevator.setMaxY(maxY);
    elevator.setMinY(minY);
    elevator.setRotated(isRotated);
    elevator.setGoingUp(isGoingUp);
    world.spawnEntity(elevator);
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(MOVING, false);
    this.dataTracker.startTracking(GOING_UP, false);
    this.dataTracker.startTracking(ROTATED, false);
    this.dataTracker.startTracking(VELOCITY_MODIFIER, 0.0F);
    this.dataTracker.startTracking(MIN_Y, (int) this.getY());
    this.dataTracker.startTracking(MAX_Y, (int) this.getY());
  }

  @Override
  public void tick() {
    super.tick();
    double nextY = this.getY() + this.getVelocity().y;

    if (!this.world.isClient()) {
      float modifiedAcceleration = this.isGoingUp() ? this.acceleration : -this.acceleration;
      this.setVelocityModifier(MathHelper.clamp(modifiedAcceleration + this.getVelocityModifier(), -1.0F, 1.0F));
      this.setVelocity(0.0D, this.maxSpeed * this.getVelocityModifier(), 0.0D);
      this.velocityDirty = true;
      this.velocityModified = true;

      boolean isMoving = !(nextY < this.getMinY() || nextY > this.getMaxY());

      if (isMoving()) {
        this.stoppedTicks = 0;
      } else {
        if (this.stoppedTicks == 1) {
          this.playSound(MineCellsSounds.ELEVATOR_STOP, 1.0F, 1.0F);
        }
        this.stoppedTicks++;
      }

      if (!isMoving) {
        if (this.isGoingUp()) {
          this.setVelocityModifier(Math.max(this.getVelocityModifier(), 0.0F));
        } else {
          this.setVelocityModifier(Math.min(this.getVelocityModifier(), 0.0F));
        }
      }

      this.setMoving(isMoving);

      this.interpolationSteps = 0;
      this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
      this.handlePassengers();
      this.handleRedstone();
    } else {
      if (wasMoving && !isMoving() && !this.isGoingUp()) {
        BlockPos pos = new BlockPos(this.getBlockX(), this.getMinY() - 1, this.getBlockZ());
        BlockState state = this.world.getBlockState(pos);
        ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
        if (!state.isAir() && nextY < this.getMinY()) {
          for (int i = 0; i < 20; i++) {
            double rx = this.random.nextDouble() - 0.5D;
            double rz = this.random.nextDouble() - 0.5D;
            Vec3d vel = new Vec3d(rx, 0.1D, rz).normalize();
            ParticleUtils.addParticle((ClientWorld) this.world, particle, this.getPos().add(vel), vel.multiply(10.0D));
            ParticleUtils.addParticle((ClientWorld) this.world, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getPos().add(vel), vel.multiply(0.01D));
          }
        }
      }
      this.wasMoving = this.isMoving();
    }

    if (this.interpolationSteps > 0) {
      double d = this.getX();
      double e = this.getY() + (this.serverY - this.getY()) / (double) this.interpolationSteps;
      double f = this.getZ();
      this.interpolationSteps--;
      this.setPosition(d, e, f);
    }

    double clampedY = MathHelper.clamp(this.getY() + this.getVelocity().y, this.getMinY(), this.getMaxY());
    this.setPosition(this.getX(), clampedY, this.getZ());

    if (this.isMoving()) {
      if (this.world.isClient()) {
        double z = this.isRotated() ? 1.0D : 0.0D;
        double x = 1.0D - z;
        this.spawnMovementParticles(new Vec3d(-x, 0.0D, -z));
        this.spawnMovementParticles(new Vec3d(x, 0.0D, z));
      } else if (!this.isGoingUp()) {
        this.handleEntitiesBelow();
      }
    }

    if (this.shouldBeRemoved()) {
      this.kill();
    }
  }

  private boolean shouldBeRemoved() {
    BlockPos pos0 = this.getBlockPos().west();
    BlockPos pos1 = this.getBlockPos().east();
    if (this.isRotated()) {
      pos0 = this.getBlockPos().south();
      pos1 = this.getBlockPos().north();
    }
    BlockState state0 = this.world.getBlockState(pos0);
    BlockState state1 = this.world.getBlockState(pos1);

    if (state0.getBlock() instanceof ChainBlock && state1.getBlock() instanceof ChainBlock) {
      return state0.get(ChainBlock.AXIS) != Direction.Axis.Y && state1.get(ChainBlock.AXIS) != Direction.Axis.Y;
    }
    return true;
  }

  @Override
  public boolean handleAttack(Entity attacker) {
    if (attacker instanceof PlayerEntity player) {
      if (player.preferredHand != null && player.getStackInHand(player.preferredHand).getItem() instanceof AxeItem) {
        this.kill();
      }
    }
    return super.handleAttack(attacker);
  }

  @Override
  public void kill() {
    if (!this.world.isClient()) {
      ItemStack[] items = {
        new ItemStack(Blocks.CHAIN, this.random.nextInt(3) + 2),
        new ItemStack(MineCellsBlocks.PUTRID_PLANKS, 1),
        new ItemStack(MineCellsItems.ELEVATOR_MECHANISM, this.random.nextInt(3))
      };

      for (ItemStack itemStack : items) {
        ItemEntity entity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack);
        this.world.spawnEntity(entity);
      }
      this.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.0F, 1.0F);
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeDouble(this.getX());
      buf.writeDouble(this.getY());
      buf.writeDouble(this.getZ());
      for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
        ServerPlayNetworking.send(player, PacketIdentifiers.ELEVATOR_DESTROYED, buf);
      }
      super.kill();
    }
  }

  @Override
  public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
    this.setPosition(x, this.getY(), z);
    this.serverY = y;
    this.interpolationSteps = interpolationSteps;
  }

  protected void spawnMovementParticles(Vec3d offset) {
    for (int i = 0; i < 5; i++) {
      double rx = (this.random.nextDouble() - 0.5D) * 0.5D;
      double rz = (this.random.nextDouble() - 0.5D) * 0.5D;
      ParticleUtils.addParticle((ClientWorld) this.world,
        ParticleTypes.ELECTRIC_SPARK,
        this.getPos().add(offset),
        new Vec3d(rx, this.isGoingUp() ? -1.0D : 1.0D, rz));
    }
  }

  public void handlePassengers() {
    if (this.isMoving()) {
      this.addPassengers();
    } else if (this.stoppedTicks > 1) {
      this.removeAllPassengers();
      this.hitEntities.clear();
    }
  }

  public void addPassengers() {
    List<LivingEntity> entities = this.world.getEntitiesByClass(
      LivingEntity.class,
      this.getBoundingBox().expand(0.0D, 0.5D, 0.0D),
      this::canBePassenger);

    for (LivingEntity e : entities) {
      if (e instanceof PathAwareEntity pathAwareEntity) {
        pathAwareEntity.getNavigation().stop();
      }
      e.startRiding(this);
    }
  }

  protected boolean canBePassenger(LivingEntity entity) {
    return !this.hitEntities.contains(entity)
      && entity.getY() > this.getY()
      && !entity.isSneaking()
      && !(entity instanceof SewersTentacleEntity);
  }

  public void handleEntitiesBelow() {
    List<LivingEntity> entities = this.world.getEntitiesByClass(
      LivingEntity.class,
      this.getBoundingBox().offset(0.0D, -1.0D, 0.0D),
      e -> !this.hitEntities.contains(e));

    for (LivingEntity e : entities) {
      if (!this.hitEntities.contains(e)) {
        e.setVelocity(e.getPos()
          .subtract(this.getPos())
          .normalize()
          .multiply(3.0D, 0.0D, 3.0D)
          .add(0.0D, 0.5D, 0.0D));
        e.damage(MineCellsDamageSource.ELEVATOR, this.damage);
        this.hitEntities.add(e);
      }
    }
  }

  public void handleRedstone() {
    boolean top = this.checkSignal(this.getMaxY());
    boolean bottom = this.checkSignal(this.getMinY());

    if (top && !this.poweredTop && !this.isGoingUp()) {
      startMoving(true, true);
    } else if (bottom & !this.poweredBottom && this.isGoingUp()) {
      startMoving(false, true);
    }

    this.poweredTop = top;
    this.poweredBottom = bottom;
  }

  protected boolean checkSignal(int y) {
    final Vec3i[] offsets = {
      new Vec3i(-2, 0, 0),
      new Vec3i(0, 0, -2),
      new Vec3i(0, 0, 2),
      new Vec3i(2, 0, 0),
      new Vec3i(-2, 1, 0),
      new Vec3i(0, 1, -2),
      new Vec3i(0, 1, 2),
      new Vec3i(2, 1, 0)
    };

    BlockPos pos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
    for (Vec3i offset : offsets) {
      if (this.world.getBlockState(pos.add(offset)).getStrongRedstonePower(world, pos, Direction.UP) > 0) {
        return true;
      }
    }
    return false;
  }

  public static boolean validateShaft(World world, int x, int z, int minY, int maxY, boolean rotated) {
    // Box box = new Box(x - 2.0D, minY - 1.0D, z - 2.0D, x + 3.0D, maxY + 1.0D, x + 3.0D);

    final Vec3i[] offsets = {
      new Vec3i(-1, 0, -1),
      new Vec3i(-1, 0, 0), // [1] West
      new Vec3i(-1, 0, 1),
      new Vec3i(0, 0, -1), // [3] North
      new Vec3i(0, 0, 0),
      new Vec3i(0, 0, 1), // [5] South
      new Vec3i(1, 0, -1),
      new Vec3i(1, 0, 0), // [7] East
      new Vec3i(1, 0, 1),
    };
    int chain0 = 1;
    int chain1 = 7;
    if (rotated) {
      chain0 = 3;
      chain1 = 5;
    }
    // Check if certain blocks are air and chains
    for (int i = 0; i < 9; i++) {
      boolean chain = i == chain0 || i == chain1;
      for (int y = minY; y <= maxY + 1; y++) {
        // Skip assembler positions
        if (i == 4 && (y == minY || y == maxY)) {
          continue;
        }
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos offsetPos = pos.add(offsets[i]);
        BlockState state = world.getBlockState(offsetPos);
        if (chain) {
          if (
            !(state.isIn(MineCellsBlockTags.ELEVATOR_CHAINS) && state.getProperties().contains(ChainBlock.AXIS))
              || state.get(ChainBlock.AXIS) != Direction.Axis.Y
          ) {
            return false;
          }
        } else if (state.isSolidBlock(world, offsetPos)) {
          return false;
        }
      }
    }

    return true;
  }

  public boolean startMoving(boolean isGoingUp, boolean fromRedstone) {
    if ((!this.isMoving() || fromRedstone)
      && validateShaft(this.world, this.getBlockX(), this.getBlockZ(), this.getMinY(), this.getMaxY(), this.isRotated())) {
      if (!this.world.isClient() && (this.stoppedTicks > 5 || fromRedstone)) {
        this.setGoingUp(isGoingUp);
        if (!this.isMoving() && this.stoppedTicks > 5) {
          this.setVelocityModifier(0.0F);
          this.playSound(MineCellsSounds.ELEVATOR_START, 1.0F, 1.0F);
        }
        this.setMoving(true);
      }
      return true;
    }
    return false;
  }

  @Override
  public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
    return interact(player, hand);
  }

  @Override
  public ActionResult interact(PlayerEntity player, Hand hand) {
    boolean result = startMoving(!this.isGoingUp(), false);
    if (result && !this.world.isClient()) {
      this.addPassengers();
    }
    return result ? ActionResult.SUCCESS : ActionResult.FAIL;
  }

  @Nullable
  @Override
  public ItemStack getPickBlockStack() {
    return new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER.asItem());
  }

  @Override
  public void updatePassengerPosition(Entity passenger) {
    passenger.setPosition(passenger.prevX, this.getY() + 0.5D, passenger.prevZ);
  }

  @Override
  public Vec3d updatePassengerForDismount(LivingEntity passenger) {
    if (this.isMoving()) {
      return new Vec3d(passenger.prevX, this.getY(), passenger.prevZ);
    }
    return new Vec3d(passenger.prevX, this.getY() + 0.5D, passenger.prevZ);
  }

  @Override
  protected boolean canAddPassenger(Entity passenger) {
    return true;
  }

  @Override
  public void move(MovementType movementType, Vec3d movement) {
    if (movementType != MovementType.SELF) {
      return;
    }
    super.move(movementType, movement);
  }

  @Override
  public boolean isCollidable() {
    return !this.isMoving();
  }

  @Override
  public boolean collidesWith(Entity other) {
    return true;
  }

  @Override
  public boolean canHit() {
    return true;
  }

  public boolean isMoving() {
    return this.dataTracker.get(MOVING);
  }

  public void setMoving(boolean moving) {
    this.dataTracker.set(MOVING, moving);
  }

  public boolean isGoingUp() {
    return this.dataTracker.get(GOING_UP);
  }

  public void setGoingUp(boolean goingUp) {
    this.dataTracker.set(GOING_UP, goingUp);
  }

  public boolean isRotated() {
    return this.dataTracker.get(ROTATED);
  }

  public void setRotated(boolean rotated) {
    this.dataTracker.set(ROTATED, rotated);
  }

  public float getVelocityModifier() {
    return this.dataTracker.get(VELOCITY_MODIFIER);
  }

  public void setVelocityModifier(float velocityModifier) {
    this.dataTracker.set(VELOCITY_MODIFIER, velocityModifier);
  }

  public int getMaxY() {
    return this.dataTracker.get(MAX_Y);
  }

  public void setMaxY(int maxY) {
    this.dataTracker.set(MAX_Y, maxY);
  }

  public int getMinY() {
    return this.dataTracker.get(MIN_Y);
  }

  public void setMinY(int minY) {
    this.dataTracker.set(MIN_Y, minY);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    this.setGoingUp(nbt.getBoolean("up"));
    this.setMinY(nbt.getInt("minY"));
    this.setMaxY(nbt.getInt("maxY"));
    this.setRotated(nbt.getBoolean("rotated"));
    this.setup = nbt.getBoolean("setup");
    this.poweredTop = nbt.getBoolean("poweredTop");
    this.poweredBottom = nbt.getBoolean("poweredBottom");
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putBoolean("up", this.isGoingUp());
    nbt.putInt("minY", this.getMinY());
    nbt.putInt("maxY", this.getMaxY());
    nbt.putBoolean("rotated", this.isRotated());
    nbt.putBoolean("setup", this.setup);
    nbt.putBoolean("poweredTop", this.poweredTop);
    nbt.putBoolean("poweredBottom", this.poweredBottom);
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
