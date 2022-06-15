package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.DashGoal;
import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MutatedBatEntity extends MineCellsEntity implements IDashEntity {

    private static final TrackedData<Boolean> DASH_CHARGING = DataTracker.registerData(MutatedBatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DASH_RELEASING = DataTracker.registerData(MutatedBatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> DASH_COOLDOWN = DataTracker.registerData(MutatedBatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public MutatedBatEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FlightMoveControl(this, 0, true);
        this.setNoGravity(true);
    }

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DASH_CHARGING, false);
        this.dataTracker.startTracking(DASH_RELEASING, false);
        this.dataTracker.startTracking(DASH_COOLDOWN, 0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new FlyGoal(this, 1.0D));
        this.goalSelector.add(0, new DashGoal<>(this, 20, 40, 45, 1.0F, 1.0F));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 3.0D, false));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDashCharging() && this.world.isClient()) {
            for (int i = 0; i < 5; i++){
                ParticleHelper.addParticle((ClientWorld) this.world, ParticleRegistry.CHARGE, this.getPos().add(0.0D, 0.2D, 0.0D), Vec3d.ZERO);
            }
        }

        this.decrementCooldown(DASH_COOLDOWN);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new MutatedBatNavigation(this, world);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean isDashCharging() {
        return this.dataTracker.get(DASH_CHARGING);
    }

    @Override
    public void setDashCharging(boolean charging) {
        this.dataTracker.set(DASH_CHARGING, charging);
    }

    @Override
    public boolean isDashReleasing() {
        return this.dataTracker.get(DASH_RELEASING);
    }

    @Override
    public void setDashReleasing(boolean releasing) {
        this.dataTracker.set(DASH_RELEASING, releasing);
    }

    @Override
    public int getDashCooldown() {
        return this.dataTracker.get(DASH_COOLDOWN);
    }

    @Override
    public void setDashCooldown(int ticks) {
        this.dataTracker.set(DASH_COOLDOWN, ticks);
    }

    @Override
    public int getDashMaxCooldown() {
        return 20 + this.random.nextInt(20);
    }

    @Override
    public float getDashDamage() {
        return 10;
    }

    @Override
    public SoundEvent getDashChargeSoundEvent() {
        return null;
    }

    @Override
    public SoundEvent getDashReleaseSoundEvent() {
        return null;
    }

    public static DefaultAttributeContainer.Builder createMutatedBatAttributes() {
        return createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D)
            .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.3D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("dashCooldown", this.getDashCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setDashCooldown(nbt.getInt("dashCooldown"));
    }

    public static class MutatedBatNavigation extends BirdNavigation {
        public MutatedBatNavigation(HostileEntity host, World world) {
            super(host, world);
        }

        @Override
        public Path findPathTo(Entity entity, int distance) {
            return this.findPathTo(entity.getBlockPos().add(0, 2, 0), distance);
        }
    }
}
