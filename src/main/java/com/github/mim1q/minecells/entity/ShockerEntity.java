package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.AuraAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IAuraEntity;
import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShockerEntity extends MineCellsEntity implements IAuraEntity {

    public static final TrackedData<Integer> AURA_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ShockerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(AURA_COOLDOWN, 50);
        this.dataTracker.startTracking(AURA_CHARGING, false);
        this.dataTracker.startTracking(AURA_RELEASING, false);
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(AURA_COOLDOWN);
        this.handleStates();
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new AuraAttackGoal<>(this, 10.0d, 15, 60, 1.0F));
    }

    //endregion
    //region Handle States

    private void handleStates() {
        if (this.world.isClient()) {
            Vec3d pos = this.getPos().add(0.0D, 1.0D, 0.0D);
            if (this.isAuraCharging()) {
                ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 5, 2.0D, -0.1D);
            } else if (this.isAuraReleasing()) {
                ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 100, 9.5D, 0.01D);
                ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 10, 1.0D, 0.5D);
            }
        }
    }
    //endregion
    //region Attributes and Initialization

    public static DefaultAttributeContainer.Builder createShockerAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setNoGravity(true);
        this.setPosition(this.getPos().add(0.0d, 1.5d, 0.0d));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
    //endregion

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isFire() || source.isMagic()) {
            return false;
        }
        if (source.isProjectile()) {
            amount = amount * 0.2F;
        }
        return super.damage(source, amount);
    }


    public boolean isAuraCharging() {
        return this.dataTracker.get(AURA_CHARGING);
    }

    public void setAuraCharging(boolean charging) {
        this.dataTracker.set(AURA_CHARGING, charging);
    }

    public boolean isAuraReleasing() {
        return this.dataTracker.get(AURA_RELEASING);
    }

    public void setAuraReleasing(boolean releasing) {
        this.dataTracker.set(AURA_RELEASING, releasing);
    }

    //region IAuraAttackEntity Implementation

    public float getAuraDamage() {
        return 4.0F;
    }

    public int getAuraMaxCooldown() {
        return 20;
    }

    public int getAuraCooldown() {
        return this.dataTracker.get(AURA_COOLDOWN);
    }

    public void setAuraCooldown(int ticks) {
        this.dataTracker.set(AURA_COOLDOWN, ticks);
    }

    public SoundEvent getAuraChargeSoundEvent() {
        return SoundRegistry.SHOCKER_CHARGE;
    }

    public SoundEvent getAuraReleaseSoundEvent() {
        return SoundRegistry.SHOCKER_RELEASE;
    }

    //endregion

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("auraCooldown", this.getAuraCooldown());
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.setAuraCooldown(nbt.getInt("auraCooldown"));
        super.readCustomDataFromNbt(nbt);
    }

    //region Sounds

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.SHOCKER_DEATH;
    }
}
