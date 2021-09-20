package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;


public class JumpingZombieJumpAttackGoal extends Goal {

    protected JumpingZombieEntity entity;
    protected LivingEntity target;
    protected int attackTicks = 0;

    public JumpingZombieJumpAttackGoal(JumpingZombieEntity entity) {
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        double d = this.entity.distanceTo(target);

        boolean canJump = this.entity.getY() >= this.entity.getTarget().getY();
        return canJump && this.entity.getJumpCooldownTicks() == 0 && d >= 3.5d && d <= 15.0d && this.entity.getRandom().nextFloat() < 0.15f;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("jump");
        this.attackTicks = 0;
        if(!this.entity.world.isClient()) {
            this.entity.world.playSound(
                    null,
                    this.entity.getBlockPos(),
                    SoundRegistry.JUMPING_ZOMBIE_JUMP_SOUND_EVENT,
                    SoundCategory.HOSTILE,
                    0.5f,
                    1.0f
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.attackTicks < this.entity.getAttackLength("jump") && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.stopAnimations();
        this.entity.setJumpCooldownTicks(50 + this.entity.getRandom().nextInt(25));
    }

    @Override
    public void tick() {
        if(this.target != null) {

            if(this.attackTicks < this.entity.getAttackTickCount("jump"))
                this.entity.getLookControl().lookAt(this.target, 30.0f, 30.0f);
            if(this.attackTicks == this.entity.getAttackTickCount("jump"))
                this.jump();
            else if(!this.entity.isOnGround())
                this.attack();

            this.attackTicks++;
        }
    }

    public void jump() {
        Vec3d diff = this.entity.getPos().add(this.target.getPos().multiply(-1.0d)).normalize();
        this.entity.setVelocity(diff.multiply(-2.0d, 0.0d, -2.0d).add(0.0d, this.entity.getRandom().nextFloat() * 0.15d + 0.3d, 0.0d));
    }

    public void attack() {
        for(PlayerEntity player : this.entity.world.getPlayers()) {
            double d = this.entity.distanceTo(player);
            if(d <= 1.5d)
                this.entity.tryAttack(player);
        }
    }
}
