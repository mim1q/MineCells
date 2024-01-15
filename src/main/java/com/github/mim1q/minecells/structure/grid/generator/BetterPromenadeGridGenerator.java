package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class BetterPromenadeGridGenerator extends MultipartGridGenerator {
  private static final Identifier MAIN = MineCells.createId("promenade/overground_buildings");

  public BetterPromenadeGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    for (var i = 0; i < 24; ++i) {
      for (var j = 0; j < 24; ++j) {
        if (i == 0 || i == 23 || j == 0 || j == 23) {
          addRoom(RoomData.create(i, 0, j, MAIN).terrainFit());
        }
      }
//      addTerrainFitRoom(
//        new Vec3i(i, 0, i),
//        BlockRotation.NONE,
//        MAIN
//      );
    }
  }
}
