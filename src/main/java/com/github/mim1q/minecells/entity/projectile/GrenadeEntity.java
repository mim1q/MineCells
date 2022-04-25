package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class GrenadeEntity extends Entity {

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public GrenadeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public static<E extends GrenadeEntity> void spawn(E entity, double x, double y, double z, double velX, double velY, double velZ) {
        entity.setFuse(entity.getMaxFuse());
        entity.setPos(x, y, z);
        entity.setVelocity(velX, velY, velZ);
        entity.world.spawnEntity(entity);
    }

    public static<E extends GrenadeEntity> void spawn(E entity, double x, double y, double z) {
        Vec3d velocity = new Vec3d(entity.world.random.nextDouble() - 0.5D, entity.world.random.nextDouble(), entity.world.random.nextDouble() - 0.5D).multiply(0.3D, 0.5D, 0.3D);
        spawn(entity, x, y, z, velocity.x, velocity.y, velocity.z);
    }

    public int getMaxFuse() {
        return 50 + this.random.nextInt(20);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 60);
    }

    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public void tick() {
        this.addVelocity(0.0D, -0.04D, 0.0D);
        this.move(MovementType.SELF, this.getVelocity());
        if (this.isOnGround()) {
            System.out.println("AAAA");
            this.setVelocity(this.getVelocity().multiply(0.7D, 1.0D, 0.7D));
        }
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (!this.world.isClient && fuse <= 0) {
            this.explode();
            this.discard();
        }
    }

    public void explode() {
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.5F, Explosion.DestructionType.NONE);
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
