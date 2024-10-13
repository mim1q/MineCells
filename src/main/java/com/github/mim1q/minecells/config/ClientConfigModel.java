package com.github.mim1q.minecells.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.annotation.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Config(name = "minecells-client", wrapperName = "MineCellsClientConfig")
public class ClientConfigModel {

  public Rendering rendering = new Rendering();

  @Comment(" The screen shake intensity for various events.")
  public ScreenShake screenShake = new ScreenShake();

  @Comment(" If you enable this and have the 3D Weapon Pack installed, weapons will only be displayed as 3D in hand.\n"
    + " In the inventory, they will be displayed as the original 2D textures."
  )
  public boolean keepOriginalGuiModels = false;

  @Comment(" Whether to show a little red sword icon next to your crosshair if the weapon you are holding will deal" +
    " critical damage on the next hit.")
  public boolean showCritIndicator = true;

  @Comment("Loops the song playing in a given dimension. If disabled, the Vanilla music algorithm will be used.\n")
  public boolean experimentalMusicLooping = true;

  @Comment("Enables the MineCells custom boss bar rendering.")
  public boolean customBossBars = true;

  public static class ScreenShake {
    @Comment(" Affects all screen shake events. Can also be adjusted in the game's accessibility settings.")
    public float global = 1f;

    @Comment(" Weapons")
    public float weaponFlint = 1.5f;
    public float weaponLightningBolt = 0.5f;

    @Comment(" Shields")
    public float shieldBlock = 0.4f;
    public float shieldParry = 0.8f;

    @Comment(" Conjunctivius Boss")
    public float conjunctiviusSmash = 1f;
    public float conjunctiviusRoar = 1f;
    public float conjunctiviusDeath = 2f;

    @Comment(" Concierge Boss")
    public float conciergeLeap = 2f;
    public float conciergeStep = 0.25f;
    public float conciergeRoar = 1f;
    public float conciergeDeath = 2f;

    @Comment(" Other")
    public float explosion = 0.75f;
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
