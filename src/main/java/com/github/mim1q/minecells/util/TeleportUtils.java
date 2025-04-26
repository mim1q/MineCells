package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import static com.github.mim1q.minecells.config.CommonConfigModel.ForceServerThreadMode;

public class TeleportUtils {
  private static boolean shouldRunOnMainThread() {
    var config = MineCells.COMMON_CONFIG.teleportForceMainThread();
    if (config == ForceServerThreadMode.DEFAULT) {
      return FabricLoader.getInstance().isModLoaded("c2me");
    }

    return config == ForceServerThreadMode.ALWAYS;
  }

  public static void teleportToDimension(Entity entity, ServerWorld targetWorld, Vec3d position, float yaw) {
    var target = new TeleportTarget(
      targetWorld,
      position,
      entity.getVelocity(),
      yaw,
      0f,
      TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET
    );

    if (shouldRunOnMainThread()) {
      targetWorld.getServer().execute(() -> {
        entity.teleportTo(target);
        entity.setPosition(position);
      });
      return;
    }

    entity.teleportTo(target);
    entity.setPosition(position);
  }
}
