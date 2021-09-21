package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IShockAttackEntity;
import net.minecraft.entity.ai.goal.Goal;

public class ShockAttackGoal<E extends MineCellsEntity & IShockAttackEntity> extends Goal {

    protected E entity;
    protected double radius;
    protected int ticks = 0;

    public ShockAttackGoal(E entity, double radius) {
        this.entity = entity;
        this.radius = radius;
    }

    @Override
    public boolean canStart() {
        return this.entity.getShockAttackCooldown() == 0;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.entity.setAttackState("shock_charge");
    }

    @Override
    public void stop() {
        this.entity.resetAttackState();
    }

    @Override
    public void tick() {
        if(this.ticks < this.entity.getShockAttackReleaseTick()) {
            this.charging();
        }
        else {
            if (this.ticks == this.entity.getShockAttackReleaseTick()) {
                this.entity.setAttackState("shock_release");
            }
            this.releasing();
        }

        this.ticks++;
    }

    public void charging() {

    }

    public void releasing() {

    }

}
