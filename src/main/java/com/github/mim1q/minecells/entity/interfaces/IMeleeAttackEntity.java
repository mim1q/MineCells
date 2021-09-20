package com.github.mim1q.minecells.entity.interfaces;

public interface IMeleeAttackEntity {
    int getMeleeAttackActionTick();
    int getMeleeAttackMaxCooldown();
    int getMeleeAttackLength();
    int getMeleeAttackCooldown();
    void setMeleeAttackCooldown(int ticks);
}
