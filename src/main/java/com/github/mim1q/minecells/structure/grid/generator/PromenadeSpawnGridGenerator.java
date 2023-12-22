package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeSpawnGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier SPAWN = MineCells.createId("promenade/spawn");
  private static final Identifier RAMPARTS_TOWER = MineCells.createId("promenade/ramparts_tower");

  @Override
  protected void addRooms(Random random) {
    addRoom(new Vec3i(0, 0, 0), BlockRotation.NONE, SPAWN);
    addRoom(new Vec3i(-1, 0, 1), BlockRotation.CLOCKWISE_90, RAMPARTS_TOWER);
  }
}
