package com.github.mim1q.minecells.entity.nonliving;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
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

public class ElevatorEntity extends Entity {

    private static final TrackedData<Boolean> IS_MOVING = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_GOING_UP = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected ArrayList<Entity> usingEntities = new ArrayList<>();

    private final int MIN_Y = 53;
    private final int MAX_Y = 73;

    public ElevatorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_MOVING, false);
        this.dataTracker.startTracking(IS_GOING_UP, false);
    }

    @Override
    public void tick() {
        this.move(MovementType.SELF, this.getVelocity());

        boolean up = this.getIsGoingUp();
        double yv = MathHelper.lerp(0.02F, this.getVelocity().y, up ? 1.5F : -1.5F);

        if (!this.world.isClient()) {
            boolean moving = this.getIsMoving();
            if (this.getY() < this.MIN_Y) {
                this.setPos(this.getX(), this.MIN_Y, this.getZ());
                moving = false;
            } else if (this.getY() > this.MAX_Y) {
                this.setPos(this.getX(), this.MAX_Y, this.getZ());
                moving = false;
            }
            this.setIsMoving(moving);
        }

        if (this.getIsMoving()) {
            for (Entity e : this.usingEntities) {
                e.fallDistance = 0;
                e.setVelocityClient(0.0D, yv, 0.0D);
                e.setOnGround(true);

                if (e instanceof PlayerEntity && (!world.isClient() || ((PlayerEntity)e).isMainPlayer())) {
                    e.setPos(e.prevX, this.getY() + 0.5D, e.prevZ);
                } else if (!this.world.isClient()) {
                    e.setPosition(e.prevX, this.getY() + 0.5D + yv + e.getY() - e.prevY, e.prevZ);
                }
            }
            for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox().offset(0.0D, 1.0D, 0.0D))){
                if (!this.usingEntities.contains(e)) {
                    this.usingEntities.add(e);
                }
            }
        } else {
            this.setVelocity(Vec3d.ZERO);
            for (Entity e : usingEntities) {
                e.setVelocity(Vec3d.ZERO);
                e.setPos(e.getX(), this.getY() + 0.6D, e.getZ());
                //e.setPos(e.getX(), e.getY() - yv - e.getY() + e.prevY, e.getZ());
            }
            this.usingEntities.clear();
        }
        if (!this.world.isClient() && this.getIsMoving()) {
            this.setVelocity(0.0D, yv, 0.0D);
        }

        super.tick();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        this.setIsGoingUp(!this.getIsGoingUp());
        this.setIsMoving(true);
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
