package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsPointOfInterestTypes;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.world.state.OverworldPortals;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;

import java.util.Objects;

public class MineCellsPortal {
  public static void teleportPlayerFromOverworld(
    ServerPlayerEntity player,
    ServerWorld currentWorld,
    KingdomPortalCoreBlockEntity portal
  ) {
    ServerWorld prisonDimension = (ServerWorld) MineCellsDimensions.getWorld(currentWorld, MineCellsDimensions.PRISON);
    if (prisonDimension == null) {
      return;
    }
    int id = portal.getPortalId();
    BlockPos portalPos = new BlockPos(MathUtils.getSpiralPosition(id).multiply(4096)).withY(33).add(8, 0, 8);
    prisonDimension.getChunk(portalPos);
    player.teleport(prisonDimension, portalPos.getX(), portalPos.getY(), portalPos.getZ(), player.getYaw(), player.getPitch());
    ((PlayerEntityAccessor) player).setKingdomPortalCooldown(50);
  }

  public static void teleportPlayerToOverworld(
    ServerPlayerEntity player,
    ServerWorld currentWorld,
    KingdomPortalCoreBlockEntity portal
  ) {
    ServerWorld overworld = (ServerWorld) MineCellsDimensions.getWorld(currentWorld, MineCellsDimensions.OVERWORLD);
    if (overworld == null) {
      return;
    }
    Vec3i closest4096Multiple = MathUtils.getClosestMultiplePosition(portal.getPos(), 4096);
    int x = closest4096Multiple.getX() / 4096;
    int z = closest4096Multiple.getZ() / 4096;
    int portalId = MathUtils.getSpiralIndex(x, z);
    var portalPos = Objects.requireNonNull(overworld.getPersistentStateManager().get(
      OverworldPortals::new,
      "minecells:overworld_portals"
    )).getPortalPos(portalId);

    player.teleport(overworld, portalPos.getX(), portalPos.getY(), portalPos.getZ(), player.getYaw(), player.getPitch());
    ((PlayerEntityAccessor) player).setKingdomPortalCooldown(50);
  }

  private static BlockPos searchNearestPortalPos(ServerWorld prisonDimension, BlockPos searchPos) {
    var portalPos = prisonDimension.getPointOfInterestStorage().getNearestPosition(
      type -> type.value() == MineCellsPointOfInterestTypes.KINGDOM_PORTAL,
      searchPos,
      128,
      PointOfInterestStorage.OccupationStatus.ANY
    );
    if (portalPos.isEmpty()) {
      return null;
    }
    return portalPos.get();
  }
}
