package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IJumpAttackEntity {
    int getJumpAttackMaxCooldown();
    int getJumpAttackCooldown();
    void setJumpAttackCooldown(int ticks);
    SoundEvent getJumpAttackChargeSoundEvent();
    SoundEvent getJumpAttackReleaseSoundEvent();
}
