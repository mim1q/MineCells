package com.github.mim1q.minecells.structure;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.Collection;

public class DungeonMapRoom {

  private BlockPos corridor = null;
  private Direction corridorDirection = null;
  private int minX;
  private int minZ;
  private int maxX;
  private int maxZ;

  public DungeonMapRoom(int x, int z, int sx, int sz) {
    minX = x;
    minZ = z;
    maxX = x + sx - 1;
    maxZ = z + sz - 1;
  }

  public void move(int x, int z) {
    minX += x;
    minZ += z;
    maxX += x;
    maxZ += z;
  }

  public BlockBox asBlockBox(int height) {
    return new BlockBox(minX, 0, minZ, maxX, height, maxZ);
  }

  public BlockBox intersection(DungeonMapRoom other) {
    int minX = Math.max(this.minX, other.minX);
    int minZ = Math.max(this.minZ, other.minZ);
    int maxX = Math.min(this.maxX, other.maxX);
    int maxZ = Math.min(this.maxZ, other.maxZ);
    if (minX > maxX || minZ > maxZ) {
      return null;
    }
    return new BlockBox(minX, 0, minZ, maxX, 0, maxZ);
  }

  public boolean contains(int x, int z) {
    return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
  }

  public void moveAway(Collection<DungeonMapRoom> rooms, Random random) {
    while (true) {
      BlockBox intersection = null;
      for (DungeonMapRoom room : rooms) {
        intersection = intersection(room);
        if (intersection != null) {
          break;
        }
      }
      if (intersection == null) {
        return;
      }
      Direction direction = Direction.Type.HORIZONTAL.random(random);
      this.corridor = intersection.getCenter();
      this.corridorDirection = direction;
      move(direction.getOffsetX(), direction.getOffsetZ());
    }
  }

  public int getMinX() {
    return minX;
  }

  public int getMinZ() {
    return minZ;
  }

  public int getMaxX() {
    return maxX;
  }

  public int getMaxZ() {
    return maxZ;
  }

  public BlockPos getCorridor() {
    return corridor;
  }

  public Direction getCorridorDirection() {
    return corridorDirection;
  }
}
