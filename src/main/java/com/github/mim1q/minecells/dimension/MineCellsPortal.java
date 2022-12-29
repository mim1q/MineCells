package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class MineCellsPortal {
  public static void teleportPlayerDownstream(
      ServerPlayerEntity player,
      ServerWorld world,
      BlockPos pos,
      Direction portalDirection,
      RegistryKey<World> targetDimension
  ) {
    PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
    playerAccessor.setKingdomPortalCooldown(10);
    playerAccessor.getMineCellsPortalData().push(world.getRegistryKey(), pos.add(portalDirection.getVector()));
    ServerWorld targetWorld = world.getServer().getWorld(targetDimension);
    Vec3d teleportPos = MineCellsDimensions.getTeleportPos(targetDimension, pos);
    if (teleportPos == null) {
      teleportToSpawnpoint(player, world);
    }
    TeleportTarget target = new TeleportTarget(
      teleportPos,
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
    FabricDimensions.teleport(player, targetWorld, target);
  }

  public static void teleportPlayerUpstream(ServerPlayerEntity player, ServerWorld world) {
    PlayerEntityAccessor playerAccessor = (PlayerEntityAccessor) player;
    playerAccessor.setKingdomPortalCooldown(10);
    Pair<String, BlockPos> portal = playerAccessor.getMineCellsPortalData().pop();
    if (portal == null) {
      teleportToSpawnpoint(player, world);
      return;
    }
    RegistryKey<World> worldKey = RegistryKey.of(Registry.WORLD_KEY, new Identifier(portal.getLeft()));
    ServerWorld targetWorld = world.getServer().getWorld(worldKey);
    TeleportTarget target = new TeleportTarget(
      Vec3d.ofCenter(portal.getRight()),
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
    FabricDimensions.teleport(player, targetWorld, target);
  }

  public static void teleportToSpawnpoint(ServerPlayerEntity player, ServerWorld world) {
    ServerWorld targetWorld = world.getServer().getOverworld();
    Vec3d teleportPos = Vec3d.ofCenter(targetWorld.getSpawnPos());
    BlockPos spawnPos = player.getSpawnPointPosition();
    if (spawnPos != null) {
      teleportPos = Vec3d.ofCenter(spawnPos);
      targetWorld = world.getServer().getWorld(player.getSpawnPointDimension());
    }
    TeleportTarget teleportTarget = new TeleportTarget(
      teleportPos,
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
    FabricDimensions.teleport(player, targetWorld, teleportTarget);
  }
}
