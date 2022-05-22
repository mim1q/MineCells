package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEntity extends Entity {

    private static final TrackedData<Boolean> IS_MOVING = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_GOING_UP = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_ROTATED = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SPEED = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> MIN_Y = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_Y = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected double serverY;
    protected int interpolationSteps = 0;
    protected boolean setup = false;
    boolean wasMoving = false;

    boolean poweredTop = false;
    boolean poweredBottom = false;

    protected ArrayList<PlayerEntity> usingPlayers = new ArrayList<>();
    protected ArrayList<LivingEntity> hitEntities = new ArrayList<>();

    public ElevatorEntity(EntityType<ElevatorEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        this.noClip = true;
        this.serverY = this.getY();
    }

    public static void spawn(World world, int x, int z, int minY, int maxY, boolean isRotated, boolean isGoingUp) {
        ElevatorEntity elevator = new ElevatorEntity(EntityRegistry.ELEVATOR, world);
        elevator.setPosition(x + 0.5D, isGoingUp ? maxY : minY, z + 0.5D);
        elevator.setMaxY(maxY);
        elevator.setMinY(minY);
        elevator.setIsRotated(isRotated);
        elevator.setIsGoingUp(isGoingUp);
        world.spawnEntity(elevator);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_MOVING, false);
        this.dataTracker.startTracking(IS_GOING_UP, false);
        this.dataTracker.startTracking(IS_ROTATED, false);
        this.dataTracker.startTracking(SPEED, 0.0F);
        this.dataTracker.startTracking(MIN_Y, (int)this.getY());
        this.dataTracker.startTracking(MAX_Y, (int)this.getY());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isLogicalSideForUpdatingMovement()) {
            double targetYv = this.getIsGoingUp() ? 1.0D : -1.0D;
            this.setSpeed(Math.min(this.getSpeed() + (this.getIsGoingUp() ? 0.01F : 0.01F), 1.0F));
            this.setVelocity(0.0D, targetYv * this.getSpeed(), 0.0D);
            this.velocityDirty = true;
            this.velocityModified = true;

            double nextY = this.getY() + this.getVelocity().y;
            boolean isMoving = !(nextY < this.getMinY() || nextY > this.getMaxY());

            if (getIsMoving()) {
                if (!isMoving) {
                    this.playSound(SoundRegistry.ELEVATOR_STOP, 0.5F, 1.0F);
                }
            } else {
                this.handleRedstone();
            }
            this.setIsMoving(isMoving);

            this.interpolationSteps = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());

        } else {
            if (wasMoving && !getIsMoving() && !this.getIsGoingUp()) {
                BlockPos pos = new BlockPos(this.getBlockX(), this.getMinY() - 1, this.getBlockZ());
                BlockState state = this.world.getBlockState(pos);
                ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
                if (!state.isAir()) {
                    for (int i = 0; i < 20; i++) {
                        double rx = this.random.nextDouble() - 0.5D;
                        double rz = this.random.nextDouble() - 0.5D;
                        Vec3d vel = new Vec3d(rx, 0.1D, rz).normalize();
                        ParticleHelper.addParticle((ClientWorld)this.world, particle, this.getPos().add(vel), vel.multiply(10.0D));
                        ParticleHelper.addParticle((ClientWorld)this.world, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getPos().add(vel), vel.multiply(0.01D));
                    }
                }
            }
            this.wasMoving = this.getIsMoving();
        }

        if (this.interpolationSteps > 0) {
            double d = this.getX();
            double e = this.getY() + (this.serverY - this.getY()) / (double)this.interpolationSteps;
            double f = this.getZ();
            this.interpolationSteps--;
            this.setPosition(d, e, f);
        }

        double clampedY = MathHelper.clamp(this.getY() + this.getVelocity().y, this.getMinY(), this.getMaxY());
        this.setPosition(this.getX(), clampedY, this.getZ());

        if (this.getIsMoving()) {
            if (this.world.isClient()) {
                double z = this.getIsRotated() ? 1.0D : 0.0D;
                double x = 1.0D - z;
                    this.spawnParticles(new Vec3d(-x, 0.0D, -z));
                    this.spawnParticles(new Vec3d(x, 0.0D, z));
            }
            if (this.isLogicalSideForUpdatingMovement() && !this.getIsGoingUp()) {
                this.handleEntitiesBelow();
            }
        }

        this.handlePlayers();
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, this.getY(), z);
        this.serverY = y;
        this.interpolationSteps = interpolationSteps;
    }

    protected void spawnParticles(Vec3d offset) {
        for (int i = 0; i < 5; i++) {
            double rx = (this.random.nextDouble() - 0.5D) * 0.5D;
            double rz = (this.random.nextDouble() - 0.5D) * 0.5D;
            ParticleHelper.addParticle((ClientWorld)this.world,
                    ParticleTypes.ELECTRIC_SPARK,
                    this.getPos().add(offset),
                    new Vec3d(rx, this.getIsGoingUp() ? -1.0D : 1.0D, rz));
        }
    }

    public void handlePlayers() {
        // Update riding players' positions
        for (PlayerEntity e : this.usingPlayers) {
            e.setPosition(e.prevX, this.getY() + 0.5D, e.prevZ);
            e.setVelocityClient(0.0D, this.getIsGoingUp() ? this.getVelocity().y : 0.0D, 0.0D);
            e.velocityDirty = true;
            e.fallDistance = 0.0F;
            e.setOnGround(true);
        }

        if (this.getIsMoving()) {
            // Add riding players
            List<PlayerEntity> players = this.world.getEntitiesByClass(
                    PlayerEntity.class,
                    this.getBoundingBox().expand(0.0D, 1.0D, 0.0D),
                    e -> !this.usingPlayers.contains(e));

            for (PlayerEntity e : players) {
                if (!this.usingPlayers.contains(e)) {
                    this.usingPlayers.add(e);
                }
            }
        } else {
            // Remove players when elevator stops
            for (PlayerEntity e : this.usingPlayers) {
                e.setVelocity(Vec3d.ZERO);
            }
            this.usingPlayers.clear();
            this.hitEntities.clear();
        }
    }

    public void handleEntitiesBelow() {
        List<LivingEntity> entities = this.world.getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().offset(0.0D, -1.0D, 0.0D),
                e -> !this.hitEntities.contains(e));

        for (LivingEntity e : entities) {
            if (!this.hitEntities.contains(e) && e.getVelocity().y >= -0.1F) {
                e.setVelocity(e.getPos()
                        .subtract(this.getPos())
                        .normalize()
                        .multiply(5.0D, 0.0D, 5.0D)
                        .add(0.0D, 0.5D, 0.0D));
                e.damage(DamageSource.ANVIL, 10.0F);
                this.hitEntities.add(e);
            }
        }
    }

    public void handleRedstone() {

        boolean top = this.checkSignal(this.getMaxY());
        boolean bottom = this.checkSignal(this.getMinY());

        if (!this.getIsMoving()) {
            if (top && !this.poweredTop && !this.getIsGoingUp()){
                startMoving(true);
            } else if (bottom & !this.poweredBottom && this.getIsGoingUp()) {
                startMoving(false);
            }
        }

        this.poweredTop = top;
        this.poweredBottom = bottom;
    }

    protected boolean checkSignal(int y) {
        final Vec3i[] offsets = {
                new Vec3i(-2, 0,  0),
                new Vec3i( 0, 0, -2),
                new Vec3i( 0, 0,  2),
                new Vec3i( 2, 0,  0),
                new Vec3i(-2, 1,  0),
                new Vec3i( 0, 1, -2),
                new Vec3i( 0, 1,  2),
                new Vec3i( 2, 1,  0)
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
        final Vec3i[] offsets = {
                new Vec3i(-1, 0, -1),
                new Vec3i(-1, 0,  0), // [1] West
                new Vec3i(-1, 0,  1),
                new Vec3i( 0, 0, -1), // [3] North
                new Vec3i( 0, 0,  0),
                new Vec3i( 0, 0,  1), // [5] South
                new Vec3i( 1, 0, -1),
                new Vec3i( 1, 0,  0), // [7] East
                new Vec3i( 1, 0,  1),
        };
        int chain0 = 1;
        int chain1 = 7;
        if (rotated) {
            chain0 = 3;
            chain1 = 5;
        }
        // Check if ceretain blocks are air and chains
        for (int i = 0; i < 9; i++) {
            boolean chain = i == chain0 || i == chain1;
            for (int y = minY; y <= maxY; y++) {
                // Skip assembler positions
                if (i == 4 && (y == minY || y == maxY)) {
                    continue;
                }
                BlockPos pos = new BlockPos(x, y, z);
                BlockPos offsetPos = pos.add(offsets[i]);
                BlockState state = world.getBlockState(offsetPos);
                if (chain) {
                    if (!(state.getBlock() instanceof ChainBlock) || state.get(ChainBlock.AXIS) != Direction.Axis.Y) {
                        return false;
                    }
                } else if (!state.getCollisionShape(world, pos).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean startMoving(boolean isGoingUp) {
        if (!this.getIsMoving() && validateShaft(this.world, this.getBlockX(), this.getBlockZ(), this.getMinY(), this.getMaxY(), this.getIsRotated())) {
            if (!this.world.isClient()) {
                this.setIsGoingUp(isGoingUp);
                this.setIsMoving(true);
                this.setSpeed(0.0F);
                this.playSound(SoundRegistry.ELEVATOR_START, 0.5F, 1.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        boolean result = startMoving(!this.getIsGoingUp());
        return result ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (movementType != MovementType.SELF) {
            return;
        }
        super.move(movementType, movement);
    }

    public boolean getIsMoving() {
        return this.dataTracker.get(IS_MOVING);
    }

    public void setIsMoving(boolean isMoving) {
        this.dataTracker.set(IS_MOVING, isMoving);
    }

    public boolean getIsGoingUp() {
        return this.dataTracker.get(IS_GOING_UP);
    }

    public void setIsGoingUp(boolean isGoingUp) {
        this.dataTracker.set(IS_GOING_UP, isGoingUp);
    }

    public boolean getIsRotated() {
        return this.dataTracker.get(IS_ROTATED);
    }

    public void setIsRotated(boolean isRotated) {
        this.dataTracker.set(IS_ROTATED, isRotated);
    }

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }

    public void setSpeed(float speed) {
        this.dataTracker.set(SPEED, speed);
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
    public boolean isCollidable() {
        return !this.getIsMoving();
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setIsGoingUp(nbt.getBoolean("up"));
        this.setMinY(nbt.getInt("minY"));
        this.setMaxY(nbt.getInt("maxY"));
        this.setIsRotated(nbt.getBoolean("rotated"));
        this.setup = nbt.getBoolean("setup");
        this.poweredTop = nbt.getBoolean("poweredTop");
        this.poweredBottom = nbt.getBoolean("poweredBottom");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("up", this.getIsGoingUp());
        nbt.putInt("minY", this.getMinY());
        nbt.putInt("maxY", this.getMaxY());
        nbt.putBoolean("rotated", this.getIsRotated());
        nbt.putBoolean("setup", this.setup);
        nbt.putBoolean("poweredTop", this.poweredTop);
        nbt.putBoolean("poweredBottom", this.poweredBottom);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
