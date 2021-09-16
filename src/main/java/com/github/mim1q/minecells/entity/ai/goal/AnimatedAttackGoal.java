package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.interfaces.AnimatedAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;

import java.util.EnumSet;

public class AnimatedAttackGoal<E extends HostileEntity & AnimatedAttackEntity> extends Goal {

    protected E entity;
    protected int attackTicks = 0;
    protected int attackCooldown = 0;

    public AnimatedAttackGoal(E entity) {
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return this.entity.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void stop() {
        this.entity.stopAnimations();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if(target != null) {
            this.entity.getLookControl().lookAt(target, 30.0f, 30.0f);
            this.entity.getNavigation().startMovingTo(target, 0.6d);
            double d = this.entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());

            if (this.attackCooldown == 0 && d <= 3.0d || this.attackTicks > 0) {
                this.entity.setAttackState("melee");
                this.attackTicks++;
                if(this.attackTicks == this.entity.getAttackTickCount("melee")) {
                    if(d <= 5.0d)
                        this.attack(target);
                }
                else if(this.attackTicks == this.entity.getAttackLength("melee")) {
                    this.attackCooldown = this.entity.getAttackCooldown("melee");
                    this.entity.stopAnimations();
                    this.attackTicks = 0;
                }
            }
        }
        if(this.attackCooldown > 0)
            this.attackCooldown--;
    }

    public void attack(LivingEntity target) {
        this.entity.tryAttack(target);
    }
}
