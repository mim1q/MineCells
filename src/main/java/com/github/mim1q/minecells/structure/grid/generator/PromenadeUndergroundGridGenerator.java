package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeUndergroundGridGenerator extends GridPiecesGenerator.RoomGridGenerator {

  public static final Identifier ENTRY = MineCells.createId("promenade/underground_buildings/entry");
  public static final Identifier SHAFT = MineCells.createId("promenade/underground_buildings/shaft");
  public static final Identifier SHAFT_BOTTOM = MineCells.createId("promenade/underground_buildings/shaft_bottom");

  public static final Identifier SIDE = MineCells.createId("promenade/overground_buildings/side");
  public static final Identifier PIT = MineCells.createId("promenade/overground_buildings/pit");

  @Override
  protected void addRooms(Random random) {
    addRoom(Vec3i.ZERO, BlockRotation.NONE, ENTRY);
    addRoom(new Vec3i(0, -1, 0), BlockRotation.NONE, SHAFT);
    addRoom(new Vec3i(0, -2, 0), BlockRotation.NONE, SHAFT_BOTTOM);

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
