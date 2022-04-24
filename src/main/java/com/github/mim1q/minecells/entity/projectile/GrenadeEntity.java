package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.TntEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class GrenadeEntity extends Entity {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
//    private static final TrackedData<Integer> TYPE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);


    public GrenadeEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setFuse(60);
    }

    public static void spawnGrenade(World world, double x, double y, double z) {
        GrenadeEntity grenade = new GrenadeEntity(EntityRegistry.GRENADE, world);
        grenade.setFuse(60);
        grenade.setPos(x, y, z);
        world.spawnEntity(grenade);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 60);
    }

    @Override
    public void tick() {
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        System.out.println(fuse);
        if (fuse < 0 && !this.world.isClient()) {
            this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.0F, Explosion.DestructionType.NONE);
            this.discard();
        }
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getInt("fuse"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
