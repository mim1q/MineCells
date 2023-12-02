package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.structure.Structure;

import static net.minecraft.util.BlockRotation.CLOCKWISE_180;
import static net.minecraft.util.BlockRotation.NONE;

public class RampartsGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier BASE = MineCells.createId("ramparts/base");
  private static final Identifier BOTTOM = MineCells.createId("ramparts/bottom");
  private static final Identifier TOP = MineCells.createId("ramparts/top");
  private static final Identifier END = MineCells.createId("ramparts/end");
  private static final Identifier BOTTOM_END = MineCells.createId("ramparts/bottom_end");
  private static final Identifier SPAWN = MineCells.createId("ramparts/spawn");
  private static final Identifier SPAWN_END = MineCells.createId("ramparts/spawn_end");

  private static final int BOTTOM_BASE_HEIGHT = 4;
  private static final int BASE_HEIGHT = 14;

  private final boolean secondPart;
  private final long seed;

  public RampartsGridGenerator(boolean secondPart, Structure.Context context) {
    this.secondPart = secondPart;
    this.seed = MathUtils.getClosestMultiplePosition(context.chunkPos().getStartPos(), 1024).hashCode() + context.seed();
  }

  @Override
  protected void addRooms(Random random) {
    random.setSeed(seed);
    addColumn(0, 0, BOTTOM_END, null, SPAWN_END, NONE);
    addColumn(0,1, BOTTOM, null, SPAWN, NONE);
    for (int i = 2; i <= 12; ++i) {
      addColumn(0, i, BOTTOM, TOP, BOTTOM, NONE);
    }
    addColumn(0, 13, BOTTOM_END,null, END, CLOCKWISE_180);
  }

  private void addColumn(int x, int z, Identifier base, Identifier top, Identifier second, BlockRotation rotation) {
    for (int i = 0; i < BOTTOM_BASE_HEIGHT; i++) {
      addRoom(new Vec3i(x, i, z), rotation, BASE);
    }
    for (int i = BOTTOM_BASE_HEIGHT; i <= BASE_HEIGHT - 2; i++) {
      addRoom(new Vec3i(x, i, z), rotation, base);
    }
    if (second != null) {
      addRoom(new Vec3i(x, BASE_HEIGHT - 1, z), rotation, second);
    }
    if (top != null) {
      addRoom(new Vec3i(x, BASE_HEIGHT, z), rotation, top);
    }
  }

  @Override
  protected void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset) {
    var newPos = pos.add(0, 0, this.secondPart ? -16 : 0);
    if (newPos.getZ() < -8 || newPos.getZ() > 8) {
      return;
    }
    super.addRoom(newPos, rotation, poolId, offset);
  }
}
