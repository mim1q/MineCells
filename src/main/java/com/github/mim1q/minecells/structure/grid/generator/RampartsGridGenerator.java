package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.structure.Structure;

import static net.minecraft.util.BlockRotation.*;

public class RampartsGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier SPAWN = MineCells.createId("ramparts/spawn");
  private static final Identifier BASE = MineCells.createId("ramparts/base");
  private static final Identifier BASE_CORNER = MineCells.createId("ramparts/base_corner");
  private static final Identifier BASE_BOTTOM = MineCells.createId("ramparts/base_bottom");
  private static final Identifier TOP = MineCells.createId("ramparts/top");
  private static final Identifier TOP_CORNER = MineCells.createId("ramparts/top_corner");
  private static final Identifier TOP_ELEVATOR = MineCells.createId("ramparts/top_elevator");
  private static final Identifier UNDERGROUND = MineCells.createId("ramparts/underground");

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
    var sectionPos = addSection(random, new Vec3i(0, BASE_HEIGHT + 1, -7), 5, true);
    for (int i = 0; i < 5; i++) {
      var length = 4 + random.nextInt(2);
      sectionPos = addSection(random, sectionPos, length, false);
    }
    addColumn(sectionPos.getX(), sectionPos.getZ(), null, 0);
  }

  private Vec3i addSection(Random random, Vec3i start, int length, boolean first) {
    var flipCorner = random.nextBoolean();
    var xOffset = flipCorner ? -1 : 1;
    if (first) {
      addColumn(start.getX() + xOffset, start.getZ(), SPAWN, 0);
    } else {
      addCorner(start.getX(), start.getZ(), flipCorner ? CLOCKWISE_180 : COUNTERCLOCKWISE_90);
      addCorner(start.getX() + xOffset, start.getZ(), flipCorner ? NONE : CLOCKWISE_90);
    }
    var undergroundHeight = 3 + random.nextInt(3);
    for (int i = 1; i < length; i++) {
      addColumn(start.getX() + xOffset, start.getZ() + i, i == 2 ? TOP_ELEVATOR : null, undergroundHeight);
    }
    //
    for (int i = 1; i <= undergroundHeight; i++) {
      for (int j = 1; j < length; j++) {
        if (i == 1 && j == 2) continue;
        addRoom(start.add(xOffset, -i, j), random.nextBoolean() ? CLOCKWISE_180 : NONE, random.nextBoolean() ? UNDERGROUND : BASE);
      }
    }
    return start.add(xOffset, 0, length);
  }



  private void addColumn(int x, int z, Identifier top, int freeSpace) {
    for (int i = 0; i < BOTTOM_BASE_HEIGHT; i++) {
      addRoom(new Vec3i(x, i, z), CLOCKWISE_180, BASE_BOTTOM);
    }
    for (int i = BOTTOM_BASE_HEIGHT; i <= BASE_HEIGHT - freeSpace; i++) {
      addRoom(new Vec3i(x, i, z), CLOCKWISE_180, BASE);
    }
    if (top == null) {
      if (freeSpace == 0) {
        addRoom(new Vec3i(x, BASE_HEIGHT, z), CLOCKWISE_180, BASE);
      }
      addRoom(new Vec3i(x, BASE_HEIGHT + 1, z), CLOCKWISE_180, TOP);
    } else {
      addRoom(new Vec3i(x, BASE_HEIGHT, z), CLOCKWISE_180, top);
    }
  }

  private void addCorner(int x, int z, BlockRotation rotation) {
    for (int i = 0; i < BOTTOM_BASE_HEIGHT; i++) {
      addRoom(new Vec3i(x, i, z), BlockRotation.NONE, BASE_BOTTOM);
    }
    for (int i = BOTTOM_BASE_HEIGHT; i <= BASE_HEIGHT; i++) {
      addRoom(new Vec3i(x, i, z), rotation, BASE_CORNER);
    }
    addRoom(new Vec3i(x, BASE_HEIGHT + 1, z), rotation, TOP_CORNER);
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
