package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class DashGoal<E extends MineCellsEntity & IDashEntity> extends Goal {

    protected final E entity;
    protected LivingEntity target;
    protected int ticks = 0;
    protected final int chargeTime;
    protected final int releaseTime;
    protected final int restTime;
    protected final float chance;
    protected final float speed;

    public DashGoal(E entity, int chargeTime, int releaseTime, int restTime, float chance, float speed) {
        this.entity = entity;
        this.chargeTime = chargeTime;
        this.releaseTime = releaseTime;
        this.restTime = restTime;
        this.chance = chance;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        return this.entity.getDashCooldown() == 0
            && this.entity.canSee(target)
            && this.entity.getY() >= this.entity.getTarget().getY()
            && this.entity.getRandom().nextFloat() < this.chance;
    }
}
