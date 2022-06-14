package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.ShootGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UndeadArcherEntity extends MineCellsEntity implements IShootEntity {

    private static final TrackedData<Boolean> SHOOT_CHARGING = DataTracker.registerData(UndeadArcherEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOOT_RELEASING = DataTracker.registerData(UndeadArcherEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> SHOOT_COOLDOWN = DataTracker.registerData(UndeadArcherEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public UndeadArcherEntity(EntityType<UndeadArcherEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0D));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(0, new UndeadArcherShootGoal(this, 20, 25, 0.5F));
        this.goalSelector.add(1, new WalkTowardsTargetGoal(this, 1.0D, false, 3.0F));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SHOOT_CHARGING, false);
        this.dataTracker.startTracking(SHOOT_RELEASING, false);
        this.dataTracker.startTracking(SHOOT_COOLDOWN, 0);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        EntityData result = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        this.setStackInHand(this.getActiveHand(), Items.BOW.getDefaultStack());
        this.setLeftHanded(false);
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(SHOOT_COOLDOWN);
    }

    public static DefaultAttributeContainer.Builder createUndeadArcherAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D);
    }

    @Override
    public boolean isShootCharging() {
        return this.dataTracker.get(SHOOT_CHARGING);
    }

    @Override
    public void setShootCharging(boolean charging) {
        this.setAttacking(charging);
        this.dataTracker.set(SHOOT_CHARGING, charging);
    }

    @Override
    public boolean isShootReleasing() {
        return this.dataTracker.get(SHOOT_RELEASING);
    }

    @Override
    public void setShootReleasing(boolean releasing) {
        this.dataTracker.set(SHOOT_RELEASING, releasing);
    }

    @Override
    public int getShootCooldown() {
        return this.dataTracker.get(SHOOT_COOLDOWN);
    }

    @Override
    public void setShootCooldown(int ticks) {
        this.dataTracker.set(SHOOT_COOLDOWN, ticks);
    }

    @Override
    public int getShootMaxCooldown() {
        return 20;
    }

    @Override
    public SoundEvent getShootChargeSoundEvent() {
        return SoundRegistry.BOW_CHARGE;
    }

    @Override
    public SoundEvent getShootReleaseSoundEvent() {
        return SoundRegistry.BOW_RELEASE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("shootCooldown", this.getShootCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setShootCooldown(nbt.getInt("shootCooldown"));
    }

    public static class UndeadArcherShootGoal extends ShootGoal<UndeadArcherEntity> {
        public UndeadArcherShootGoal(UndeadArcherEntity entity, int actionTick, int lengthTicks, float chance) {
            super(entity, actionTick, lengthTicks, chance);
        }

        @Override
        public boolean canStart() {
            return this.entity.getMainHandStack().getItem() == Items.BOW && super.canStart();
        }

        @Override
        public void shoot(LivingEntity target) {
            super.shoot(target);
            PersistentProjectileEntity persistentProjectileEntity = new ArrowEntity(this.entity.world, this.entity);
            double d = target.getX() - this.entity.getX();
            double e = target.getBodyY(0.33D) - persistentProjectileEntity.getY();
            double f = target.getZ() - this.entity.getZ();
            double g = Math.sqrt(d * d + f * f);
            persistentProjectileEntity.setVelocity(d, e + g * 0.2D, f, 1.6F, 1.0F);
            this.entity.world.spawnEntity(persistentProjectileEntity);
        }

        @Override
        public void tick() {
            super.tick();
            this.entity.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.entity, Items.BOW));
            if (this.entity.isShootReleasing()) {
                this.entity.clearActiveItem();
            }
        }
    }
}
