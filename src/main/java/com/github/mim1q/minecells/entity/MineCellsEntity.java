package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.nonliving.projectile.GrenadeEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class MineCellsEntity extends HostileEntity {

    protected MineCellsEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Environment(EnvType.CLIENT)
    public float animationTimestamp = Float.NEGATIVE_INFINITY;

    @Environment(EnvType.CLIENT)
    public String lastAnimation = "idle";

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource instanceof GrenadeEntity.GrenadeDamageSource) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    protected void decrementCooldown(TrackedData<Integer> cooldown) {
        int current = this.dataTracker.get(cooldown);
        if (current > 0) {
            this.dataTracker.set(cooldown, current - 1);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.LEAPING_ZOMBIE_DEATH;
    }
}
