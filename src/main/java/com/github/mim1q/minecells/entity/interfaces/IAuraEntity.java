package com.github.mim1q.minecells.entity.interfaces;

import net.minecraft.sound.SoundEvent;

public interface IAuraEntity {
  boolean isAuraCharging();

  void setAuraCharging(boolean charging);

  boolean isAuraReleasing();

  void setAuraReleasing(boolean releasing);

  int getAuraCooldown();

  void setAuraCooldown(int ticks);

  int getAuraMaxCooldown();

  float getAuraDamage();

  SoundEvent getAuraChargeSoundEvent();

  SoundEvent getAuraReleaseSoundEvent();
}
