package com.github.mim1q.minecells.util;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ModelUtils {
  public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
    VoxelShape[] buffer = new VoxelShape[]{shape, VoxelShapes.empty()};

    int times = (to.getHorizontal() - from.getHorizontal() + 4) % 4;
    for (int i = 0; i < times; i++) {
      buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combine(
        buffer[1],
        VoxelShapes.cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX),
        BooleanBiFunction.OR)
      );
      buffer[0] = buffer[1];
      buffer[1] = VoxelShapes.empty();
    }
    return buffer[0];
  }
}
