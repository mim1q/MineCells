package com.github.mim1q.minecells.structure.grid;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PrisonGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PromenadeUndergroundGridGenerator;
import com.github.mim1q.minecells.structure.grid.generator.PromenadeWallGenerator;
import com.github.mim1q.minecells.structure.grid.generator.RampartsGridGenerator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GridBasedStructure extends Structure {
  private static boolean awayFromWallsPredicate(Structure.Context ctx) {
    var x = MathHelper.abs(MathHelper.floorMod(ctx.chunkPos().x, 64));
    var z = MathHelper.abs(MathHelper.floorMod(ctx.chunkPos().z, 64));
    return x != 32 && x != 31 && z != 32 && z != 31;
  }

  public static final Codec<GridBasedStructure> PRISON_CODEC = createGridBasedStructureCodec(
    ctx -> new PrisonGridGenerator(), () -> MineCellsStructures.PRISON
  );
  public static final Codec<GridBasedStructure> PROMENADE_OVERGROUND_CODEC = createGridBasedStructureCodec(
    ctx -> RoomGridGenerator.single(MineCells.createId("promenade/overground_buildings")),
    () -> MineCellsStructures.PROMENADE_OVERGROUND,
    GridBasedStructure::awayFromWallsPredicate
  );
  public static final Codec<GridBasedStructure> PROMENADE_PIT_CODEC = createGridBasedStructureCodec(
    ctx -> RoomGridGenerator.single(MineCells.createId("promenade/overground_buildings/pit"), new Vec3i(0, -23, 0)),
    () -> MineCellsStructures.PROMENADE_PIT,
    GridBasedStructure::awayFromWallsPredicate
  );
  public static final Codec<GridBasedStructure> PROMENADE_UNDERGROUND_CODEC = createGridBasedStructureCodec(
    ctx -> new PromenadeUndergroundGridGenerator(), () -> MineCellsStructures.PROMENADE_UNDERGROUND
  );
  public static final Codec<GridBasedStructure> PROMENADE_WALL_X_CODEC = createGridBasedStructureCodec(
    ctx -> new PromenadeWallGenerator(false),
    () -> MineCellsStructures.PROMENADE_WALL_X,
    ctx -> MathHelper.abs(MathHelper.floorMod(ctx.chunkPos().z, 64)) == 32
        && MathHelper.floorMod(ctx.chunkPos().x, 16) == 0
  );
  public static final Codec<GridBasedStructure> PROMENADE_WALL_Z_CODEC = createGridBasedStructureCodec(
    ctx -> new PromenadeWallGenerator(true),
    () -> MineCellsStructures.PROMENADE_WALL_Z,
    ctx -> MathHelper.abs(MathHelper.floorMod(ctx.chunkPos().x, 64)) == 32
        && MathHelper.floorMod(ctx.chunkPos().z, 16) == 0
  );
  public static final Codec<GridBasedStructure> RAMPARTS_CODEC = createGridBasedStructureCodec(
    ctx -> new RampartsGridGenerator(false, ctx), () -> MineCellsStructures.RAMPARTS,
    ctx -> MathHelper.floorMod(ctx.chunkPos().x, 64) == 60 && MathHelper.floorMod(ctx.chunkPos().z, 64) == 54
  );
  public static final Codec<GridBasedStructure> RAMPARTS_SECOND_CODEC = createGridBasedStructureCodec(
    ctx -> new RampartsGridGenerator(true, ctx), () -> MineCellsStructures.RAMPARTS_SECOND,
    ctx -> MathHelper.floorMod(ctx.chunkPos().x, 64) == 60 && MathHelper.floorMod(ctx.chunkPos().z, 64) == 6
  );

  public static Codec<GridBasedStructure> createGridBasedStructureCodec(
    Function<Context, RoomGridGenerator> generatorProvider,
    Supplier<StructureType<?>> typeSupplier
  ) {
    return createGridBasedStructureCodec(generatorProvider, typeSupplier, ctx -> true);
  }

  public static Codec<GridBasedStructure> createGridBasedStructureCodec(
    Function<Context, RoomGridGenerator> generatorProvider,
    Supplier<StructureType<?>> typeSupplier,
    Predicate<Structure.Context> spawnPredicate
  ) {
    return RecordCodecBuilder.<GridBasedStructure>mapCodec((instance ->
      instance.group(
        Structure.configCodecBuilder(instance),
        HeightProvider.CODEC.fieldOf("start_height").forGetter(GridBasedStructure::getHeightProvider),
        Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(GridBasedStructure::getProjectStartToHeightmap)
      ).apply(instance, (config, heightProvider, projectStartToHeightmap) -> new GridBasedStructure(config, heightProvider, projectStartToHeightmap, generatorProvider, typeSupplier, spawnPredicate))
    )).codec();
  }

  private final Function<Context, RoomGridGenerator> generatorProvider;
  private List<GridPiece> pieces = new ArrayList<>();
  private final HeightProvider heightProvider;
  private final Optional<Heightmap.Type> projectStartToHeightmap;
  private final Supplier<StructureType<?>> typeSupplier;
  private final Predicate<Structure.Context> spawnPredicate;

  protected GridBasedStructure(
    Config config,
    HeightProvider heightProvider,
    Optional<Heightmap.Type> projectStartToHeightmap,
    Function<Context, RoomGridGenerator> generatorProvider,
    Supplier<StructureType<?>> typeSupplier,
    Predicate<Structure.Context> spawnPredicate
  ) {
    super(config);
    this.generatorProvider = generatorProvider;
    this.heightProvider = heightProvider;
    this.projectStartToHeightmap = projectStartToHeightmap;
    this.typeSupplier = typeSupplier;
    this.spawnPredicate = spawnPredicate;
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
    return spawnPredicate.test(context);
  }

  protected RoomGridGenerator getGenerator(Structure.Context context) {
    return this.generatorProvider.apply(context);
  }

  public HeightProvider getHeightProvider() {
    return heightProvider;
  }

  public Optional<Heightmap.Type> getProjectStartToHeightmap() {
    return projectStartToHeightmap;
  }
}
