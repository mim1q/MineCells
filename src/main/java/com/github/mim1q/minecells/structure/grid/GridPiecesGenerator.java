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
    list.add(new GridPiece(context, MineCells.createId("prison/spawn"), startPos, BlockRotation.NONE, size));
    for (int i = 1; i <= 10; i++) {
      list.add(new GridPiece(context, MineCells.createId("prison/main_corridor"), startPos.add(0, 0, i * size), BlockRotation.NONE, size));
      int length = context.random().nextInt(3) + 3;
      for (int j = 1; j < length; j++) {
        list.add(new GridPiece(context, MineCells.createId("prison/side_corridor"), startPos.add(j * size, 0, i * size), BlockRotation.COUNTERCLOCKWISE_90, size));
      }
      int length2 = context.random().nextInt(3) + 3;
      for (int j = 1; j < length2; j++) {
        list.add(new GridPiece(context, MineCells.createId("prison/side_corridor"), startPos.add(-j * size, 0, i * size), BlockRotation.CLOCKWISE_90, size));
      }
    }
    return list;
  }
}
