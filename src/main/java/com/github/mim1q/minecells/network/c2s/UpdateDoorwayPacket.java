package com.github.mim1q.minecells.network.c2s;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record UpdateDoorwayPacket(
  BlockPos doorwayPos,
  Identifier dimensionId,
  boolean onlyOwnerCanUse
) {
}
