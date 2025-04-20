package com.github.mim1q.minecells.screen;

import com.github.mim1q.minecells.screen.doorway.DoorwaySelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class ScreenUtils {
  public static void openDoorwaySelectionScreen(BlockPos pos, BlockPos posOverride) {
    MinecraftClient.getInstance().setScreen(new DoorwaySelectionScreen(pos, posOverride));
  }
}
