package com.github.mim1q.minecells.accessor;

import com.github.mim1q.minecells.world.state.MineCellsData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;

public interface PlayerEntityAccessor {
  int getCells();
  void setCells(int amount);
  PlayerSpecificMineCellsData getMineCellsData();
  MineCellsData.PlayerData getCurrentMineCellsPlayerData();
  void setMineCellsData(PlayerSpecificMineCellsData data);
  void addBalancedBladeStack();
  int getBalancedBladeStacks();
  void setInvincibilityFrames(int frames);
}
