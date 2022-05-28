package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IAuraEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class AuraGoal<E extends MineCellsEntity & IAuraEntity> extends Goal {

    protected E entity;
    protected int ticks = 0;
    protected final double radius;
    protected final int actionTick;
    protected final int lengthTicks;
    protected final float chance;

    public AuraGoal(E entity, double radius, int actionTick, int lengthTicks, float chance) {
        this.entity = entity;
        this.radius = radius;
        this.actionTick = actionTick;
        this.lengthTicks = lengthTicks;
        this.chance = chance;
    }

    @Override
    public boolean canStart() {
        PlayerEntity closestPlayer = this.entity.world.getClosestPlayer(this.entity, this.radius - 1.0D);
        return this.entity.getAuraCooldown() == 0
            && closestPlayer != null
            && this.entity.canSee(closestPlayer)
            && !(closestPlayer.isCreative() || closestPlayer.isSpectator())
            && this.entity.getRandom().nextFloat() < this.chance;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.entity.setAuraCharging(true);
        this.entity.playSound(this.entity.getAuraChargeSoundEvent(), 0.5f, 1.0f);
    }

    @Override
    public boolean shouldContinue() {
        return this.ticks < this.lengthTicks;
    }

    @Override
    public void stop() {
        this.entity.setAuraCharging(false);
        this.entity.setAuraReleasing(false);
        this.entity.setAuraCooldown(this.entity.getAuraMaxCooldown());
    }

    @Override
    public void tick() {
        if (this.ticks == this.actionTick) {
            this.entity.setAuraCharging(false);
            this.entity.setAuraReleasing(true);
            this.entity.playSound(this.entity.getAuraReleaseSoundEvent(), 0.5f, 1.0f);
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
            player.damage(DamageSource.mob(this.entity).setUsesMagic(), this.entity.getAuraDamage());
        }
    }
}
