package com.github.mim1q.minecells.network.s2c;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SpawnerRuneUpdateS2CPacket(
  BlockPos pos,
  long lastActivationTime,
  Identifier dataId
) {
}
