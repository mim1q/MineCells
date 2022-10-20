package com.github.mim1q.minecells.dimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class MineCellsDimensions {
  public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecraft", "overworld"));
  public static final RegistryKey<World> PRISON = RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecells", "prison"));

  public static World getWorld(World world, RegistryKey<World> key) {
    MinecraftServer server = world.getServer();
    if (server == null) {
      return null;
    }
    return server.getWorld(key);
  }

  public static boolean isDimension(World world, RegistryKey<World> key) {
    return world.getRegistryKey().equals(key);
  }
}
