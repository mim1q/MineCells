package com.github.mim1q.minecells.entity.interfaces;

public interface IShockAttackEntity {
    int getShockAttackReleaseTick();
    int getShockAttackMaxCooldown();
    int getShockAttackLength();
    int getShockAttackCooldown();
    void setShockAttackCooldown(int ticks);
}
