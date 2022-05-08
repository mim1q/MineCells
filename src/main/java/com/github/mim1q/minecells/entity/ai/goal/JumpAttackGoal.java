package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IJumpAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class JumpAttackGoal<E extends MineCellsEntity & IJumpAttackEntity> extends Goal {

    protected E entity;
    protected LivingEntity target;
    protected int ticks = 0;
    protected final int actionTick;
    protected final int lengthTicks;
    protected final float chance;
    List<UUID> alreadyAttacked;

    public JumpAttackGoal(E entity, int actionTick, int lengthTicks, float chance) {
        this.actionTick = actionTick;
        this.lengthTicks = lengthTicks;
        this.chance = chance;
        this.setControls(EnumSet.of(Control.MOVE));
        this.entity = entity;
        this.alreadyAttacked = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        return this.entity.getJumpAttackCooldown() == 0
            && this.entity.canSee(target)
            && this.entity.getY() >= this.entity.getTarget().getY()
            && this.entity.getRandom().nextFloat() < this.chance;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("jump");
        this.ticks = 0;
        this.alreadyAttacked.clear();

        if (!this.entity.world.isClient() && this.entity.getJumpAttackChargeSoundEvent() != null) {
            this.entity.playSound(this.entity.getJumpAttackChargeSoundEvent(),0.5F,1.0F);
        }
    }

    @Override
    public boolean shouldContinue() {
        return (this.ticks < this.lengthTicks || !this.entity.isOnGround()) && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.setJumpAttackCooldown(this.entity.getJumpAttackMaxCooldown());
        this.entity.resetAttackState();
    }

    @Override
    public void tick() {
        if (this.target != null) {

            if (this.ticks < this.actionTick) {
                this.entity.getMoveControl().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), 0.0D);
            }
            else if (this.ticks == this.actionTick) {
                this.jump();
            }
            else if (!this.entity.isOnGround()) {
                this.attack();
            }

            this.ticks++;
        }
    }

    public void jump() {
        Vec3d diff = this.target.getPos().add(this.entity.getPos().multiply(-1.0D));
        this.entity.setVelocity(diff.multiply(0.3D, 0.05D, 0.3D).add(0.0D, 0.3D, 0.0D));
        if (!this.entity.world.isClient() && this.entity.getJumpAttackReleaseSoundEvent() != null) {
            this.entity.playSound(this.entity.getJumpAttackReleaseSoundEvent(),0.5f,1.0F);
        }
        this.entity.resetAttackState();
    }

    public void attack() {
        List<PlayerEntity> players = this.entity.world.getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(0.5D), e -> !this.alreadyAttacked.contains(e.getUuid()));
        for (PlayerEntity player : players) {
            this.entity.tryAttack(this.target);
            this.alreadyAttacked.add(player.getUuid());
        }
    }
}
