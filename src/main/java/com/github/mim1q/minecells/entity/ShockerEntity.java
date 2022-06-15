package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.AuraGoal;
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

    public static final TrackedData<Integer> AURA_COOLDOWN = DataTracker.registerData(ShockerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> AURA_CHARGING = DataTracker.registerData(ShockerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> AURA_RELEASING = DataTracker.registerData(ShockerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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
    public void initGoals() {
        this.goalSelector.add(0, new AuraGoal<>(this, 10.0d, 15, 60, 1.0F));
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(AURA_COOLDOWN);
        this.handleStates();
    }

    private void handleStates() {
        if (this.world.isClient()) {
            Vec3d pos = this.getPos().add(0.0D, this.getHeight() * 0.5D, 0.0D);
            if (this.isAuraCharging()) {
                for (int i = 0; i < 10; i++) {
                    ParticleHelper.addParticle((ClientWorld)this.world, ParticleRegistry.CHARGE, pos, Vec3d.ZERO);
                }
            } else if (this.isAuraReleasing()) {
                ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 100, 9.5D, 0.01D);
                ParticleHelper.addAura((ClientWorld)this.world, pos, ParticleRegistry.AURA, 10, 1.0D, 0.5D);
            }
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setNoGravity(true);
        this.setPosition(this.getPos().add(0.0d, 1.5d, 0.0d));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

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

    @Override
    public boolean isAuraCharging() {
        return this.dataTracker.get(AURA_CHARGING);
    }
    @Override
    public void setAuraCharging(boolean charging) {
        this.dataTracker.set(AURA_CHARGING, charging);
    }

    @Override
    public boolean isAuraReleasing() {
        return this.dataTracker.get(AURA_RELEASING);
    }
    @Override
    public void setAuraReleasing(boolean releasing) {
        this.dataTracker.set(AURA_RELEASING, releasing);
    }

    @Override
    public int getAuraCooldown() {
        return this.dataTracker.get(AURA_COOLDOWN);
    }
    @Override
    public void setAuraCooldown(int cooldown) {
        this.dataTracker.set(AURA_COOLDOWN, cooldown);
    }
    @Override
    public int getAuraMaxCooldown() {
        return 60 + this.random.nextInt(40);
    }

    @Override
    public float getAuraDamage() {
        return 5.0F;
    }

    @Override
    public SoundEvent getAuraChargeSoundEvent() {
        return SoundRegistry.SHOCKER_CHARGE;
    }
    @Override
    public SoundEvent getAuraReleaseSoundEvent() {
        return SoundRegistry.SHOCKER_RELEASE;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundRegistry.SHOCKER_DEATH;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("auraCooldown", this.getAuraCooldown());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAuraCooldown(nbt.getInt("auraCooldown"));
    }

    public static DefaultAttributeContainer.Builder createShockerAttributes() {
        return createLivingAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0D)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D)
            .add(EntityAttributes.GENERIC_ARMOR, 10.0D)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0D);
    }
}
