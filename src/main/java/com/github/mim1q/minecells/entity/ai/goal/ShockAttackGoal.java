package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.ShockerEntity;
import com.github.mim1q.minecells.entity.interfaces.IShockAttackEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class ShockAttackGoal<E extends MineCellsEntity & IShockAttackEntity> extends Goal {

    protected E entity;
    protected double radius;
    protected int ticks = 0;

    public ShockAttackGoal(E entity, double radius) {
        this.entity = entity;
        this.radius = radius;
    }

    @Override
    public boolean canStart() {
        PlayerEntity closestPlayer = this.entity.world.getClosestPlayer(this.entity, this.radius - 2.0d);
        return this.entity.getShockAttackCooldown() == 0
                && closestPlayer != null && !(closestPlayer.isCreative() || closestPlayer.isSpectator());
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.entity.setAttackState("shock_charge");
        this.entity.playSound(this.entity.getShockAttackChargeSoundEvent(), 0.5f, 1.0f);
    }

    @Override
    public boolean shouldContinue() {
        return this.ticks < this.entity.getShockAttackLength();
    }

    @Override
    public void stop() {
        this.entity.resetAttackState();
        this.entity.setShockAttackCooldown(this.entity.getShockAttackMaxCooldown());
    }

    @Override
    public void tick() {
        if(this.ticks == this.entity.getShockAttackReleaseTick()) {
            this.entity.setAttackState("shock_release");
            this.entity.playSound(this.entity.getShockAttackReleaseSoundEvent(), 0.5f, 1.0f);
        } else if(this.ticks > this.entity.getShockAttackReleaseTick()) {
            this.damage();
        }
        this.ticks++;
    }

    protected void damage() {
        List<Entity> entitiesInRange = this.entity.world.getOtherEntities(
                this.entity,
                this.entity.getBoundingBox().expand(this.radius),
                (e) -> e instanceof LivingEntity && this.entity.distanceTo(e) <= this.radius && !(e instanceof ShockerEntity)
        );
        for(Entity entity : entitiesInRange) {
            entity.damage(DamageSource.mob(this.entity), (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
        }
    }
}
