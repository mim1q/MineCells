package com.github.mim1q.minecells.entity.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;

public interface IShockAttackEntity {
    int getShockAttackReleaseTick();
    int getShockAttackMaxCooldown();
    int getShockAttackLength();
    int getShockAttackCooldown();
    void setShockAttackCooldown(int ticks);
    SoundEvent getShockAttackChargeSoundEvent();
    SoundEvent getShockAttackReleaseSoundEvent();

    @Environment(EnvType.CLIENT)
    void spawnParticles(ParticleEffect particle, int amount, double radius, double speed);
}
