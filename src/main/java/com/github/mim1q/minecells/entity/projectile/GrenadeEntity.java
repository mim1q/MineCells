package com.github.mim1q.minecells.entity.projectile;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class GrenadeEntity extends ProjectileEntity {
    private Vec3d shootVector;
    private boolean shouldResetVelocity = false;

    private static final TrackedData<Integer> FUSE = DataTracker.registerData(GrenadeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public GrenadeEntity(EntityType<? extends GrenadeEntity> type, World world) {
        super(type, world);
    }

    public void shoot(Vec3d v) {
        this.shouldResetVelocity = true;
        shootVector = v;
    }

    public int getMaxFuse() {
        return 15 + this.random.nextInt(10);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, this.getMaxFuse());
    }

    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public void tick() {
        if (this.shouldResetVelocity) {
            this.shouldResetVelocity = false;
            this.setVelocity(shootVector);
        }

        int fuse = this.getFuse() - 1;
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7D, 0.0D, 0.7D));
            this.setFuse(fuse);
        }
        if (!this.world.isClient) {
            if (fuse <= 0) {
                this.explode();
                this.discard();
            }
            this.addVelocity(0.0D, -0.04D, 0.0D);
        }
        this.move(MovementType.SELF, this.getVelocity());
    }

    public void explode() {
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3.0F, Explosion.DestructionType.NONE);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setFuse(nbt.getInt("fuse"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("fuse", this.getFuse());
    }
}
