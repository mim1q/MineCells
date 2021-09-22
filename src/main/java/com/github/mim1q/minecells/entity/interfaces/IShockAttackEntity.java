package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IShockAttackEntity {
    int getShockAttackReleaseTick();
    int getShockAttackMaxCooldown();
    int getShockAttackLength();
    int getShockAttackCooldown();
    void setShockAttackCooldown(int ticks);
    SoundEvent getShockAttackChargeSoundEvent();
    SoundEvent getShockAttackReleaseSoundEvent();
}
