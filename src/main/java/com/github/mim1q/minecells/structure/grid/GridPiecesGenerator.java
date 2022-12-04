package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class GridPiecesGenerator {
  public static List<GridPiece> generatePieces(BlockPos startPos, Structure.Context context, int size) {
    List<GridPiece> list = new ArrayList<>();
    for (int z = 0; z < 4; z++) {
      for (int x = 0; x < 4; x++) {
        BlockPos pos = new BlockPos(startPos.getX() + x * size, startPos.getY(), startPos.getZ() + z * size);
        list.add(new GridPiece(context, MineCells.createId("testpiece"), pos, BlockRotation.random(context.random()), size));
      }
    }
    return list;
  }
}
