package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class PromenadeWallGenerator extends GridPiecesGenerator.RoomGridGenerator {
  private static final Identifier TOP = MineCells.createId("promenade/border_wall/top");
  private static final Identifier MIDDLE = MineCells.createId("promenade/border_wall/middle");
  private static final Identifier BOTTOM = MineCells.createId("promenade/border_wall/bottom");
  private static final Identifier UNDERGROUND = MineCells.createId("promenade/border_wall/underground");

  private final boolean zAxis;

  public PromenadeWallGenerator(boolean zAxis) {
    this.zAxis = zAxis;
  }

  @Override
  protected void addRooms(Random random) {
    var rotation = zAxis ? BlockRotation.CLOCKWISE_90 : BlockRotation.NONE;
    var offset = zAxis ? new Vec3i(-7, -4, 0) : new Vec3i(0, -4, -7);
    for (int i = -7; i <= 8; i++) {
      var x = zAxis ? 0 : i;
      var z = zAxis ? i : 0;
      var pos = new Vec3i(x, (zAxis ? 3 : 8), z);
      this.addColumn(pos, rotation, offset);
    }
  }

  private void addColumn(Vec3i pos, BlockRotation rotation, Vec3i blockOffset) {
    for (int i = 0; i < 3; ++i) {
      this.addRoom(pos.down(i), rotation, UNDERGROUND, blockOffset);
    }
    this.addRoom(pos, rotation, BOTTOM, blockOffset);
    for (int i = 1; i < 7; ++i) {
      this.addRoom(pos.up(i), rotation, MIDDLE, blockOffset);
    }
    this.addRoom(pos.up(7), rotation, TOP, blockOffset);
  }
}
