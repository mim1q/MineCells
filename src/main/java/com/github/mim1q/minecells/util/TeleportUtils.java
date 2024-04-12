package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.config.CommonConfig.ForceServerThreadMode;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class TeleportUtils {
  private static boolean shouldRunOnMainThread() {
    var config = MineCells.COMMON_CONFIG.teleportForceMainThread;
    if (config == ForceServerThreadMode.DEFAULT) {
      return FabricLoader.getInstance().isModLoaded("c2me");
    }

    return config == ForceServerThreadMode.ALWAYS;
  }

  public static void teleportToDimension(Entity entity, ServerWorld targetWorld, Vec3d position, float yaw) {
    var target = new TeleportTarget(
      position,
      entity.getVelocity(),
      yaw,
      0f
    );

    if (shouldRunOnMainThread()) {
      targetWorld.getServer().execute(() -> {
        FabricDimensions.teleport(entity, targetWorld, target);
        entity.setPosition(position);
      });
      return;
    }

    FabricDimensions.teleport(entity, targetWorld, target);
    entity.setPosition(position);
  }
}
