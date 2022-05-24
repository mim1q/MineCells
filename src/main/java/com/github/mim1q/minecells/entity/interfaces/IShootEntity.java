package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IShootEntity {
    boolean isShootCharging();
    void setShootCharging(boolean charging);

    boolean isShootReleasing();
    void setShootReleasing(boolean releasing);

    int getShootCooldown();
    void setShootCooldown(int ticks);

    int getShootMaxCooldown();

    SoundEvent getShootChargeSoundEvent();
    SoundEvent getShootReleaseSoundEvent();
}
