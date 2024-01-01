package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class BetterPromenadeGridGenerator extends MultipartGridGenerator {
  private static final Identifier MAIN = MineCells.createId("promenade/overground_buildings");

  public BetterPromenadeGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override
  protected void addRooms(Random random) {
    for (var i = 0; i < 64; ++i) {
      addTerrainFitRoom(
        new Vec3i(i, 0, i),
        BlockRotation.NONE,
        MAIN
      );
    }
  }
}
