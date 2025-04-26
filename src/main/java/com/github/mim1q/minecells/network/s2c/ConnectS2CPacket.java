package com.github.mim1q.minecells.network.s2c;

import net.minecraft.util.math.Vec3d;

public record ConnectS2CPacket(
  Vec3d pos0,
  Vec3d pos1
) {
}
