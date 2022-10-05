package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.MineCells;
import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class MineCellsStructures {

  public static StructureType<BigDungeonStructure> BIG_DUNGEON = register("big_dungeon", BigDungeonStructure.CODEC);

  private static <S extends Structure> StructureType<S> register(String id, Codec<S> codec) {
    return Registry.register(Registry.STRUCTURE_TYPE, MineCells.createId(id), () -> codec);
  }

  public static void init() {

  }
}
