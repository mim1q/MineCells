package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IDashEntity {
  boolean isDashCharging();

  void setDashCharging(boolean charging);

  boolean isDashReleasing();

  void setDashReleasing(boolean releasing);

  int getDashCooldown();

  void setDashCooldown(int ticks);

  int getDashMaxCooldown();

  float getDashDamage();

  SoundEvent getDashChargeSoundEvent();

  SoundEvent getDashReleaseSoundEvent();
}
