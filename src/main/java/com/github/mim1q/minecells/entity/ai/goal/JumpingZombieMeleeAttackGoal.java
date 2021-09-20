package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.JumpingZombieEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class JumpingZombieMeleeAttackGoal extends Goal {

    protected JumpingZombieEntity entity;
    protected LivingEntity target;
    protected int attackTicks = 0;

    public JumpingZombieMeleeAttackGoal(JumpingZombieEntity entity) {
        this.setControls(EnumSet.of(Control.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        this.target = this.entity.getTarget();
        if(this.target == null)
            return false;
        double d = this.entity.distanceTo(target);
        boolean canAttack = this.entity.getY() > this.entity.getTarget().getY() - 2.0d;
        return canAttack && this.entity.getJumpCooldownTicks() < 80 && this.entity.getMeleeCooldownTicks() == 0 && d <= 2.5d && this.entity.getRandom().nextFloat() < 0.2f;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("melee");
        this.attackTicks = 0;
        if(!this.entity.world.isClient()) {
            this.entity.world.playSound(
                    null,
                    this.entity.getBlockPos(),
                    SoundRegistry.JUMPING_ZOMBIE_MELEE_SOUND_EVENT,
                    SoundCategory.HOSTILE,
                    0.5f,
                    1.0f
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.attackTicks < this.entity.getAttackLength("melee") && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.stopAnimations();
        this.entity.setMeleeCooldownTicks(10 + this.entity.getRandom().nextInt(10));
    }

    @Override
    public void tick() {
        if(this.target != null) {
            this.entity.getLookControl().lookAt(this.target);
            double d = this.entity.distanceTo(target);
            if(this.attackTicks == this.entity.getAttackTickCount("melee") && d <= 2.5d) {
                this.attack(this.target);
            }
            this.attackTicks++;
        }
    }

    public void attack(LivingEntity target) {
        this.entity.tryAttack(target);
    }
}
