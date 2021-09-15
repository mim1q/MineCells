package com.github.mim1q.minecells.entity.interfaces;

public interface AnimatedMeleeAttackEntity {

    public int getAttackTickCount(String attackName);

    public int getAttackCooldown(String attackName);

    public void setAttackState(String attackName);

    public String getAttackState();

    public void stopAnimations();
}
