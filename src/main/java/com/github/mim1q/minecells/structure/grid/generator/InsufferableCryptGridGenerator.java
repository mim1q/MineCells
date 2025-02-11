package com.github.mim1q.minecells.structure.grid.generator;

import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class InsufferableCryptGridGenerator extends MultipartGridGenerator {
  public InsufferableCryptGridGenerator(int xPart, int zPart) {
    super(xPart, zPart);
  }

  @Override protected void addRooms(Random random) {
    addRoom(room(new Vec3i(32, 2, 32), EMPTY)
      .specialPoint(SpecialPointIds.ENTRANCE, new Vec3i(6, 9, 1), BlockRotation.NONE));
  }

  @Override public int getVersion() {
    return 1;
  }
}
