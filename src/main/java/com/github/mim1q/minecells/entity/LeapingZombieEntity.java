package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.LeapGoal;
import com.github.mim1q.minecells.entity.interfaces.ILeapEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;


public class LeapingZombieEntity extends MineCellsEntity implements ILeapEntity {

    // Animation Data
    @Environment(EnvType.CLIENT)
    public float additionalRotation = 0.0F;

    private static final TrackedData<Integer> LEAP_COOLDOWN = DataTracker.registerData(LeapingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LEAP_CHARGING = DataTracker.registerData(LeapingZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> LEAP_RELEASING = DataTracker.registerData(LeapingZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public LeapingZombieEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LEAP_COOLDOWN, 50);
        this.dataTracker.startTracking(LEAP_CHARGING, false);
        this.dataTracker.startTracking(LEAP_RELEASING, false);
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(LEAP_COOLDOWN);
    }

    @Override
    public void initGoals() {
        super.initGoals();

        this.goalSelector.add(0, new LeapingZombieLeapGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.3D, false));
    }

    @Override
    public boolean isLeapCharging() {
        return this.dataTracker.get(LEAP_CHARGING);
    }

    @Override
    public void setLeapCharging(boolean charging) {
        this.dataTracker.set(LEAP_CHARGING, charging);
    }

    @Override
    public boolean isLeapReleasing() {
        return this.dataTracker.get(LEAP_RELEASING);
    }

    @Override
    public void setLeapReleasing(boolean releasing) {
        this.dataTracker.set(LEAP_RELEASING, releasing);

    }

    @Override
    public int getLeapCooldown() {
        return this.dataTracker.get(LEAP_COOLDOWN);
    }

    @Override
    public void setLeapCooldown(int ticks) {
        this.dataTracker.set(LEAP_COOLDOWN, ticks);
    }

    @Override
    public int getLeapMaxCooldown() {
        return 20 + this.getRandom().nextInt(20);
    }

    @Override
    public float getLeapDamage() {
        return 10.0F;
    }

    @Override
    public SoundEvent getLeapChargeSoundEvent() {
        return SoundRegistry.LEAPING_ZOMBIE_CHARGE;
    }

    @Override
    public SoundEvent getLeapReleaseSoundEvent() {
        return SoundRegistry.LEAPING_ZOMBIE_RELEASE;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("leapCooldown", this.getLeapCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setLeapCooldown(nbt.getInt("leapCooldown"));
    }

    public static DefaultAttributeContainer.Builder createLeapingZombieAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 4.0D)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0D)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D);
    }

    static class LeapingZombieLeapGoal extends LeapGoal<LeapingZombieEntity> {

        public LeapingZombieLeapGoal(LeapingZombieEntity entity) {
            super(entity, 15, 20, 0.3F);
        }

        @Override
        public boolean canStart() {
            boolean canJump = super.canStart() && this.entity.getRandom().nextFloat() < 0.1F;
            if (!canJump)
                return false;
            double d = this.entity.distanceTo(this.entity.getTarget());
            return d <= 15.0D;
        }
    }
}
