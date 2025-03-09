package com.github.mim1q.minecells.structure.grid.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class Vec3iCursor extends BlockPos.Mutable {
  private Direction direction = Direction.SOUTH;

  public Vec3iCursor(Vec3i pos, Direction direction) {
    super(pos.getX(), pos.getY(), pos.getZ());
    this.direction = direction;
  }

  public Vec3iCursor(Vec3i pos) {
    super(pos.getX(), pos.getY(), pos.getZ());
  }

  public Vec3iCursor split() {
    return new Vec3iCursor(this, direction);
  }

  public Vec3iCursor turnLeft() {
    direction = direction.rotateYCounterclockwise();
    return this;
  }

  public Vec3iCursor turnRight() {
    direction = direction.rotateYClockwise();
    return this;
  }

  public Vec3iCursor stepLeft() {
    this.move(direction.rotateYCounterclockwise());
    return this;
  }

  public Vec3iCursor stepRight() {
    this.move(direction.rotateYClockwise());
    return this;
  }

  public Vec3iCursor forward() {
    this.move(direction);
    return this;
  }

  public Vec3iCursor back() {
    this.move(direction.getOpposite());
    return this;
  }

  public Vec3iCursor up() {
    this.move(0, 1, 0);
    return this;
  }

  public Vec3iCursor down() {
    this.move(0, -1, 0);
    return this;
  }
}
