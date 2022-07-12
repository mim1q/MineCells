package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import draylar.omegaconfig.api.Syncing;
import net.minecraft.util.math.MathHelper;

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

  @Override
  public void save() {
    this.elevator.maxAssemblyHeight = MathHelper.clamp(elevator.maxAssemblyHeight, 64, 320);
    this.elevator.minAssemblyHeight = MathHelper.clamp(elevator.minAssemblyHeight, 1, 10);
    this.elevator.speed = MathHelper.clamp(elevator.speed, 0.1F, 10.0F);
    this.elevator.acceleration = MathHelper.clamp(elevator.acceleration, 0.001F, 0.1F);
    this.elevator.damage = MathHelper.clamp(elevator.damage, 0.0F, 20.0F);
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
}
