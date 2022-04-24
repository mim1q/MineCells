package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
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

    public enum Variants {
        NORMAL, BIG, SEWER
    }

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> VARIANT = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.STRING);


    public GrenadeEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setFuse(60);
    }

    public static void spawnGrenade(World world, Variants variant, double x, double y, double z, double velX, double velY, double velZ) {
        GrenadeEntity grenade = new GrenadeEntity(EntityRegistry.GRENADE, world);
        grenade.setFuse(60);
        grenade.setPos(x, y, z);
        grenade.setVariant(variant.name());
        grenade.setVelocity(velX, velY, velZ);
        world.spawnEntity(grenade);
    }

    public static void spawnGrenade(World world, Variants variant, double x, double y, double z) {
        Vec3d velocity = new Vec3d(world.random.nextDouble() - 0.5D, world.random.nextDouble(), world.random.nextDouble() - 0.5D).multiply(0.5D, 0.5D, 0.5D);
        spawnGrenade(world, variant, x, y, z, velocity.x, velocity.y, velocity.z);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 60);
        this.dataTracker.startTracking(VARIANT, Variants.NORMAL.name());
    }

    @Override
    public void tick() {
        this.addVelocity(0.0, -0.04, 0.0);
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7D, 0.0D, 0.7D));
        }
        this.move(MovementType.SELF, this.getVelocity());

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse < 0 && !this.world.isClient()) {
            this.explode();
            this.discard();
        }
    }

    public void explode() {
        if (this.getVariant().equals(Variants.BIG.name())) {
            for (int i = 0; i < 3; i++) {
                spawnGrenade(this.world, Variants.NORMAL, this.getX(), this.getY(), this.getZ());
            }
        }
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 2.0F, Explosion.DestructionType.NONE);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public String getVariant() { return this.dataTracker.get(VARIANT); }

    public void setVariant(String variant) { this.dataTracker.set(VARIANT, variant); }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getInt("fuse"));
        this.setVariant(nbt.getString("variant"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse());
        nbt.putString("variant", this.getVariant());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
