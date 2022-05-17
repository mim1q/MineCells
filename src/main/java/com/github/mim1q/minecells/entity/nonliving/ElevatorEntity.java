package com.github.mim1q.minecells.entity.nonliving;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ElevatorEntity extends Entity {

    private static final TrackedData<Boolean> IS_MOVING = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_GOING_UP = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SPEED = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected ArrayList<PlayerEntity> usingPlayers = new ArrayList<>();
    protected ArrayList<LivingEntity> hitEntities = new ArrayList<>();

    private final int minY = 53;
    private final int maxY = 73;

    public ElevatorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_MOVING, false);
        this.dataTracker.startTracking(IS_GOING_UP, false);
        this.dataTracker.startTracking(SPEED, 0.0F);
    }

    @Override
    public void tick() {
        double y = this.getY() + this.getVelocity().y;
        this.move(MovementType.SELF, this.getVelocity());
        double clampedY = MathHelper.clamp(this.getY(), this.minY, this.maxY);
        this.setPosition(this.getX(), clampedY, this.getZ());

        if (!this.world.isClient()) {
            boolean isMoving = !(y < this.minY || y > this.maxY);
            this.setIsMoving(isMoving);
            double targetYv = this.getIsGoingUp() ? 5.0D : -5.0D;
            this.setSpeed(Math.min(this.getSpeed() + (this.getIsGoingUp() ? 0.005F : 0.005F), 1.0F));
            this.setVelocity(0.0D, targetYv * this.getSpeed(), 0.0D);
        }
        if (!this.world.isClient() && this.getIsMoving() && !this.getIsGoingUp()) {
            this.handleEntitiesBelow();
        }

        this.handlePlayers();
        super.tick();
    }

    public void handlePlayers() {
        // Update riding players' positions
        for (PlayerEntity e : this.usingPlayers) {
            e.setVelocity(this.getIsGoingUp() ? this.getVelocity() : Vec3d.ZERO);
            e.setPosition(e.prevX, this.getY() + 0.5D, e.prevZ);
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
            this.setVelocity(0.0D, MathHelper.clamp(this.getVelocity().y, -1.0D, 1.0D), 0.0D);
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

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        this.setIsGoingUp(!this.getIsGoingUp());
        this.setIsMoving(true);
        this.setSpeed(0.0F);
        return ActionResult.SUCCESS;
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

    public float getSpeed() {
        return this.dataTracker.get(SPEED);
    }

    public void setSpeed(float speed) {
        this.dataTracker.set(SPEED, speed);
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
        this.setIsGoingUp(nbt.getBoolean("Up"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Up", getIsGoingUp());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
