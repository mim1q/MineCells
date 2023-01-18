package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeGridGenerator extends GridPiecesGenerator.RoomGridGenerator {


  public static final Identifier SPAWN = MineCells.createId("promenade/overground_buildings");
  @Override
  protected void addRooms(Random random) {
    addRoom(Vec3i.ZERO, BlockRotation.NONE, SPAWN);
  }
}
