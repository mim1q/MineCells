package com.github.mim1q.minecells.client.gui;

import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;

import java.util.UUID;

public class ConjunctiviusClientBossBar extends ClientBossBar {
  private int tentacleCount = 0;
  private int maxTentacleCount = 0;

  public ConjunctiviusClientBossBar(UUID uuid, Text name, float percent, Color color, Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
    super(uuid, name, percent, color, style, darkenSky, dragonMusic, thickenFog);
  }

  public int getTentacleCount() {
    return tentacleCount;
  }

  public void setTentacleCount(int tentacleCount, int maxTentacleCount) {
    this.tentacleCount = tentacleCount;
    this.maxTentacleCount = maxTentacleCount;
  }

  public int getMaxTentacleCount() {
    return maxTentacleCount;
  }
}
