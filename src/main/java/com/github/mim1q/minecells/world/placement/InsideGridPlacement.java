package com.github.mim1q.minecells.world.placement;

import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.world.feature.MineCellsStructurePlacementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class InsideGridPlacement extends StructurePlacement {
  private static final Map<CacheKey, List<ChunkPos>> CACHE = new ConcurrentHashMap<>(512);
  private static long cachedSeed = 0;

  public static final Codec<InsideGridPlacement> CODEC = RecordCodecBuilder.create(instance ->
    instance.group(
      Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(InsideGridPlacement::getLocateOffset),
      Codecs.NONNEGATIVE_INT.optionalFieldOf("salt", 0).forGetter(InsideGridPlacement::getSalt),
      ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(InsideGridPlacement::getExclusionZone),
      Codecs.NONNEGATIVE_INT.optionalFieldOf("grid_size", 64).forGetter(p -> p.gridSize),
      Codecs.NONNEGATIVE_INT.fieldOf("min_distance").forGetter(p -> p.minDistance),
      Codecs.NONNEGATIVE_INT.fieldOf("max_distance").forGetter(p -> p.maxDistance),
      Codecs.POSITIVE_INT.optionalFieldOf("max_count", 1).forGetter(p -> p.maxCount),
      Codecs.NONNEGATIVE_INT.optionalFieldOf("separation", 0).forGetter(p -> p.separation)
    ).apply(instance, InsideGridPlacement::new)
  );

  private final int gridSize;
  private final int minDistance;
  private final int maxDistance;
  private final int maxCount;
  private final int separation;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected InsideGridPlacement(
    Vec3i locateOffset,
    int salt,
    Optional<ExclusionZone> exclusionZone,
    int gridSize,
    int minDistance,
    int maxDistance,
    int maxCount,
    int separation
  ) {
    super(locateOffset, FrequencyReductionMethod.DEFAULT, 1F, salt, exclusionZone);
    this.gridSize = gridSize;
    this.minDistance = minDistance;
    this.maxDistance = maxDistance;
    this.maxCount = maxCount;
    this.separation = separation;
  }

  @Override
  protected boolean isStartChunk(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
    var center = MathUtils.getClosestMultiplePosition(new Vec3i(chunkX, 0, chunkZ), gridSize);
    var centerChunkPos = new ChunkPos(center.getX(), center.getZ());
    return getStartChunks(centerChunkPos, calculator.getStructureSeed()).contains(new ChunkPos(chunkX, chunkZ));
  }

  protected List<ChunkPos> getStartChunks(ChunkPos center, long seed) {
    if (CACHE.size() >= 512 || seed != cachedSeed) {
      CACHE.clear();
      cachedSeed = seed;
    }
    var key = new CacheKey(center, seed, getSalt());
    if (CACHE.containsKey(key)) {
      return CACHE.get(key);
    }
    var candidateChunks = generateCandidateChunks(center, minDistance, maxDistance);
    var random = new ChunkRandom(new CheckedRandom(0L));
    random.setCarverSeed(seed + getSalt(), center.x, center.z);
    var result = pickStartChunks(candidateChunks, maxCount, random);
    CACHE.put(key, result);
    return result;
  }

  protected List<ChunkPos> pickStartChunks(List<ChunkPos> chunks, int count, Random random) {
    if (count >= chunks.size()) {
      return chunks;
    }
    var result = new ArrayList<ChunkPos>();
    var selection = new ArrayList<>(chunks);
    for (var i = 0; i < count && selection.size() > 0; i++) {
      var randomIndex = random.nextInt(selection.size());
      result.add(selection.get(randomIndex));
      selection.remove(randomIndex);
      if (separation > 0) {
        removeBySeparation(selection, separation, result.get(i));
      }
    }
    return result;
  }

  protected void removeBySeparation(List<ChunkPos> chunks, int separation, ChunkPos pos) {
    chunks.removeIf(chunk -> pos.getChebyshevDistance(chunk) <= separation);
  }

  protected List<ChunkPos> generateCandidateChunks(ChunkPos center, int minDistance, int maxDistance) {
    var list = new ArrayList<ChunkPos>();
    for (int i = minDistance; i <= maxDistance; i++) {
      list.addAll(generateCandidateChunks(center, i));
    }
    return list;
  }

  protected List<ChunkPos> generateCandidateChunks(ChunkPos center, int distance) {
    if (distance == 0) {
      return List.of(center);
    }
    var set = new HashSet<ChunkPos>();
    var x = center.x;
    var z = center.z;
    for (int i = -distance; i <= distance; i++) {
      set.add(new ChunkPos(x - distance, z + i));
      set.add(new ChunkPos(x + distance, z + i));
      set.add(new ChunkPos(x + i, z - distance));
      set.add(new ChunkPos(x + i, z + distance));
    }
    return List.copyOf(set);
  }

  @Override
  public StructurePlacementType<?> getType() {
    return MineCellsStructurePlacementTypes.INSIDE_GRID;
  }

  public BlockPos getClosestPosition(ChunkPos pos, long worldSeed) {
    var center = MathUtils.getClosestMultiplePosition(new Vec3i(pos.x, 0, pos.z), gridSize);
    var startChunks = getStartChunks(new ChunkPos(center.getX(), center.getZ()), worldSeed);
    if (startChunks.isEmpty()) {
      return null;
    }
    var closest = startChunks.get(0);
    for (var chunk : startChunks) {
      var distance = pos.getChebyshevDistance(chunk);
      if (distance < closest.getChebyshevDistance(pos)) {
        closest = chunk;
      }
    }
    return new BlockPos(closest.x * 16 + getLocateOffset().getX(), getLocateOffset().getY(), closest.z * 16 + getLocateOffset().getZ());
  }

  private record CacheKey(
    ChunkPos center,
    long seed,
    long salt
  ) {
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof CacheKey other) {
        return center.equals(other.center) && seed == other.seed && salt == other.salt;
      }
      return false;
    }
  }
}
