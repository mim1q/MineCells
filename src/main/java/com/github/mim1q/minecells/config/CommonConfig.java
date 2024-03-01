package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;
import net.minecraft.util.math.MathHelper;

import java.util.List;

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
  public Entities entities = new Entities();
  public Items items = new Items();

  @Comment(" Whether the entry door to boss rooms should remain unlocked")
  public boolean unlockedBossEntry = false;

  @SuppressWarnings("TextBlockMigration")
  @Comment(" Forces the teleport between dimensions method to be used from the main server thread\n"
    + " Might be necessary when used with mods that mess with threading, like C2ME\n"
    + " Warning: This may cause weird desync issues, so use at your own risk and only if it crashes without it\n"
  )
  public boolean teleportForceMainThread = false;

  @Comment(" Whether the Mine Cells data should automatically get wiped after major updates")
  public boolean autoWipeData = true;

  @Override
  public void save() {
    elevator.maxAssemblyHeight = MathHelper.clamp(elevator.maxAssemblyHeight, 64, 320);
    elevator.minAssemblyHeight = MathHelper.clamp(elevator.minAssemblyHeight, 1, 10);
    elevator.speed = MathHelper.clamp(elevator.speed, 0.1F, 10.0F);
    elevator.acceleration = MathHelper.clamp(elevator.acceleration, 0.001F, 0.1F);
    elevator.damage = MathHelper.clamp(elevator.damage, 0.0F, 20.0F);
    entities.cellDropChanceModifier = MathHelper.clamp(entities.cellDropChanceModifier, 0.1F, 10.0F);
    if (entities.cellDropChanceModifier == 0.0F) {
      entities.cellDropChanceModifier = 1.0F;
    }
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

  public static class Entities {
    @Comment(" default: 1.0, min: 0.1, max: 10.0")
    public float cellDropChanceModifier = 1.0F;

    @Comment(" default: false")
    public boolean allMobsDropCells = false;

    @Comment(" Which mobs outside of Mine Cells should drop cells (default: empty)")
    public List<String> cellDropWhitelist = List.of();
  }

  public static class Items {
    @Comment(" default: false")
    public boolean enableDevelopmentTab = false;
  }
}
