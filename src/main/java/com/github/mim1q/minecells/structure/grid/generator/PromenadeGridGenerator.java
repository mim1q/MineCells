package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeGridGenerator extends GridPiecesGenerator.RoomGridGenerator {

  public static final Identifier MAIN = MineCells.createId("promenade/overground_buildings/main");
  public static final Identifier SIDE = MineCells.createId("promenade/overground_buildings/side");
  public static final Identifier PIT = MineCells.createId("promenade/overground_buildings/pit");

  @Override
  protected void addRooms(Random random) {
    addTerrainFitRoom(Vec3i.ZERO, BlockRotation.random(random), MAIN);
    boolean special = false;
    for (Direction dir : Direction.Type.HORIZONTAL) {
      if (!special && random.nextFloat() < 0.2F) {
        addTerrainFitRoom(dir.getVector(), BlockRotation.random(random), PIT, new Vec3i(0, -23, 0));
        special = true;
        continue;
      }
      addTerrainFitRoom(dir.getVector(), BlockRotation.random(random), SIDE);
    }
  }
}
