package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfig implements Config {

  @Override
  public String getName() {
    return "minecells-client";
  }

  @Override
  public String getExtension() {
    return "json5";
  }

  public Rendering rendering = new Rendering();

  @Comment(" The screen shake intensity for various events.")
  public ScreenShake screenShake = new ScreenShake();

  @Override
  public void save() {
    Config.super.save();
  }

  public static class ScreenShake {
    public float weaponFlint = 2f;

    public float conjunctiviusSmash = 1f;
    public float conjunctiviusRoar = 1f;
    public float conjunctiviusDeath = 2f;

    public float conciergeLeap = 2f;
    public float conciergeStep = 0.25f;
    public float conciergeRoar = 1f;
    public float conciergeDeath = 2f;
  }

  public static class Rendering {
    @Comment(" default: false")
    public boolean opaqueParticles = false;

    @Comment(" default: true")
    public boolean shockerGlow = true;

    @Comment(" default: true")
    public boolean grenadierGlow = true;

    @Comment(" default: true")
    public boolean leapingZombieGlow = true;

    @Comment(" default: true")
    public boolean disgustingWormGlow = true;

    @Comment(" default: true")
    public boolean protectorGlow = true;

    @Comment(" default: true")
    public boolean rancidRatGlow = true;

    @Comment(" default: true")
    public boolean scorpionGlow = true;
  }
}
