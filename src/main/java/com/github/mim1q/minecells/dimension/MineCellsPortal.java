package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

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
    System.out.println("Portal pos: " + portalPos);
    if (portalPos == null) {
      return;
    }
    player.teleport(prisonDimension, portalPos.getX(), portalPos.getY(), portalPos.getZ(), 0, 0);
  }

  private static BlockPos searchNearestPortalPos(ServerWorld prisonDimension, BlockPos searchPos) {
    return searchPos.add(8, 0, 8);
  }
}
