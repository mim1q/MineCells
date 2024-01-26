package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

import static net.minecraft.util.BlockRotation.*;

public class RampartsGridGenerator extends MultipartGridGenerator {
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

  private static final Identifier TOWER_BOTTOM = MineCells.createId("ramparts/tower/bottom");
  private static final Identifier TOWER_BASE = MineCells.createId("ramparts/tower/base");
  private static final Identifier TOWER_ROOM = MineCells.createId("ramparts/tower/room");
  private static final Identifier TOWER_ENTRY_ROOM = MineCells.createId("ramparts/tower/entry_room");
  private static final Identifier TOWER_TOP = MineCells.createId("ramparts/tower/top");
  private static final Identifier END_TOWER_ENTRANCE = MineCells.createId("ramparts/end_tower/entrance");
  private static final Identifier END_TOWER_ELEVATOR_SHAFT = MineCells.createId("ramparts/end_tower/elevator_shaft");
  private static final Identifier END_TOWER_EXIT = MineCells.createId("ramparts/end_tower/exit");

  private static final Identifier PLATFORM = MineCells.createId("ramparts/platform");
  private static final Identifier PLATFORM_UP = MineCells.createId("ramparts/platform_up");

  private static final int LOWER_BASE_HEIGHT = 4;
  private static final int BASE_HEIGHT = 12;

  public RampartsGridGenerator(int z) {
    super(0, z);
  }

  @Override
  protected void addRooms(Random random) {
    var turns = new boolean[]{random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
    if ((turns[0] && turns[1] && turns[2] && turns[3])
      || (!turns[0] && !turns[1] && !turns[2] && !turns[3])
    ) {
      turns[random.nextInt(4)] ^= true;
    }
    addWall(8, 0, BASE_HEIGHT, 3, true);

    int x = 8;
    int z = 2;
    int height = BASE_HEIGHT;
    for (int i = 0; i < 4; ++i) {
      var up = random.nextBoolean();

      x += turns[i] ? 2 : -2;
      var rot = turns[i] ? COUNTERCLOCKWISE_90 : CLOCKWISE_90;
      var length = 4 + random.nextInt(4);
      addRoom(new Vec3i(x - rot.rotate(Direction.SOUTH).getOffsetX(), height - 1, z + 1), rot, up ? PLATFORM_UP : PLATFORM);

      if (up) height += 1;
      addWall(x, z, height, length, false);

      var sideTowers = new boolean[]{random.nextFloat() < 0.75, random.nextFloat() < 0.75};
      if (!sideTowers[0] && !sideTowers[1]) {
        sideTowers[random.nextInt(2)] = true;
      }

      if (sideTowers[0]) {
        var nextRight = i != 3 && turns[i + 1];
        var prevRight = turns[i];
        var startZ = z + (prevRight ? 2 : 1);
        var addedZ = length - (nextRight ? 2 : 1);
        addTower(x - 2, height, startZ + random.nextInt(addedZ), random, CLOCKWISE_90);
      }

      if (sideTowers[1]) {
        var nextLeft = i != 3 && !turns[i + 1];
        var prevLeft = !turns[i];
        var startZ = z + (prevLeft ? 2 : 1);
        var addedZ = length - (nextLeft ? 2 : 1);
        addTower(x + 2, height, startZ + random.nextInt(addedZ), random, COUNTERCLOCKWISE_90);
      }
      z += length - 1;
    }

    addRoom(new Vec3i(x, height - 1, z + 2), NONE, PLATFORM_UP);
    addEndTower(x, height + 1, z + 3);
  }

  private void addWall(int x, int z, int height, int length, boolean spawn) {
    var rooms = (length >= 4 && !spawn);
    addColumn(x, z, 0, height, 0, BOTTOM_END, null, spawn ? SPAWN_END : END, NONE, NONE);
    if (spawn) {
      addColumn(x, z, 1, height, 0, BOTTOM, null, SPAWN, NONE, NONE);
    }
    for (int i = spawn ? 2 : 1; i <= length; ++i) {
      addColumn(
        x, z, i, height,
        (rooms && i > 1 && i < length) ? 1 : 0,
        BOTTOM,
        (rooms && i == 1) ? TOP_ENTRY : TOP,
        (rooms && i > 1 && i < length) ? null : BOTTOM,
        NONE,
        NONE
      );
    }
    if (rooms) {
      addDungeonRooms(x, height - 1, z + 1, length, 2, NONE);
    }
    addColumn(x, z, length + 1, height, 0, BOTTOM_END, null, END, CLOCKWISE_180, NONE);
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

  private void addTower(
    int x,
    int y,
    int z,
    Random random,
    BlockRotation rotation
  ) {
    var floating = random.nextBoolean();
    var height = 2 + random.nextInt(3);

    addRoom(new Vec3i(x - rotation.rotate(Direction.SOUTH).getOffsetX(), y - 1, z), rotation, PLATFORM);

    if (floating) {
      var underY = random.nextInt(1) + 2;
      addRoom(new Vec3i(x, y - underY, z), rotation, TOWER_BOTTOM);
      for (int i = y - underY + 1; i < y; i++) {
        addRoom(new Vec3i(x, i, z), rotation, TOWER_BASE);
      }
    } else for (int i = 0; i < y; i++) {
      addRoom(new Vec3i(x, i, z), rotation, TOWER_BASE);
    }
    for (int i = y; i < y + height; i++) {
      addRoom(new Vec3i(x, i, z), rotation, i == y ? TOWER_ENTRY_ROOM : TOWER_ROOM);
    }
    addRoom(new Vec3i(x, y + height, z), rotation, TOWER_TOP);
  }

  private void addEndTower(int x, int y, int z) {
    for (int i = 0; i < 10; i++) {
      addRoom(new Vec3i(x, i, z), NONE, TOWER_BASE);
    }
    addRoom(new Vec3i(x, 10, z), NONE, END_TOWER_EXIT);
    for (int i = 11; i < y; i++) {
      addRoom(new Vec3i(x, i, z), NONE, END_TOWER_ELEVATOR_SHAFT);
    }
    addRoom(new Vec3i(x, y, z), NONE, END_TOWER_ENTRANCE);
    addRoom(new Vec3i(x, y + 1, z), NONE, TOWER_TOP);
  }

  private void addColumn(
    int startX,
    int startZ,
    int offset,
    int height,
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
    for (int i = LOWER_BASE_HEIGHT; i <= height - 2 - freeY; i++) {
      addRoom(new Vec3i(x, i, z), rotation, bottom);
    }
    if (second != null) {
      addRoom(new Vec3i(x, height - 1, z), rotation, second);
    }
    if (top != null) {
      addRoom(new Vec3i(x, height, z), rotation, top);
    }
  }

//  @Override
//  protected void addRoom(Vec3i pos, BlockRotation rotation, Identifier poolId, Vec3i offset, boolean terrainFit) {
//    var newPos = pos.add(0, 0, this.secondPart ? -16 : 0);
//    if (newPos.getZ() < -8 || newPos.getZ() > 8) {
//      return;
//    }
//    super.addRoom(newPos, rotation, poolId, offset, terrainFit);
//  }
}
