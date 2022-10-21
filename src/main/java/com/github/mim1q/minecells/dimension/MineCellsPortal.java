package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsPointOfInterestTypes;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;

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
    int id = portal.getId();
    BlockPos searchPos = new BlockPos(MathUtils.getSpiralPosition(id).multiply(4096)).withY(32);
    BlockPos portalPos = searchNearestPortalPos(prisonDimension, searchPos);
    if (portalPos == null) {
      return;
    }
    Vec3d teleportPos = offsetPortalPos(portalPos, portal);
    player.teleport(prisonDimension, teleportPos.x, teleportPos.y, teleportPos.z, player.getYaw(), player.getPitch());
  }

  private static BlockPos searchNearestPortalPos(ServerWorld prisonDimension, BlockPos searchPos) {
    prisonDimension.getChunk(searchPos);
    var portalPos = prisonDimension.getPointOfInterestStorage().getNearestPosition(
      type -> type.value() == MineCellsPointOfInterestTypes.KINGDOM_PORTAL,
      searchPos,
      128,
      PointOfInterestStorage.OccupationStatus.ANY
    );
    if (portalPos.isEmpty()) {
      return null;
    }
    BlockState state = prisonDimension.getBlockState(portalPos.get());
    if (!state.isOf(MineCellsBlocks.KINGDOM_PORTAL_CORE)) {
      return null;
    }
    return portalPos.get();
  }

  private static Vec3d offsetPortalPos(BlockPos portalPos, KingdomPortalCoreBlockEntity portal) {
    Direction dir = portal.getDirection();
    return Vec3d.of(portalPos)
      .add(portal.getOffset())
      .add(Vec3d.of(dir.getVector()));
  }
}
