package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IMeleeAttackEntity {
    int getMeleeAttackActionTick();
    int getMeleeAttackMaxCooldown();
    int getMeleeAttackLength();
    int getMeleeAttackCooldown();
    void setMeleeAttackCooldown(int ticks);
    SoundEvent getMeleeAttackChargeSoundEvent();
    SoundEvent getMeleeAttackReleaseSoundEvent();
}
