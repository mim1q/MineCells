package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class GridGenerator {
  public static List<GridPiece> generatePieces(BlockPos startPos, Structure.Context context) {
    StructureTemplateManager manager = context.structureTemplateManager();

    Registry<StructurePool> registry = context.dynamicRegistryManager().get(Registry.STRUCTURE_POOL_KEY);
    StructurePool pool = registry.get(MineCells.createId("portal"));
    if (pool == null) {
      throw new RuntimeException("Pool not found");
    }
    StructurePoolElement element = pool.getRandomElement(context.random());

    List<GridPiece> list = new ArrayList<>();
    for (int z = 0; z < 4; z++) {
      for (int x = 0; x < 4; x++) {
        BlockPos pos = new BlockPos(startPos.getX() + x * 32, startPos.getY(), startPos.getZ() + z * 32);
        BlockBox box = BlockBox.create(pos, pos.add(32, 32, 32));
        list.add(new GridPiece(manager, element, pos, BlockRotation.NONE, box));
      }
    }
    return list;
  }
}
