package com.github.mim1q.minecells.entity.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;

public interface IShockAttackEntity {
    float getShockAttackDamage();
    int getShockAttackMaxCooldown();
    int getShockAttackCooldown();
    void setShockAttackCooldown(int ticks);
    SoundEvent getShockAttackChargeSoundEvent();
    SoundEvent getShockAttackReleaseSoundEvent();
}
