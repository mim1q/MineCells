package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class TeleportUtils {
  public static void teleportToDimension(Entity entity, ServerWorld targetWorld, Vec3d position, float yaw) {
    var target = new TeleportTarget(
      position,
      entity.getVelocity(),
      yaw,
      0f
    );

    if (MineCells.COMMON_CONFIG.teleportForceMainThread) {
      targetWorld.getServer().execute(() -> FabricDimensions.teleport(entity, targetWorld, target));
      return;
    }

    FabricDimensions.teleport(entity, targetWorld, target);
  }
}
