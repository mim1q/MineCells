package com.github.mim1q.minecells.accessor;


public interface PlayerEntityAccessor {
  int getCells();
  void setCells(int amount);
  void addBalancedBladeStack();
  int getBalancedBladeStacks();
  void setInvincibilityFrames(int frames);
}
