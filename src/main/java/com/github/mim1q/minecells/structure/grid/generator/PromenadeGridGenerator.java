package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeGridGenerator extends GridPiecesGenerator.RoomGridGenerator {

  public static final Identifier MAIN = MineCells.createId("promenade/overground_buildings/main");
  public static final Identifier SIDE = MineCells.createId("promenade/overground_buildings/side");
  public static final Identifier PIT = MineCells.createId("promenade/overground_buildings/pit");

  public static final Vec3i[] CLOSER_NEIGHBORS = {
    new Vec3i(-1, 0,  0),
    new Vec3i( 0, 0, -1),
    new Vec3i( 0, 0,  1),
    new Vec3i( 1, 0,  0)
  };

  public static final Vec3i[] FARTHER_NEIGHBORS = {
    new Vec3i(-1, 0, -1),
    new Vec3i(-1, 0,  1),
    new Vec3i( 1, 0, -1),
    new Vec3i( 1, 0,  1),
  };

  @Override
  protected void addRooms(Random random) {
    addMain(random);

    boolean special = false;

    for (Vec3i vec : CLOSER_NEIGHBORS) {
      if (random.nextFloat() > 0.66F) {
        continue;
      }
      if (!special && random.nextFloat() < 0.1F) {
        addTerrainFitRoom(vec, BlockRotation.random(random), PIT, new Vec3i(0, -23, 0));
        special = true;
        continue;
      }
      addTerrainFitRoom(vec, BlockRotation.random(random), SIDE);
    }

    for (Vec3i vec : FARTHER_NEIGHBORS) {
      if (random.nextFloat() > 0.25F) {
        continue;
      }
      if (!special && random.nextFloat() < 0.33F) {
        addTerrainFitRoom(vec, BlockRotation.random(random), PIT, new Vec3i(0, -23, 0));
        special = true;
        continue;
      }
      addTerrainFitRoom(vec, BlockRotation.random(random), SIDE);
    }
  }

  protected void addMain(Random random) {
    addRoom(Vec3i.ZERO, BlockRotation.random(random), MAIN);
  }
}
