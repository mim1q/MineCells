package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.StructureType;

public class GridBasedStructures {
  public static class Prison extends GridBasedStructure {
    public static final Codec<Prison> CODEC = createGridBasedStructureCodec(Prison::new);

    protected Prison(Config config, HeightProvider heightProvider) {
      super(config, heightProvider, new PrisonGridGenerator());
    }

    @Override
    public StructureType<?> getType() {
      return MineCellsStructures.PRISON;
    }
  }
}
