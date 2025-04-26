package com.github.mim1q.minecells.network.s2c;

import net.minecraft.util.math.Box;

public record SpawnRuneParticlesS2CPacket(
  double minX, double minY, double minZ, double maxX, double maxY, double maxZ
) {
  public SpawnRuneParticlesS2CPacket(Box box) {
    this(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
  }
}
