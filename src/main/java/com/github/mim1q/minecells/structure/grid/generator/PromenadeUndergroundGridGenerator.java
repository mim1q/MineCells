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

  @Override
  protected void addRooms(Random random) {
    super.addRooms(random);
    addRoom(new Vec3i(0, -1, 0), BlockRotation.NONE, SHAFT);
    addRoom(new Vec3i(0, -2, 0), BlockRotation.NONE, SHAFT_BOTTOM);
  }

  @Override
  protected void addMain(Random random) {
    addRoom(Vec3i.ZERO, BlockRotation.NONE, ENTRY);
  }
}
