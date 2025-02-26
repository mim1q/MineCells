package com.github.mim1q.minecells.screen;

import com.github.mim1q.minecells.screen.doorway.DoorwaySelectionScreen;
import net.minecraft.client.MinecraftClient;

public class ScreenUtils {
  public static void openDoorwaySelectionScreen() {
    MinecraftClient.getInstance().setScreen(new DoorwaySelectionScreen());
  }
}
