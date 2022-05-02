package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IShootEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ShootGoal <E extends MineCellsEntity & IShootEntity> extends Goal {

    protected E entity;
    protected LivingEntity target;
    protected int ticks = 0;
    protected final int actionTick;
    protected final int lengthTicks;

    public ShootGoal(E entity, int actionTick, int lengthTicks) {
        this.actionTick = actionTick;
        this.lengthTicks = lengthTicks;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        return this.entity.canSee(target) && this.entity.getShootCooldown() == 0;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("shoot");
        this.ticks = 0;

        if (!this.entity.world.isClient() && this.entity.getShootChargeSoundEvent() != null) {
            this.entity.playSound(this.entity.getShootChargeSoundEvent(),0.5f,1.0f);
        }
    }

    @Override
    public boolean shouldContinue() {
        return (this.ticks < this.lengthTicks && this.target.isAlive());
    }

    @Override
    public void stop() {
        this.entity.resetAttackState();
        this.entity.setShootCooldown(this.entity.getShootMaxCooldown());
    }

    @Override
    public void tick() {
        if (this.target != null) {
            this.entity.getLookControl().lookAt(target);
            if (this.ticks == this.actionTick) {
                if (!this.entity.world.isClient() && this.entity.getShootReleaseSoundEvent() != null) {
                    this.entity.playSound(this.entity.getShootReleaseSoundEvent(), 0.5f, 1.0f);
                }
                if (!this.entity.world.isClient()) {
                    this.shoot(this.target);
                }
            }
        }
        this.ticks++;
    }

    public void shoot(LivingEntity target) {

    }
}
