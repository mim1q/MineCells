package com.github.mim1q.minecells.structure;

import net.minecraft.util.math.random.Random;

import java.util.HashSet;

public class DungeonMap extends HashSet<DungeonMapRoom> {

  private final Random random;

  public DungeonMap(int rooms) {
    random = Random.create();
    for (int i = 0; i < rooms; i++) {
      add(new DungeonMapRoom(0, 0, random.nextBetween(6, 10), random.nextBetween(6, 10)));
    }
  }

  @Override
  public boolean add(DungeonMapRoom room) {
    room.moveAway(this, random);
    return super.add(room);
  }

  public void print() {
    int minY = this.stream().mapToInt(DungeonMapRoom::getMinZ).min().orElse(0);
    int minX = this.stream().mapToInt(DungeonMapRoom::getMinX).min().orElse(0);
    int maxY = this.stream().mapToInt(DungeonMapRoom::getMaxZ).max().orElse(0);
    int maxX = this.stream().mapToInt(DungeonMapRoom::getMaxX).max().orElse(0);

    for (int z = minY; z <= maxY; z++) {
      for (int x = minX; x <= maxX; x++) {
        boolean found = false;
        for (DungeonMapRoom room : this) {
          if (room.contains(x, z)) {
            if (room.getCorridor() != null
              && room.getCorridor().add(room.getCorridorDirection().getVector()).getX() == x
              && room.getCorridor().add(room.getCorridorDirection().getVector()).getZ() == z) {
              System.out.print("==");
            } else if (x == room.getMinX() || x == room.getMaxX() || z == room.getMinZ() || z == room.getMaxZ()) {
              System.out.print("XX");
            } else {
              System.out.print("..");
            }
            found = true;
            break;
          }
        }
        if (!found) {
          System.out.print("  ");
        }
      }
      System.out.println();
    }
  }
}
