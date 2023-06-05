package com.github.mim1q.minecells.accessor;

import com.github.mim1q.minecells.world.state.MineCellsData;

public interface PlayerEntityAccessor {
  int getCells();
  void setCells(int amount);
  MineCellsData.PlayerData getMineCellsData();
  void setMineCellsData(MineCellsData.PlayerData data);
  void addBalancedBladeStack();
  int getBalancedBladeStacks();
  void setInvincibilityFrames(int frames);
}
