package com.github.mim1q.minecells.network.s2c;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public record ShockwaveClientEventS2CPacket(
  int blockId,
  BlockPos pos,
  boolean end
) {
  public ShockwaveClientEventS2CPacket(Block block, BlockPos postiion, boolean end) {
    this(Registries.BLOCK.getRawId(block), postiion, end);
  }
}
