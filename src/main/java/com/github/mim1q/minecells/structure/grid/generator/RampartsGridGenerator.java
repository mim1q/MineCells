package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.structure.Structure;

import static net.minecraft.util.BlockRotation.CLOCKWISE_180;
import static net.minecraft.util.BlockRotation.NONE;

public class RampartsGridGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier BASE = MineCells.createId("ramparts/base");
  private static final Identifier BOTTOM = MineCells.createId("ramparts/bottom");
  private static final Identifier TOP = MineCells.createId("ramparts/top");
  private static final Identifier TOP_ENTRY = MineCells.createId("ramparts/top_entry");
  private static final Identifier ROOM_ENTRY = MineCells.createId("ramparts/room_entry");
  private static final Identifier ROOM = MineCells.createId("ramparts/room");
  private static final Identifier ROOM_END = MineCells.createId("ramparts/room_end");
  private static final Identifier ROOM_EXIT = MineCells.createId("ramparts/room_exit");
  private static final Identifier END = MineCells.createId("ramparts/end");
  private static final Identifier BOTTOM_END = MineCells.createId("ramparts/bottom_end");
  private static final Identifier SPAWN = MineCells.createId("ramparts/spawn");
  private static final Identifier SPAWN_END = MineCells.createId("ramparts/spawn_end");

  private static final int LOWER_BASE_HEIGHT = 4;
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
    addWall(0, -8, 6, true);
    addWall(1, -1, 5, false);
  }

  private void addWall(int x, int z, int length, boolean spawn) {
    var rooms = (length >= 4 && !spawn);
    addColumn(x, z, 0, 0, BOTTOM_END, null, spawn ? SPAWN_END : END, NONE, NONE);
    if (spawn) {
      addColumn(x, z, 1, 0, BOTTOM, null, SPAWN, NONE, NONE);
    }
    for (int i = spawn ? 2 : 1; i <= length; ++i) {
      addColumn(
        x, z, i,
        (rooms && i > 1 && i < length) ? 2 : 0,
        BOTTOM,
        (rooms && i == 1) ? TOP_ENTRY : TOP,
        (rooms && i > 1 && i < length) ? null : BOTTOM,
        NONE,
        NONE
      );
    }
    if (rooms) {
      addDungeonRooms(x, BASE_HEIGHT - 1, z + 1, length, 3, NONE);
    }
    addColumn(x, z, length + 1, 0, BOTTOM_END, null, END, CLOCKWISE_180, NONE);
  }

  private void addDungeonRooms(
    int startX,
    int startY,
    int startZ,
    int length,
    int height,
    BlockRotation offsetRotation
  ) {
    for (int y = startY; y > startY - height; y--) {
      for (int offset = 0; offset < length; offset++) {

        var x = startX + offset * offsetRotation.rotate(Direction.SOUTH).getOffsetX();
        var z = startZ + offset * offsetRotation.rotate(Direction.SOUTH).getOffsetZ();

        var reversed = (y % 2 == 1) ^ (startY % 2 == 1);
        var roomType = ROOM;
        if (reversed && offset == length - 1 || !reversed && offset == 0) {
          roomType = ROOM_ENTRY;
        }
        if (reversed && offset == 0 || !reversed && offset == length - 1) {
          roomType = y == startY - height + 1 ? ROOM_EXIT : ROOM_END;
        }

        addRoom(new Vec3i(x, y, z), reversed ? CLOCKWISE_180 : NONE, roomType);
      }
    }
  }

  private void addColumn(
    int startX,
    int startZ,
    int offset,
    int freeY,
    Identifier bottom,
    Identifier top,
    Identifier second,
    BlockRotation rotation,
    BlockRotation offsetRotation
  ) {
    var x = startX + offset * offsetRotation.rotate(Direction.SOUTH).getOffsetX();
    var z = startZ + offset * offsetRotation.rotate(Direction.SOUTH).getOffsetZ();
    for (int i = 0; i < LOWER_BASE_HEIGHT; i++) {
      addRoom(new Vec3i(x, i, z), rotation, BASE);
    }
    for (int i = LOWER_BASE_HEIGHT; i <= BASE_HEIGHT - 2 - freeY; i++) {
      addRoom(new Vec3i(x, i, z), rotation, bottom);
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
