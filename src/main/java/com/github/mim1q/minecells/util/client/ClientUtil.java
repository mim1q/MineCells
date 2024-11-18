package com.github.mim1q.minecells.util.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class ClientUtil {
  public static Vec3d getClientCameraPos() {
    var camera = MinecraftClient.getInstance().getCameraEntity();
    if (camera == null) {
      return Vec3d.ZERO;
    }
    return camera.getPos();
  }
}
