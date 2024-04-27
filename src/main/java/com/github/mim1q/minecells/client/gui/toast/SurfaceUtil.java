package com.github.mim1q.minecells.client.gui.toast;

import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.Identifier;

public class SurfaceUtil {
  public static Surface backgroundTexture(Identifier texture, int width, int height) {
    return (context, component) -> {
      context.drawTexture(
        texture,
        component.x() - width + component.width(), component.y(),
        0, 0,
        width, height,
        256, 256
      );
    };
  }

  public static Surface backgroundTexture(Identifier texture, int width, int height, int textureWidth, int textureHeight) {
    return (context, component) -> {
      context.drawTexture(
        texture,
        component.x() - width + component.width(), component.y(),
        0, 0,
        width, height,
        textureWidth, textureHeight
      );
    };
  }
}
