package com.github.mim1q.minecells.entity.nonliving;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class CellEntity extends Entity {

    public CellEntity(EntityType<CellEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() { }

    @Override
    public void tick() {
        super.tick();
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
