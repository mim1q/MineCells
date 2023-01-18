package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PromenadeGridGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridBasedStructures {
  public static class Prison extends GridBasedStructure {
    public static final Codec<Prison> CODEC = createGridBasedStructureCodec(Prison::new);

    protected Prison(Config config, HeightProvider heightProvider, Optional<Heightmap.Type> projectStartToHeightmap) {
      super(config, heightProvider, projectStartToHeightmap, new PrisonGridGenerator());
    }

    @Override
    public StructureType<?> getType() {
      return MineCellsStructures.PRISON;
    }
  }

  public static class Promenade extends GridBasedStructure {
    public static final Codec<Promenade> CODEC = createGridBasedStructureCodec(Promenade::new);

    protected Promenade(Config config, HeightProvider heightProvider, Optional<Heightmap.Type> projectStartToHeightmap) {
      super(config, heightProvider, projectStartToHeightmap, new PromenadeGridGenerator());
    }

    @Override
    public StructureType<?> getType() {
      return MineCellsStructures.PROMENADE;
    }
  }
}
