package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class RampartsGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static Identifier BASE = new Identifier("minecells:ramparts/base/straight");

  @Override
  protected void addRooms(Random random) {
    for (int z = -8; z <= 1; z++) {
      addColumn(0, z, -1);
    }
    for (int z = 2; z <= 6; z++) {
      addColumn(0, z, 0);
    }
    for (int z = -1; z <= 8; z++) {
      addColumn(1, z, 2);
    }
  }

  private void addColumn(int x, int z, int additionalHeight) {
    for (int y = 0; y < 14 + additionalHeight; y++) {
      addRoom(new Vec3i(x, y, z), BlockRotation.CLOCKWISE_90, BASE);
    }
  }
}
