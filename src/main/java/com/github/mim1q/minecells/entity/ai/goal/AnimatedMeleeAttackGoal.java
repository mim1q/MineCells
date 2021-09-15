package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.interfaces.AnimatedMeleeAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;

import java.util.EnumSet;

public class AnimatedMeleeAttackGoal<E extends HostileEntity & AnimatedMeleeAttackEntity> extends Goal {

    E entity;
    int attackTicks = 0;
    int attackCooldown = 0;

    public AnimatedMeleeAttackGoal(E entity) {
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

            if (this.attackCooldown == 0 && d <= 1.5d || this.attackTicks > 0) {
                this.entity.setAttackState("melee");
                this.attackTicks++;
                if(this.attackTicks == this.entity.getAttackTickCount("melee")) {
                    this.attackTicks = 0;
                    this.attackCooldown = this.entity.getAttackCooldown("melee");
                    this.attack(target);
                    this.entity.stopAnimations();
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
