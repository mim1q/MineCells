package com.github.mim1q.minecells.entity.interfaces;

public interface IJumpAttackEntity {
    int getJumpAttackActionTick();
    int getJumpAttackMaxCooldown();
    int getJumpAttackLength();
    int getJumpAttackCooldown();
    void setJumpAttackCooldown(int ticks);
}
