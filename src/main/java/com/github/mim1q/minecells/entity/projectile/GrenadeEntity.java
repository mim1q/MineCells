package com.github.mim1q.minecells.entity.projectile;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

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
                this.explode(3.0F);
                this.discard();
            }
            this.addVelocity(0.0D, -0.04D, 0.0D);
        }
        this.move(MovementType.SELF, this.getVelocity());
    }

    public void explode(float power) {
        this.world.createExplosion(this, new GrenadeDamageSource(), null, this.getX(), this.getY(), this.getZ(), power, false, Explosion.DestructionType.NONE);
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
        this.setFuse(nbt.getInt("Fuse"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Fuse", this.getFuse());
    }

    public static class GrenadeDamageSource extends DamageSource {

        protected GrenadeDamageSource() {
            super("minecellsGrenade");
            this.setExplosive();
        }
    }
}
