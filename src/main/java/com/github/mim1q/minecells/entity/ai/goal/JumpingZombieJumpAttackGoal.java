package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class JumpingZombieJumpAttackGoal extends Goal {

    protected JumpingZombieEntity entity;
    protected int attackTicks = 0;
    protected int attackCooldown = 0;
    protected boolean canEnd = false;

    public JumpingZombieJumpAttackGoal(JumpingZombieEntity entity) {
        this.setControls(EnumSet.of(Control.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if(target != null) {
            return true;
//            double d = this.entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
//            return d > 10.0d && d < 15.0d;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return !this.canEnd;
    }

    @Override
    public void stop() {
        this.entity.stopAnimations();
    }

    @Override
    public void tick() {
        MineCells.LOGGER.error("kurwaaa");
        LivingEntity target = this.entity.getTarget();
        if(target != null) {
            double d = this.entity.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
            this.entity.getLookControl().lookAt(target, 30.0f, 30.0f);

            if (this.attackCooldown == 0 && d <= 3.0d || this.attackTicks > 0) {
                this.entity.setAttackState("jump");
                this.attackTicks++;
                if(this.attackTicks == this.entity.getAttackTickCount("jump")) {
                    this.jump(target);
                }
                else if(this.attackTicks < this.entity.getAttackLength("jump") && d <= 1.5d) {
                    this.attack(target);
                }
                else if(this.attackTicks == this.entity.getAttackLength("jump")) {
                    this.attackCooldown = this.entity.getAttackCooldown("jump");
                    this.entity.stopAnimations();
                    this.attackTicks = 0;
                    this.canEnd = true;
                }
            }
        }
        if(this.attackCooldown > 0)
            this.attackCooldown--;
    }

    public void jump(LivingEntity target) {
        Vec3d diff = this.entity.getPos().add(target.getPos().multiply(-1.0d));
        this.entity.setVelocity(diff.normalize().multiply(5).add(0.0d, 0.5d, 0.0d));
    }

    public void attack(LivingEntity target) {
        this.entity.tryAttack(target);
    }
}
