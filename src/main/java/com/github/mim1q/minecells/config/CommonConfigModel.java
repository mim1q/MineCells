package com.github.mim1q.minecells.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("TextBlockMigration")
@Config(name = "minecells-common", wrapperName = "MineCellsCommonConfig")
public class CommonConfigModel {
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

  @Sync(Option.SyncMode.OVERRIDE_CLIENT)
  @Comment(" The maximum distance (in blocks) the Conjunctivius' Tentacle weapon can stretch when activated\n"
    + " Breaks at large distances (above around 64 blocks), so be careful with high values.")
  public int baseTentacleMaxDistance = 24;

  @Sync(Option.SyncMode.OVERRIDE_CLIENT)
  @Comment("The additional time (in ticks) after holding up a shield that allows you to parry an attack")
  public int additionalParryTime = 0;

  public static class Elevator {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment(" default: 256, min: 64, max: 320")
    @RangeConstraint(min = 64, max = 320)
    public int maxAssemblyHeight = 256;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment(" default: 1, min: 1, max: 10")
    @RangeConstraint(min = 1, max = 10)
    public int minAssemblyHeight = 1;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment(" default: 1.0, min: 0.1, max: 2.5")
    @RangeConstraint(min = 0.1, max = 2.5)
    public float speed = 1.0F;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment(" default: 0.01, min: 0.001, max: 0.1")
    @RangeConstraint(min = 0.001, max = 0.1)
    public float acceleration = 0.01F;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment(" default: 10.0, min: 0.0, max: 100.0")
    @RangeConstraint(min = 0.0, max = 100.0)
    public float damage = 10.0F;
  }

  public enum ForceServerThreadMode {
    ALWAYS,
    NEVER,
    DEFAULT
  }
}
