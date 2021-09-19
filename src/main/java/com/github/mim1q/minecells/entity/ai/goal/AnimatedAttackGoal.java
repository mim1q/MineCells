package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.interfaces.IAnimatedAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class AnimatedAttackGoal<E extends HostileEntity & IAnimatedAttackEntity> extends Goal {

    protected E entity;
    protected int attackTicks = 0;
    protected int attackCooldown = 0;
    protected final String attackName;

    public AnimatedAttackGoal(E entity, String attackName) {
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.attackName = attackName;
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return this.entity.getTarget() != null && this.entity.getAttackState().equals("none");
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.entity.getTarget();
        return target != null && !target.isSpectator() && !((PlayerEntity)target).isCreative();
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

            if(this.attackCooldown == 0 && d <= 3.0d || this.attackTicks > 0) {
                this.entity.setAttackState(this.attackName);
                this.attackTicks++;
                if(this.attackTicks == this.entity.getAttackTickCount(this.attackName)) {
                    if(d <= 6.0d)
                        this.attack(target);
                }
                else if(this.attackTicks == this.entity.getAttackLength(this.attackName)) {
                    this.attackCooldown = this.entity.getAttackCooldown(this.attackName);
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
