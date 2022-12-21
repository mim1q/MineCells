package com.github.mim1q.minecells.accessor;

import com.github.mim1q.minecells.entity.player.MineCellsPortalData;

public interface PlayerEntityAccessor {
  int getCells();
  void setCells(int amount);
  void setKingdomPortalCooldown(int cooldown);
  int getKingdomPortalCooldown();
  boolean canUseKingdomPortal();
  MineCellsPortalData getMineCellsPortalData();
  void setLastDimensionTranslationKey(String key);
  String getLastDimensionTranslationKey();
}
