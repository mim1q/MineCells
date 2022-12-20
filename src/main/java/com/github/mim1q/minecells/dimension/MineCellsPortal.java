package com.github.mim1q.minecells.dimension;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class MineCellsPortal {
  public static void teleportPlayer(
      ServerPlayerEntity player,
      ServerWorld world,
      BlockPos pos,
      RegistryKey<World> targetDimension
  ) {
    ServerWorld targetWorld = world.getServer().getWorld(targetDimension);
    Vec3d teleportPos = MineCellsDimensions.getTeleportPos(targetDimension, pos);
    if (teleportPos == null) {
      BlockPos spawnPos = player.getSpawnPointPosition();
      if (spawnPos != null) {
        teleportPos = Vec3d.ofCenter(spawnPos);
        targetWorld = world.getServer().getWorld(player.getSpawnPointDimension());
      } else {
        targetWorld = world.getServer().getOverworld();
        teleportPos = Vec3d.ofCenter(targetWorld.getSpawnPos());
      }
    }
    TeleportTarget target = new TeleportTarget(
      teleportPos,
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
    FabricDimensions.teleport(player, targetWorld, target);
  }
}
