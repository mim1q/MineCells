package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class TimedShootGoal<E extends HostileEntity> extends TimedActionGoal<E> {

    private Entity target;
    private final ProjectileCreator projectileCreator;

    public TimedShootGoal(Builder<E> builder) {
        super(builder);
        this.projectileCreator = builder.projectileCreator;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = this.entity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return super.canStart();
    }

    @Override
    public void tick() {
        this.entity.getLookControl().lookAt(this.target);
        super.tick();
    }

    @Override
    protected void runAction() {
        if (this.target != null) {
            Entity projectile = projectileCreator.create(
                this.entity.getPos(),
                this.target.getPos().add(0.0D, this.target.getHeight() * 0.5D, 0.0D)
            );
            this.entity.world.spawnEntity(projectile);
        }
    }

    public interface ProjectileCreator {
        Entity create(Vec3d position, Vec3d targetPosition);
    }

    public static class Builder<E extends HostileEntity> extends TimedActionGoal.Builder<E, Builder<E>> {

        ProjectileCreator projectileCreator = null;

        public Builder(E entity) {
            super(entity);
        }

        public Builder<E> projectileCreator(ProjectileCreator projectileCreator) {
            this.projectileCreator = projectileCreator;
            return this;
        }

        @Override
        protected void check() {
            super.check();
            if (this.projectileCreator == null) {
                throw new IllegalStateException("projectileCreator must be set");
            }
        }

        public TimedShootGoal<E> build() {
            this.check();
            return new TimedShootGoal<>(this);
        }
    }
}
