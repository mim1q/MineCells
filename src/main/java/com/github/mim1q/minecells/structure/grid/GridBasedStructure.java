package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PromenadeUndergroundGridGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridBasedStructure extends Structure {
  public static final Codec<GridBasedStructure> PRISON_CODEC = createGridBasedStructureCodec(
    PrisonGridGenerator::new, () -> MineCellsStructures.PRISON
  );
  public static final Codec<GridBasedStructure> PROMENADE_OVERGROUND_CODEC = createGridBasedStructureCodec(
    () -> RoomGridGenerator.single(MineCells.createId("promenade/overground_buildings")), () -> MineCellsStructures.PROMENADE_OVERGROUND
  );
  public static final Codec<GridBasedStructure> PROMENADE_PIT_CODEC = createGridBasedStructureCodec(
    () -> RoomGridGenerator.single(MineCells.createId("promenade/overground_buildings/pit"), new Vec3i(0, -23, 0)),
    () -> MineCellsStructures.PROMENADE_PIT
  );
  public static final Codec<GridBasedStructure> PROMENADE_UNDERGROUND_CODEC = createGridBasedStructureCodec(
    PromenadeUndergroundGridGenerator::new, () -> MineCellsStructures.PROMENADE_UNDERGROUND
  );

  public static Codec<GridBasedStructure> createGridBasedStructureCodec(
    Supplier<RoomGridGenerator> generatorProvider,
    Supplier<StructureType<?>> typeSupplier
  ) {
    return RecordCodecBuilder.<GridBasedStructure>mapCodec((instance ->
      instance.group(
        Structure.configCodecBuilder(instance),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(GridBasedStructure::getHeightProvider),
        Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(GridBasedStructure::getProjectStartToHeightmap)
      ).apply(instance, (config, heightProvider, projectStartToHeightmap) -> new GridBasedStructure(config, heightProvider, projectStartToHeightmap, generatorProvider, typeSupplier))
    )).codec();
  }

  private final Supplier<RoomGridGenerator> generatorProvider;
  private List<GridPiece> pieces = new ArrayList<>();
  private final HeightProvider heightProvider;
  private final Optional<Heightmap.Type> projectStartToHeightmap;
  private final Supplier<StructureType<?>> typeSupplier;

  protected GridBasedStructure(
    Config config,
    HeightProvider heightProvider,
    Optional<Heightmap.Type> projectStartToHeightmap,
    Supplier<RoomGridGenerator> generatorProvider
  ) {
    this(config, heightProvider, projectStartToHeightmap, generatorProvider, () -> null);
  }

  protected GridBasedStructure(
    Config config,
    HeightProvider heightProvider,
    Optional<Heightmap.Type> projectStartToHeightmap,
    Supplier<RoomGridGenerator> generatorProvider,
    Supplier<StructureType<?>> typeSupplier
  ) {
    super(config);
    this.generatorProvider = generatorProvider;
    this.heightProvider = heightProvider;
    this.projectStartToHeightmap = projectStartToHeightmap;
    this.typeSupplier = typeSupplier;
  }

  @Override
  public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
    if (!canSpawn(context)) {
      return Optional.empty();
    }
    ChunkPos chunkPos = context.chunkPos();
    int x = chunkPos.x * 16;
    int z = chunkPos.z * 16;
    int y = this.heightProvider.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
    int heightmapY = projectStartToHeightmap.map(
      type -> y + context.chunkGenerator().getHeightOnGround(x + 8, z + 8, type, context.world(), context.noiseConfig())
    ).orElse(0);
    BlockPos blockPos = new BlockPos(x, y + heightmapY, z);
    RoomGridGenerator generator = this.getGenerator(context);
    pieces = GridPiecesGenerator.generatePieces(blockPos, projectStartToHeightmap, context, 16, generator);
    return Optional.of(new Structure.StructurePosition(blockPos, collector -> {
      for (GridPiece piece : pieces) {
        collector.addPiece(piece);
      }
    }));
  }

  @Override
  public StructureType<?> getType() {
    return typeSupplier.get();
  }

  protected boolean canSpawn(Structure.Context context) {
    return true;
  }

  protected RoomGridGenerator getGenerator(Structure.Context context) {
    return this.generatorProvider.get();
  }

  public HeightProvider getHeightProvider() {
    return heightProvider;
  }

  public Optional<Heightmap.Type> getProjectStartToHeightmap() {
    return projectStartToHeightmap;
  }
}
