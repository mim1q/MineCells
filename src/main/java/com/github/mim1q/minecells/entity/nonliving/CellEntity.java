package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class CellEntity extends Entity {

    protected PlayerEntity target;
    protected boolean bound = false;

    public CellEntity(EntityType<CellEntity> type, World world) {
        super(type, world);
        this.setPosition(this.getX() + this.random.nextFloat(), this.getY(), this.getZ() + this.random.nextFloat());
    }

    @Override
    protected void initDataTracker() { }

    @Override
    public void tick() {
        super.tick();
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (!this.world.isClient()) {
            if (!this.bound && this.age % 20 == 1) {
                this.target = this.world.getClosestPlayer(this, 10.0D);
            }
            if (this.target != null && this.target.isAlive() && this.target.distanceTo(this) <= 10.0D) {
                double distance = this.target.distanceTo(this);
                double multiplier = distance == 0.0D
                    ? 1.0D
                    : 1.0D / distance;
                this.setVelocity(this.target.getPos()
                    .add(0.0D, 0.5D, 0.0D)
                    .subtract(this.getPos())
                    .normalize()
                    .multiply(0.5D * multiplier)
                );
                if (this.target.getBoundingBox().contains(this.getBoundingBox().getCenter())) {
                    PlayerEntityAccessor target = (PlayerEntityAccessor)this.target;
                    target.setCells(target.getCells() + 1);
                    this.discard();
                }
            }
            this.setVelocity(this.getVelocity().multiply(0.95D));
            this.addVelocity(0.0D, -0.01D, 0.0D);
            this.velocityModified = true;
        }
        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("target")) {
            this.target = this.world.getPlayerByUuid(nbt.getUuid("target"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.target != null) {
            nbt.putUuid("target", this.target.getUuid());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
