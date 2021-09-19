package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class JumpingZombieJumpAttackGoal extends Goal {

    protected JumpingZombieEntity entity;
    protected LivingEntity target;
    protected int attackTicks = 0;
    protected boolean canEnd = false;

    public JumpingZombieJumpAttackGoal(JumpingZombieEntity entity) {
        //this.setControls(EnumSet.of(Control.LOOK));

        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        double d = this.entity.distanceTo(target);

        return this.entity.getJumpCooldownTicks() == 0 && d >= 5.0d && d <= 10.0d;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setJumpCooldownTicks(200);
    }

    @Override
    public boolean shouldContinue() {
        return !canEnd && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.stopAnimations();
    }

    @Override
    public void tick() {
        if(this.target != null) {
            double d = this.entity.distanceTo(this.target);

            this.entity.setAttackState("jump");
            this.attackTicks++;
            if(this.attackTicks == this.entity.getAttackTickCount("jump")) {
                this.jump();
            }
            else if(this.attackTicks < this.entity.getAttackLength("jump") && d <= 1.5d) {
                this.attack(this.target);
            }
            else if(this.attackTicks == this.entity.getAttackLength("jump")) {
                this.entity.stopAnimations();
                this.attackTicks = 0;
                this.canEnd = true;
            }
        } else {
            //this.entity.stopAnimations();
        }
    }

    public void jump() {
        Vec3d diff = this.entity.getPos().add(this.target.getPos().multiply(-1.0d));
        this.entity.setVelocity(diff.multiply(-0.25d).add(0.0d, 0.5d, 0.0d));
        MineCells.LOGGER.info("jump");
    }

    public void attack(LivingEntity target) {
        this.entity.tryAttack(target);
    }
}
