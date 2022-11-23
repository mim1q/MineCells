package com.github.mim1q.minecells.accessor;

public interface PlayerEntityAccessor {
  int getCells();
  void setCells(int amount);
  void setKingdomPortalCooldown(int cooldown);
  int getKingdomPortalCooldown();
  boolean canUseKingdomPortal();
}
