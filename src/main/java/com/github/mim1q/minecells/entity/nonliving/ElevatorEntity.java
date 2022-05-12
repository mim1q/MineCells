package com.github.mim1q.minecells.entity.nonliving;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ElevatorEntity extends Entity {
    private static final TrackedData<Boolean> IS_MOVING = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_GOING_UP = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> TARGET_VELOCITY = DataTracker.registerData(ElevatorEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public ElevatorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        //this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_MOVING, false);
        this.dataTracker.startTracking(IS_GOING_UP, false);
        this.dataTracker.startTracking(TARGET_VELOCITY, 0.0F);
    }

    @Override
    public void tick() {
        this.move(MovementType.SELF, this.getVelocity());
        super.tick();

        boolean moving = this.getIsMoving();
        boolean up = this.getIsGoingUp();
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setTargetVelocity(up ? 2.0F : -2.0F);
            double yv = MathHelper.lerp(0.01F, this.getVelocity().y, this.getTargetVelocity());
            this.setVelocityClient(0.0D, yv, 0.0D);
        }
        if (moving) {
            for (Entity e : this.world.getOtherEntities(this, this.getBoundingBox().expand(0.0D, 2.0D, 0.0D).shrink(0.1D, 0.0D, 0.1D))) {
                e.move(MovementType.SELF, new Vec3d(0.0D, (this.getY() + 0.5D) - e.getY(), 0.0D));
                e.setVelocityClient(e.getVelocity().x, up ? 0.0D : -10.0D, e.getVelocity().z);
                e.setOnGround(true);
                e.setAir(0);
            }
        }

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

    public float getTargetVelocity() {
        return this.dataTracker.get(TARGET_VELOCITY);
    }

    public void setTargetVelocity(float velocity) {
        this.dataTracker.set(TARGET_VELOCITY, velocity);
    }

    @Override
    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        if (this.world.getBlockState(pos.west()).getBlock() instanceof ChainBlock) {
            return false;
        }
        return collidesWithStateAtPos(pos, state);
    }

    @Override
    public boolean isCollidable() {
        return !(this.getIsMoving() & this.getIsGoingUp());
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
