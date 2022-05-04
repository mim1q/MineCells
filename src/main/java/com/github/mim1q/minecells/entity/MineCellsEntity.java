package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MineCellsEntity extends HostileEntity {

    protected MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static final TrackedData<String> ATTACK_STATE = DataTracker.registerData(MineCellsEntity.class, TrackedDataHandlerRegistry.STRING);

    @Environment(EnvType.CLIENT)
    public float animationTimestamp = Float.NEGATIVE_INFINITY;

    @Environment(EnvType.CLIENT)
    public String lastAnimation = "none";

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACK_STATE, "none");
    }

    public void setAttackState(String attackName) {
        this.dataTracker.set(ATTACK_STATE, attackName);
    }

    public String getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    public void resetAttackState() {
        this.setAttackState("none");
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource instanceof GrenadeEntity.GrenadeDamageSource) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    protected void decrementCooldown(TrackedData<Integer> cooldown, @Nullable String state) {
        int current = this.dataTracker.get(cooldown);
        if (current > 0 && !this.getAttackState().equals(state)) {
            this.dataTracker.set(cooldown, current - 1);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.JUMPING_ZOMBIE_DEATH;
    }
}
