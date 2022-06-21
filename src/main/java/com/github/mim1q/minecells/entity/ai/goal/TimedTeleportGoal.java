package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;

import java.util.EnumSet;

public class TimedTeleportGoal extends TimedActionGoal<HostileEntity> {
    Entity target;

    public TimedTeleportGoal(Builder builder) {
        super(builder);
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = this.entity.getTarget();

        return this.target != null && this.target.isAlive() && this.target.isAttackable()
            && (this.entity.distanceTo(this.target) > 10.0D || !this.entity.canSee(this.target))
            && super.canStart();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void runAction() {
        this.entity.teleport(this.target.getX(), this.target.getY(), this.target.getZ());
    }

    @Override
    protected void playReleaseSound() {
        super.playReleaseSound();
    }

    public static class Builder extends TimedActionGoal.Builder<HostileEntity, Builder> {

        public Builder(HostileEntity entity) {
            super(entity);
            this.chargeSound = SoundRegistry.TELEPORT_CHARGE;
            this.releaseSound = SoundRegistry.TELEPORT_RELEASE;
        }

        public TimedTeleportGoal build() {
            this.check();
            return new TimedTeleportGoal(this);
        }
    }
}
