package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("TextBlockMigration")
public class CommonConfig implements Config {
  @Override
  public String getName() {
    return "minecells-common";
  }

  @Override
  public String getExtension() {
    return "json5";
  }

  public Elevator elevator = new Elevator();

  @Comment(" Whether the entry door to boss rooms should remain unlocked")
  public boolean unlockedBossEntry = false;

  @Comment(" Forces the teleport between dimensions method to be used from the main server thread\n"
    + " Might be necessary when used with mods that mess with threading, like C2ME "
    + " (if DEFAULT is set, this option will enable itself if that mod is present)\n"
    + " Warning: This may cause weird desync issues, so use at your own risk and only if it crashes without it\n"
    + " Possible values: ALWAYS | NEVER | DEFAULT"
  )
  public ForceServerThreadMode teleportForceMainThread = ForceServerThreadMode.DEFAULT;

  @Comment(" Whether the Mine Cells data should automatically get wiped after major updates")
  public boolean autoWipeData = true;

  @Comment(" Disable the fall protection feature of the Ramparts. \n"
    + " This will let players explore the bottom of the dimension freely, but keep in mind: \n"
    + " that isn't the intended way to explore the dimension."
  )
  public boolean disableFallProtection = false;

  @Syncing
  @Comment(" The maximum distance (in blocks) the Conjunctivius' Tentacle weapon can stretch when activated\n"
    + " Breaks at large distances (above around 64 blocks), so be careful with high values.")
  public int baseTentacleMaxDistance = 24;

  @Override
  public void save() {
    elevator.maxAssemblyHeight = MathHelper.clamp(elevator.maxAssemblyHeight, 64, 320);
    elevator.minAssemblyHeight = MathHelper.clamp(elevator.minAssemblyHeight, 1, 10);
    elevator.speed = MathHelper.clamp(elevator.speed, 0.1F, 10.0F);
    elevator.acceleration = MathHelper.clamp(elevator.acceleration, 0.001F, 0.1F);
    elevator.damage = MathHelper.clamp(elevator.damage, 0.0F, 20.0F);
    Config.super.save();
  }

  public static class Elevator {
    @Syncing
    @Comment(" default: 256, min: 64, max: 320")
    public int maxAssemblyHeight = 256;

    @Syncing
    @Comment(" default: 1, min: 1, max: 10")
    public int minAssemblyHeight = 1;

    @Syncing
    @Comment(" default: 1.0, min: 0.1, max: 2.5")
    public float speed = 1.0F;

    @Syncing
    @Comment(" default: 0.01, min: 0.001, max: 0.1")
    public float acceleration = 0.01F;

    @Syncing
    @Comment(" default: 10.0, min: 0.0, max: 20.0")
    public float damage = 10.0F;
  }

  public enum ForceServerThreadMode {
    ALWAYS,
    NEVER,
    DEFAULT
  }
}
