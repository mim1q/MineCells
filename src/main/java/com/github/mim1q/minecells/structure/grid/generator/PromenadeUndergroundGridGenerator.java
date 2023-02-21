package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeUndergroundGridGenerator extends PromenadeGridGenerator {

  public static final Identifier ENTRY = MineCells.createId("promenade/underground_buildings/entry");
  public static final Identifier SHAFT = MineCells.createId("promenade/underground_buildings/shaft");
  public static final Identifier SHAFT_BOTTOM = MineCells.createId("promenade/underground_buildings/shaft_bottom");
  public static final Identifier CHAIN_BOTTOM = MineCells.createId("promenade/underground_buildings/chain_bottom");
  public static final Identifier CHAIN_TOP = MineCells.createId("promenade/underground_buildings/chain_top");
  public static final Identifier ROOM = MineCells.createId("promenade/underground_buildings/room");
  public static final Identifier END_CENTER = MineCells.createId("promenade/underground_buildings/room_end_center");
  public static final Identifier END_RIGHT = MineCells.createId("promenade/underground_buildings/room_end_right");
  public static final Identifier END_LEFT = MineCells.createId("promenade/underground_buildings/room_end_left");

  @Override
  protected void addRooms(Random random) {
    super.addRooms(random);
    addRoom(new Vec3i(0, -1, 0), BlockRotation.NONE, SHAFT);
    Vec3i second = addFloor(new Vec3i(0, -2, 0), random, true);
    Vec3i third = addFloor(second.down(), random, false);
    addFloor(third.down(), random, false);
  }

  protected Vec3i addFloor(Vec3i startPos, Random random, boolean first) {
    Vec3i origin = new Vec3i(startPos.getX(), startPos.getY(), 0);
    addRoom(startPos, BlockRotation.NONE, first ? SHAFT_BOTTOM : CHAIN_BOTTOM);
    for (int z = startPos.getZ() + 1; z <= 3; z++) {
      addRoom(origin.add(0, 0, z), BlockRotation.NONE, ROOM);
    }
    addRoom(origin.add(0, 0, 4), BlockRotation.NONE, END_CENTER);
    addRoom(origin.add(1, 0, 4), BlockRotation.NONE, END_LEFT);
    addRoom(origin.add(-1, 0, 4), BlockRotation.NONE, END_RIGHT);
    boolean left = random.nextBoolean();
    int leftMinZ = -random.nextInt(4);
    for (int z = 3; z >= leftMinZ; z--) {
      addRoom(origin.add(1, 0, z), BlockRotation.NONE, z == leftMinZ && left ? CHAIN_TOP : ROOM);
    }
    int rightMinZ = -random.nextInt(4);
    for (int z = 3; z >= rightMinZ; z--) {
      addRoom(origin.add(-1, 0, z), BlockRotation.NONE, z == rightMinZ && !left ? CHAIN_TOP : ROOM);
    }
    return origin.add(left ? 1 : -1, 0, left ? rightMinZ : leftMinZ);
  }

  @Override
  protected Vec3i getOffset(Random random) {
    return Vec3i.ZERO;
  }

  @Override
  protected void addMain(Random random, Vec3i offset) {
    addRoom(offset, BlockRotation.NONE, ENTRY);
  }
}
