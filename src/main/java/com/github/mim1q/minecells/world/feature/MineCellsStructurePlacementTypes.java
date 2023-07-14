package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.placement.BetterRandomSpreadPlacement;
import com.github.mim1q.minecells.world.placement.InsideGridPlacement;
import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public class MineCellsStructurePlacementTypes {
  public static final StructurePlacementType<InsideGridPlacement> INSIDE_GRID = register("inside_grid", InsideGridPlacement.CODEC);
  public static final StructurePlacementType<BetterRandomSpreadPlacement> BETTER_RANDOM_SPREAD = register("better_random_spread", BetterRandomSpreadPlacement.CODEC);

  public static void init() { }

  private static <SP extends StructurePlacement> StructurePlacementType<SP> register(String name, Codec<SP> codec) {
    return Registry.register(Registry.STRUCTURE_PLACEMENT, MineCells.createId(name), () -> codec);
  }
}
