package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface ILeapEntity {
    boolean isLeapCharging();
    void setLeapCharging(boolean charging);

    boolean isLeapReleasing();
    void setLeapReleasing(boolean releasing);

    int getLeapCooldown();
    void setLeapCooldown(int ticks);

    int getLeapMaxCooldown();
    float getLeapDamage();

    SoundEvent getLeapChargeSoundEvent();
    SoundEvent getLeapReleaseSoundEvent();
}
