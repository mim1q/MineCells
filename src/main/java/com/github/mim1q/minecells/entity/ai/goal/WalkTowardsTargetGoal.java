package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class WalkTowardsTargetGoal extends Goal {

    protected HostileEntity entity;

    public WalkTowardsTargetGoal(HostileEntity entity) {
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        return this.entity.getTarget() != null;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.entity.getTarget();
        return target != null && !target.isSpectator() && !((PlayerEntity)target).isCreative();
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if(target != null) {
            this.entity.getLookControl().lookAt(target);
            this.entity.getNavigation().startMovingTo(target, 0.6d);
        }
    }
}
