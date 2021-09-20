package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IMeleeAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.EnumSet;

public class AnimetedMeleeAttackGoal<E extends MineCellsEntity & IMeleeAttackEntity> extends Goal {

    protected E entity;
    protected LivingEntity target;
    protected int attackTicks = 0;
    protected final SoundEvent ATTACK_SOUND_EVENT;

    public AnimetedMeleeAttackGoal(E entity, SoundEvent sound) {
        this.setControls(EnumSet.of(Control.LOOK));
        this.ATTACK_SOUND_EVENT = sound;
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        this.target = this.entity.getTarget();
        if(this.target == null)
            return false;
        double d = this.entity.distanceTo(target);
        boolean canAttack = this.entity.getY() > this.entity.getTarget().getY() - 2.0d;
        return canAttack && this.entity.getMeleeAttackCooldown() == 0 && d <= 2.5d && this.entity.getRandom().nextFloat() < 0.2f;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("melee");
        this.attackTicks = 0;

        //TODO Move this somewhere else (maybe entity?)

        if(!this.entity.world.isClient() && this.ATTACK_SOUND_EVENT != null) {
            this.entity.world.playSound(
                null,
                this.entity.getBlockPos(),
                this.ATTACK_SOUND_EVENT,
                SoundCategory.HOSTILE,
                0.5f,
                1.0f
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.attackTicks < this.entity.getMeleeAttackLength() && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.resetState();
        this.entity.setMeleeAttackCooldown(this.entity.getMeleeAttackMaxCooldown());
    }

    @Override
    public void tick() {
        if(this.target != null) {
            this.entity.getLookControl().lookAt(this.target);
            double d = this.entity.distanceTo(target);
            if(this.attackTicks == this.entity.getMeleeAttackActionTick() && d <= 2.5d) {
                this.attack(this.target);
            }
            this.attackTicks++;
        }
    }

    public void attack(LivingEntity target) {
        this.entity.tryAttack(target);
    }
}
