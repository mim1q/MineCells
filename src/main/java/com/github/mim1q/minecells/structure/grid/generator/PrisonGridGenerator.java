package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PrisonGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier SPAWN = MineCells.createId("prison/spawn");
  private static final Identifier STAIRCASE_DOWN = MineCells.createId("prison/spawn");

  @Override
  protected void addRooms(Random random) {
    generateFloor(Vec3i.ZERO, BlockRotation.NONE, SPAWN, STAIRCASE_DOWN, random);
    generateFloor(Vec3i.ZERO.add(0, -1, 0), BlockRotation.CLOCKWISE_180, STAIRCASE_DOWN, SPAWN, random);
    generateFloor(Vec3i.ZERO.add(0, -2, 0), BlockRotation.CLOCKWISE_90, SPAWN, STAIRCASE_DOWN, random);
  }

  protected void generateFloor(Vec3i pos, BlockRotation rotation, Identifier startPool, Identifier endPool, Random random) {
    addRoom(pos, BlockRotation.NONE.rotate(rotation), startPool);
    Vec3i unit = rotation.rotate(Direction.SOUTH).getVector();
    Vec3i rotatedUnit = rotation.rotate(Direction.EAST).getVector();
    for (int i = 1; i <= 5; i++) {
      addRoom(pos.add(unit.multiply(i)), BlockRotation.NONE.rotate(rotation), "prison/main_corridor");
      int length1 = random.nextInt(5) + 1;
      for (int j = 1; j <= length1; j++) {
        addRoom(pos.add(unit.multiply(i)).add(rotatedUnit.multiply(j)), BlockRotation.COUNTERCLOCKWISE_90.rotate(rotation), "prison/side_corridor");
      }
      int length2 = random.nextInt(5) + 1;
      for (int j = 1; j <= length2; j++) {
        addRoom(pos.add(unit.multiply(i)).add(rotatedUnit.multiply(-j)), BlockRotation.CLOCKWISE_90.rotate(rotation), "prison/side_corridor");
      }
    }
  }
}
