package com.github.mim1q.minecells.network.c2s;

import net.minecraft.util.math.Vec3d;

public record UseTentacleWeaponC2SPacket(
  Vec3d targetPos
) {
}
