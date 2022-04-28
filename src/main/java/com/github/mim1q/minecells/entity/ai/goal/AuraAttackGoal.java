package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IAuraAttackEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class AuraAttackGoal<E extends MineCellsEntity & IAuraAttackEntity> extends Goal {

    protected E entity;
    protected double radius;
    protected int ticks = 0;
    protected int actionTick;
    protected int lengthTicks;

    public AuraAttackGoal(E entity, double radius, int actionTick, int lengthTicks) {
        this.entity = entity;
        this.radius = radius;
        this.actionTick = actionTick;
        this.lengthTicks = lengthTicks;
    }

    @Override
    public boolean canStart() {
        PlayerEntity closestPlayer = this.entity.world.getClosestPlayer(this.entity, this.radius - 2.0d);
        return this.entity.getAuraAttackCooldown() == 0
                && closestPlayer != null && !(closestPlayer.isCreative() || closestPlayer.isSpectator());
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.entity.setAttackState("aura_charge");
        this.entity.playSound(this.entity.getAuraAttackChargeSoundEvent(), 0.5f, 1.0f);
    }

    @Override
    public boolean shouldContinue() {
        return this.ticks < this.lengthTicks;
    }

    @Override
    public void stop() {
        this.entity.resetAttackState();
        this.entity.setAuraAttackCooldown(this.entity.getAuraAttackMaxCooldown());
    }

    @Override
    public void tick() {
        if (this.ticks == this.actionTick) {
            this.entity.setAttackState("aura_release");
            this.entity.playSound(this.entity.getAuraAttackReleaseSoundEvent(), 0.5f, 1.0f);
        } else if (this.ticks >= this.actionTick && this.ticks % 2 == 0) {
            this.damage();
        }
        this.ticks++;
    }

    protected void damage() {
        List<Entity> playersInRange = this.entity.world.getOtherEntities(
                this.entity,
                this.entity.getBoundingBox().expand(this.radius),
                (e) -> e instanceof PlayerEntity && this.entity.distanceTo(e) <= this.radius
        );
        for (Entity player : playersInRange) {
            player.damage(DamageSource.mob(this.entity), this.entity.getAuraAttackDamage());
        }
    }
}
