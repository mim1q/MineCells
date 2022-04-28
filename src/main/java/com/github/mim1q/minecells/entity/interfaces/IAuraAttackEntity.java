package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IAuraAttackEntity {
    float getAuraAttackDamage();
    int getAuraAttackMaxCooldown();
    int getAuraAttackCooldown();
    void setAuraAttackCooldown(int ticks);
    SoundEvent getAuraAttackChargeSoundEvent();
    SoundEvent getAuraAttackReleaseSoundEvent();
}
