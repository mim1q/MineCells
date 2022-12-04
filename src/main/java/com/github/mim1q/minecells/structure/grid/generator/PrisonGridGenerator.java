package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PrisonGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  @Override
  protected void addRooms(Random random) {
    addRoom(new Vec3i(0, 0, 0), BlockRotation.NONE, "prison/spawn");
    for (int i = 1; i <= 10; i++) {
      addRoom(new Vec3i(0, 0, i), BlockRotation.NONE, "prison/main_corridor");
      int length1 = random.nextInt(3) + 3;
      for (int j = 1; j <= length1; j++) {
        addRoom(new Vec3i(j, 0, i), BlockRotation.COUNTERCLOCKWISE_90, "prison/side_corridor");
      }
      int length2 = random.nextInt(3) + 3;
      for (int j = 1; j <= length2; j++) {
        addRoom(new Vec3i(-j, 0, i), BlockRotation.CLOCKWISE_90, "prison/side_corridor");
      }
    }
  }
}
