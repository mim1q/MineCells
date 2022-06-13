package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IDashEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DashGoal<E extends MineCellsEntity & IDashEntity> extends Goal {

    protected final E entity;
    protected LivingEntity target;
    protected Vec3d targetPos;
    protected double distance;
    protected double distanceTravelled;
    protected int ticks = 0;
    protected final int releaseTick;
    protected final int restTick;
    protected final int lengthTicks;
    protected final float chance;
    protected final float speed;
    protected List<Entity> alreadyAttacked;

    public DashGoal(E entity, int releaseTick, int restTick, int lengthTicks, float chance, float speed) {
        this.entity = entity;
        this.releaseTick = releaseTick;
        this.restTick = restTick;
        this.lengthTicks = lengthTicks;
        this.chance = chance;
        this.speed = speed;
        this.alreadyAttacked = new ArrayList<>();

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null) {
            return false;
        }

        return this.entity.getDashCooldown() == 0
            && this.entity.canSee(target)
            && this.entity.getY() >= this.entity.getTarget().getY()
            && this.entity.getRandom().nextFloat() < this.chance;
    }

    @Override
    public void start() {
        System.out.println("startuwa!!");
        this.target = this.entity.getTarget();
        this.entity.setDashCharging(true);
        this.ticks = 0;
        this.distanceTravelled = 0;
        this.alreadyAttacked.clear();

        if (!this.entity.world.isClient() && this.entity.getDashChargeSoundEvent() != null) {
            this.entity.playSound(this.entity.getDashChargeSoundEvent(), 0.5F, 1.0F);
        }
    }

    @Override
    public void stop() {
        this.entity.setDashCharging(false);
        this.entity.setDashReleasing(false);
        this.entity.setDashCooldown(this.entity.getDashMaxCooldown());
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive() && this.ticks < this.lengthTicks;
    }

    @Override
    public void tick() {
        if (this.ticks == this.restTick || this.distanceTravelled > this.distance + 2.0D) {
            this.entity.setDashReleasing(false);
        } else if (this.ticks == this.releaseTick) {
            this.targetPos = this.target.getPos().subtract(this.entity.getPos()).normalize().multiply(this.speed);
            this.distance = this.target.getPos().distanceTo(this.entity.getPos());
            this.entity.setDashCharging(false);
            this.entity.setDashReleasing(true);
            this.entity.playSound(this.entity.getDashReleaseSoundEvent(), 0.5F, 1.0F);
        }
        if (this.entity.isDashReleasing()) {
            this.entity.move(MovementType.SELF, this.targetPos);
            this.distanceTravelled += this.speed;
            this.attack();
        } else if (this.entity.isDashCharging()) {
            this.entity.getLookControl().lookAt(this.target);
            this.entity.getNavigation().stop();
            this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), this.speed);
        }

        this.ticks++;
    }

    public void attack() {
        List<PlayerEntity> players = this.entity.world.getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(0.33D), e -> !this.alreadyAttacked.contains(e));
        for (PlayerEntity player : players) {
            player.damage(DamageSource.mob(this.entity), this.entity.getDashDamage());
            this.alreadyAttacked.add(player);
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
}