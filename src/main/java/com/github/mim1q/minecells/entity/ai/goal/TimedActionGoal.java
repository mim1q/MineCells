package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class TimedActionGoal<E extends LivingEntity> extends Goal {

    protected final E entity;
    protected final CooldownGetter cooldownGetter;
    protected final CooldownSetter cooldownSetter;
    protected final StateSetter stateSetter;
    protected final int defaultCooldown;
    protected final int actionTick;
    protected final int length;
    protected final float chance;

    private int ticks = 0;

    public TimedActionGoal(E entity,
                           CooldownGetter cooldownGetter,
                           CooldownSetter cooldownSetter,
                           StateSetter stateSetter,
                           int defaultCooldown,
                           int actionTick,
                           int length,
                           float chance) {
        this.entity = entity;
        this.cooldownGetter = cooldownGetter;
        this.cooldownSetter = cooldownSetter;
        this.stateSetter = stateSetter;
        this.defaultCooldown = defaultCooldown;
        this.actionTick = actionTick;
        this.length = length;
        this.chance = chance;
    }

    @Override
    public boolean canStart() {
        int cooldown = cooldownGetter.getCooldown();
        return cooldown == 0
            && (this.chance == 1.0F || entity.getRandom().nextFloat() < chance);
    }

    @Override
    public boolean shouldContinue() {
        return this.ticks <= this.actionTick;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.stateSetter.setState(State.CHARGE, true);
    }

    @Override
    public void tick() {
        if (this.ticks == actionTick) {
            this.runAction();
            this.stateSetter.setState(State.CHARGE, false);
            this.stateSetter.setState(State.RELEASE, true);
        }
        this.ticks++;
    }

    protected void runAction() {

    }

    @Override
    public void stop() {
        cooldownSetter.setCooldown(defaultCooldown);
        this.stateSetter.setState(State.CHARGE, false);
        this.stateSetter.setState(State.RELEASE, false);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    public enum State {
        CHARGE,
        RELEASE
    }

    public interface CooldownGetter {
        int getCooldown();
    }

    public interface CooldownSetter {
        void setCooldown(int cooldown);
    }

    public interface StateSetter {
        void setState(State state, boolean bool);
    }
}
