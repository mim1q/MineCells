package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvent;

public class TimedActionGoal<E extends LivingEntity> extends Goal {

    protected final E entity;
    protected final CooldownGetter cooldownGetter;
    protected final CooldownSetter cooldownSetter;
    protected final StateSetter stateSetter;
    protected final int defaultCooldown;
    protected final int actionTick;
    protected final int length;
    protected final float chance;
    protected final SoundEvent chargeSound;
    protected final SoundEvent releaseSound;

    private int ticks = 0;

    public TimedActionGoal(Builder<E, ?> builder) {
        this.entity = builder.entity;
        this.cooldownGetter = builder.cooldownGetter;
        this.cooldownSetter = builder.cooldownSetter;
        this.stateSetter = builder.stateSetter;
        this.defaultCooldown = builder.defaultCooldown;
        this.actionTick = builder.actionTick;
        this.length = builder.length;
        this.chance = builder.chance;
        this.chargeSound = builder.chargeSound;
        this.releaseSound = builder.releaseSound;
    }

    @Override
    public boolean canStart() {
        int cooldown = cooldownGetter.getCooldown();
        return cooldown == 0
            && (this.chance == 1.0F || entity.getRandom().nextFloat() < chance);
    }

    @Override
    public boolean shouldContinue() {
        return this.ticks <= this.length;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.playSound(this.chargeSound);
        this.stateSetter.setState(State.CHARGE, true);
    }

    @Override
    public void tick() {
        if (this.ticks == actionTick) {
            this.playSound(this.releaseSound);
            this.runAction();
            this.stateSetter.setState(State.CHARGE, false);
            this.stateSetter.setState(State.RELEASE, true);
        } else if (this.ticks < actionTick) {
            this.charge();
        } else {
            this.release();
        }
        this.ticks++;
    }

    protected void runAction() {

    }

    protected void charge() {

    }

    protected void release() {

    }

    protected void playSound(SoundEvent soundEvent) {
        this.entity.playSound(soundEvent, 0.5F, 1.0F);
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

    @SuppressWarnings("unchecked")
    public static class Builder<E extends LivingEntity, B extends Builder<E, B>> {
        public E entity;
        public CooldownGetter cooldownGetter;
        public CooldownSetter cooldownSetter;
        public StateSetter stateSetter;
        public int defaultCooldown = 100;
        public int actionTick = 10;
        public int length = 20;
        public float chance = 1.0F;
        public SoundEvent chargeSound;
        public SoundEvent releaseSound;

        public Builder(E entity) {
            this.entity = entity;
        }

        public B cooldownSetter(CooldownSetter cooldownSetter) {
            this.cooldownSetter = cooldownSetter;
            return (B) this;
        }

        public B cooldownGetter(CooldownGetter cooldownGetter) {
            this.cooldownGetter = cooldownGetter;
            return (B) this;
        }

        public B stateSetter(StateSetter stateSetter) {
            this.stateSetter = stateSetter;
            return (B) this;
        }

        public B defaultCooldown(int defaultCooldown) {
            this.defaultCooldown = defaultCooldown;
            return (B) this;
        }

        public B actionTick(int actionTick) {
            this.actionTick = actionTick;
            return (B) this;
        }

        public B length(int length) {
            this.length = length;
            return (B) this;
        }

        public B chance(int chance) {
            this.chance = chance;
            return (B) this;
        }

        public B chargeSound(SoundEvent chargeSound) {
            this.chargeSound = chargeSound;
            return (B) this;
        }

        public B releaseSound(SoundEvent releaseSound) {
            this.releaseSound = releaseSound;
            return (B) this;
        }

        protected void check() {
            if (this.cooldownGetter == null || this.cooldownSetter == null || this.stateSetter == null) {
                throw new IllegalStateException("cooldownGetter, cooldownSetter and stateSetter must be set");
            }
        }

        public TimedActionGoal<E> build() {
            this.check();
            return new TimedActionGoal<>(this);
        }
    }
}
