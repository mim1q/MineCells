package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvent;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class TimedActionGoal<E extends LivingEntity> extends Goal {
  protected final E entity;
  protected final Supplier<Integer> cooldownGetter;
  protected final Consumer<Integer> cooldownSetter;
  protected final BiConsumer<State, Boolean> stateSetter;
  protected final int defaultCooldown;
  protected final int actionTick;
  protected final int length;
  protected final float chance;
  protected final SoundEvent chargeSound;
  protected final SoundEvent releaseSound;
  protected final float soundVolume;
  protected final Predicate<E> startPredicate;

  private int ticks = 0;

  protected <S extends TimedActionSettings> TimedActionGoal(E entity, S settings, Predicate<E> predicate) {
    this.entity = entity;
    cooldownGetter = settings.cooldownGetter;
    cooldownSetter = settings.cooldownSetter;
    stateSetter = settings.stateSetter;
    defaultCooldown = settings.defaultCooldown;
    actionTick = settings.actionTick;
    length = settings.length;
    chance = settings.chance;
    chargeSound = settings.chargeSound;
    releaseSound = settings.releaseSound;
    soundVolume = settings.soundVolume;
    startPredicate = predicate == null ? Objects::nonNull : predicate;
  }

  public TimedActionGoal(E entity, Consumer<TimedActionSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedActionSettings(), settingsConsumer), predicate);
  }

  @Override
  public boolean canStart() {
    int cooldown = cooldownGetter.get();
    return cooldown <= 0
      && (this.chance == 1.0F || entity.getRandom().nextFloat() < chance)
      && startPredicate.test(entity);
  }

  @Override
  public boolean shouldContinue() {
    return this.ticks <= this.length;
  }

  @Override
  public void start() {
    this.ticks = 0;
    this.stateSetter.accept(State.CHARGE, true);
    this.playChargeSound();
  }

  @Override
  public void tick() {
    if (this.ticks == actionTick) {
      this.runAction();
      this.stateSetter.accept(State.CHARGE, false);
      this.stateSetter.accept(State.RELEASE, true);
      this.playReleaseSound();
    } else if (this.ticks < actionTick) {
      this.charge();
    } else {
      this.release();
    }
    this.ticks++;
  }

  protected int ticks() {
    return this.ticks;
  }

  protected void runAction() {

  }

  protected void charge() {

  }

  protected void release() {

  }

  protected void playSound(SoundEvent soundEvent) {
    if (soundEvent != null) {
      this.entity.playSound(soundEvent, this.soundVolume, 1.0F);
    }
  }

  protected void playChargeSound() {
    this.playSound(this.chargeSound);
  }

  protected void playReleaseSound() {
    this.playSound(this.releaseSound);
  }

  @Override
  public void stop() {
    cooldownSetter.accept(defaultCooldown);
    this.stateSetter.accept(State.CHARGE, false);
    this.stateSetter.accept(State.RELEASE, false);
  }

  @Override
  public boolean shouldRunEveryTick() {
    return true;
  }

  public enum State {
    IDLE,
    CHARGE,
    RELEASE
  }

  public static class TimedActionSettings {
    public Supplier<Integer> cooldownGetter = () -> 0;
    public Consumer<Integer> cooldownSetter = cooldown -> {};
    public BiConsumer<State, Boolean> stateSetter = (state, value) -> {};
    public int defaultCooldown = 100;
    public int actionTick = 10;
    public int length = 20;
    public float chance = 1.0F;
    public SoundEvent chargeSound;
    public SoundEvent releaseSound;
    public float soundVolume = 1.0F;

    public static <S extends TimedActionSettings> S edit(S settings, Consumer<S> consumer) {
      consumer.accept(settings);
      return settings;
    }
  }
}
