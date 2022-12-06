package com.github.mim1q.minecells.structure.grid;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import net.minecraft.block.JigsawBlock;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

import java.util.*;

// Most of this code is copied from Mojang's StructurePoolBasedGenerator, modified to work in Mine Cells's use case

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
public class MineCellsStructurePoolBasedGenerator {
  static final Logger LOGGER = LogUtils.getLogger();

  public MineCellsStructurePoolBasedGenerator() {
  }

  private static Optional<Structure.StructurePosition> generate(
    Structure.Context context,
    RegistryEntry<StructurePool> structurePool,
    Optional<Identifier> id,
    int size,
    BlockPos pos,
    Optional<Heightmap.Type> projectStartToHeightmap,
    BlockRotation rotation
  ) {
    DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
    ChunkGenerator chunkGenerator = context.chunkGenerator();
    StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
    HeightLimitView heightLimitView = context.world();
    ChunkRandom chunkRandom = context.random();
    Registry<StructurePool> registry = dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY);
    StructurePool structurePool2 = structurePool.value();
    StructurePoolElement structurePoolElement = structurePool2.getRandomElement(chunkRandom);
    if (structurePoolElement == EmptyPoolElement.INSTANCE) {
      return Optional.empty();
    } else {
      BlockPos blockPos;
      if (id.isPresent()) {
        Identifier identifier = id.get();
        Optional<BlockPos> optional = findStartingJigsawPos(structurePoolElement, identifier, pos, rotation, structureTemplateManager, chunkRandom);
        if (optional.isEmpty()) {
          LOGGER.error("No starting jigsaw {} found in start pool {}", identifier, structurePool.getKey().orElseThrow().getValue());
          return Optional.empty();
        }

        blockPos = optional.get();
      } else {
        blockPos = pos;
      }

      Vec3i vec3i = blockPos.subtract(pos);
      BlockPos blockPos2 = pos.subtract(vec3i);
      PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureTemplateManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), rotation, structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, rotation));
      BlockBox blockBox = poolStructurePiece.getBoundingBox();
      int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
      int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
      int k;
      k = projectStartToHeightmap.map(type -> pos.getY() + chunkGenerator.getHeightOnGround(i, j, type, heightLimitView, context.noiseConfig())).orElseGet(blockPos2::getY);

      int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
      poolStructurePiece.translate(0, k - l, 0);
      int m = k + vec3i.getY();
      return Optional.of(new Structure.StructurePosition(new BlockPos(i, m, j), (collector) -> {
        List<PoolStructurePiece> list = Lists.newArrayList();
        list.add(poolStructurePiece);
        if (size > 0) {
          Box box = new Box(i - 128, m - 128, j - 128, i + 128 + 1, m + 128 + 1, j + 128 + 1);
          VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
          generate(context.noiseConfig(), size, chunkGenerator, structureTemplateManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
          Objects.requireNonNull(collector);
          list.forEach(collector::addPiece);
        }
      }));
    }
  }

  private static Optional<BlockPos> findStartingJigsawPos(StructurePoolElement pool, Identifier id, BlockPos pos, BlockRotation rotation, StructureTemplateManager structureManager, ChunkRandom random) {
    List<StructureTemplate.StructureBlockInfo> list = pool.getStructureBlockInfos(structureManager, pos, rotation, random);
    Optional<BlockPos> optional = Optional.empty();

    for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
      Identifier identifier = Identifier.tryParse(structureBlockInfo.nbt.getString("name"));
      if (id.equals(identifier)) {
        optional = Optional.of(structureBlockInfo.pos);
        break;
      }
    }

    return optional;
  }

  private static void generate(NoiseConfig noiseConfig, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape) {
    StructurePoolGenerator structurePoolGenerator = new StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
    structurePoolGenerator.structurePieces.addLast(new ShapedPoolStructurePiece(firstPiece, new MutableObject<>(pieceShape), 0));

    while(!structurePoolGenerator.structurePieces.isEmpty()) {
      ShapedPoolStructurePiece shapedPoolStructurePiece = structurePoolGenerator.structurePieces.removeFirst();
      structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, heightLimitView, noiseConfig);
    }

  }

  public static void generate(
    StructureWorldAccess world,
    ChunkGenerator chunkGenerator,
    DynamicRegistryManager registryManager,
    StructureTemplateManager structureTemplateManager,
    StructureAccessor structureAccessor,
    NoiseConfig noiseConfig,
    Random random,
    int seed,
    RegistryEntry<StructurePool> structurePool,
    int i,
    BlockPos pos,
    BlockRotation rotation
  ) {
    Structure.Context context = new Structure.Context(registryManager, chunkGenerator, chunkGenerator.getBiomeSource(), noiseConfig, structureTemplateManager, seed, new ChunkPos(pos), world, (registryEntry) -> true);
    Optional<Structure.StructurePosition> optional = generate(context, structurePool, Optional.empty(), i, pos, Optional.empty(), rotation);
    if (optional.isPresent()) {
      StructurePiecesCollector structurePiecesCollector = optional.get().generate();

      for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
        if (structurePiece instanceof PoolStructurePiece poolStructurePiece) {
          poolStructurePiece.generate(world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, false);
        }
      }
    }
  }

  static final class StructurePoolGenerator {
    private final Registry<StructurePool> registry;
    private final int maxSize;
    private final ChunkGenerator chunkGenerator;
    private final StructureTemplateManager structureTemplateManager;
    private final List<? super PoolStructurePiece> children;
    private final Random random;
    final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

    StructurePoolGenerator(Registry<StructurePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolStructurePiece> children, Random random) {
      this.registry = registry;
      this.maxSize = maxSize;
      this.chunkGenerator = chunkGenerator;
      this.structureTemplateManager = structureTemplateManager;
      this.children = children;
      this.random = random;
    }

    void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, HeightLimitView world, NoiseConfig noiseConfig) {
      StructurePoolElement structurePoolElement = piece.getPoolElement();
      BlockPos blockPos = piece.getPos();
      BlockRotation blockRotation = piece.getRotation();
      StructurePool.Projection projection = structurePoolElement.getProjection();
      boolean bl = projection == StructurePool.Projection.RIGID;
      MutableObject<VoxelShape> mutableObject = new MutableObject<>();
      BlockBox blockBox = piece.getBoundingBox();
      int i = blockBox.getMinY();
      Iterator<StructureTemplate.StructureBlockInfo> var15 = structurePoolElement.getStructureBlockInfos(this.structureTemplateManager, blockPos, blockRotation, this.random).iterator();

      label93:
      while(var15.hasNext()) {
        StructureTemplate.StructureBlockInfo structureBlockInfo = var15.next();
        Direction direction = JigsawBlock.getFacing(structureBlockInfo.state);
        BlockPos blockPos2 = structureBlockInfo.pos;
        BlockPos blockPos3 = blockPos2.offset(direction);
        int j = blockPos2.getY() - i;
        int k = -1;
        Identifier identifier = new Identifier(structureBlockInfo.nbt.getString("pool"));
        Optional<StructurePool> optional = this.registry.getOrEmpty(identifier);
        if (optional.isPresent() && (optional.get().getElementCount() != 0 || Objects.equals(identifier, StructurePools.EMPTY.getValue()))) {
          Identifier identifier2 = optional.get().getTerminatorsId();
          Optional<StructurePool> optional2 = this.registry.getOrEmpty(identifier2);
          if (optional2.isPresent() && (optional2.get().getElementCount() != 0 || Objects.equals(identifier2, StructurePools.EMPTY.getValue()))) {
            boolean bl2 = blockBox.contains(blockPos3);
            MutableObject<VoxelShape> mutableObject2;
            if (bl2) {
              mutableObject2 = mutableObject;
              if (mutableObject.getValue() == null) {
                mutableObject.setValue(VoxelShapes.cuboid(Box.from(blockBox)));
              }
            } else {
              mutableObject2 = pieceShape;
            }

            List<StructurePoolElement> list = Lists.newArrayList();
            if (minY != this.maxSize) {
              list.addAll(optional.get().getElementIndicesInRandomOrder(this.random));
            }

            list.addAll(optional2.get().getElementIndicesInRandomOrder(this.random));

            for (StructurePoolElement structurePoolElement2 : list) {
              if (structurePoolElement2 == EmptyPoolElement.INSTANCE) {
                break;
              }

              Iterator<BlockRotation> var31 = BlockRotation.randomRotationOrder(this.random).iterator();

              label133:
              while (var31.hasNext()) {
                BlockRotation blockRotation2 = var31.next();
                List<StructureTemplate.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2, this.random);
                structurePoolElement2.getBoundingBox(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2);
                Iterator<StructureTemplate.StructureBlockInfo> var36 = list2.iterator();

                StructurePool.Projection projection2;
                boolean bl3;
                int n;
                int o;
                int p;
                BlockBox blockBox4;
                BlockPos blockPos6;
                int r;
                do {
                  StructureTemplate.StructureBlockInfo structureBlockInfo2;
                  do {
                    if (!var36.hasNext()) {
                      continue label133;
                    }

                    structureBlockInfo2 = var36.next();
                  } while (!JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2));

                  BlockPos blockPos4 = structureBlockInfo2.pos;
                  BlockPos blockPos5 = blockPos3.subtract(blockPos4);
                  BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, blockPos5, blockRotation2);
                  int m = blockBox3.getMinY();
                  projection2 = structurePoolElement2.getProjection();
                  bl3 = projection2 == StructurePool.Projection.RIGID;
                  n = blockPos4.getY();
                  o = j - n + JigsawBlock.getFacing(structureBlockInfo.state).getOffsetY();
                  if (bl && bl3) {
                    p = i + o;
                  } else {
                    if (k == -1) {
                      k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                    }

                    p = k - n;
                  }

                  int q = p - m;
                  blockBox4 = blockBox3.offset(0, q, 0);
                  blockPos6 = blockPos5.add(0, q, 0);
                } while (VoxelShapes.matchesAnywhere(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND));

                mutableObject2.setValue(VoxelShapes.combine(mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                r = piece.getGroundLevelDelta();
                int s;
                if (bl3) {
                  s = r - o;
                } else {
                  s = structurePoolElement2.getGroundLevelDelta();
                }

                PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureTemplateManager, structurePoolElement2, blockPos6, s, blockRotation2, blockBox4);
                int t;
                if (bl) {
                  t = i + j;
                } else if (bl3) {
                  t = p + n;
                } else {
                  if (k == -1) {
                    k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                  }

                  t = k + o / 2;
                }

                piece.addJunction(new JigsawJunction(blockPos3.getX(), t - j + r, blockPos3.getZ(), o, projection2));
                poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), t - n + s, blockPos2.getZ(), -o, projection));
                this.children.add(poolStructurePiece);
                if (minY + 1 <= this.maxSize) {
                  this.structurePieces.addLast(new ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, minY + 1));
                }
                continue label93;
              }
            }
          } else {
            LOGGER.warn("Empty or non-existent fallback pool: {}", identifier2);
          }
        } else {
          LOGGER.warn("Empty or non-existent pool: {}", identifier);
        }
      }
    }
  }

  private record ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
  }
}
