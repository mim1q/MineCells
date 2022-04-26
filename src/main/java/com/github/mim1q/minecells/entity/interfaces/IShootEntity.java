package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IShootEntity {
    int getShootMaxCooldown();
    int getShootCooldown();
    void setShootCooldown(int ticks);
    SoundEvent getShootChargeSoundEvent();
    SoundEvent getShootReleaseSoundEvent();
}
