package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeGridGenerator extends GridPiecesGenerator.RoomGridGenerator {

  public static final Identifier MAIN = MineCells.createId("promenade/overground_buildings/main");
  public static final Identifier PIT = MineCells.createId("promenade/overground_buildings/pit");

  @Override
  protected void addRooms(Random random) {
    if (random.nextFloat() < 0.2F) {
      addRoom(Vec3i.ZERO, BlockRotation.random(random), PIT, new Vec3i(0, -23, 0));
      return;
    }
    addRoom(Vec3i.ZERO, BlockRotation.random(random), MAIN);
  }
}
