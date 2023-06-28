package com.github.mim1q.minecells.world.placement;

import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.world.feature.MineCellsStructurePlacementTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class InsideGridPlacement extends StructurePlacement {
  private static final Map<CacheKey, List<ChunkPos>> CACHE = new ConcurrentHashMap<>();
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
  protected boolean isStartChunk(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long seed, int chunkX, int chunkZ) {
    var center = MathUtils.getClosestMultiplePosition(new Vec3i(chunkX, 0, chunkZ), gridSize);
    var centerChunkPos = new ChunkPos(center.getX(), center.getZ());
    var random = new ChunkRandom(Random.create());
    return getStartChunks(centerChunkPos, minDistance, maxDistance, maxCount, random, seed, getSalt()).contains(new ChunkPos(chunkX, chunkZ));
  }

  protected List<ChunkPos> getStartChunks(ChunkPos center, int minDistance, int maxDistance, int count, Random random, long seed, long salt) {
    System.out.println(CACHE.size());
    if (CACHE.size() >= 512 || seed != cachedSeed) {
      CACHE.clear();
      cachedSeed = seed;
    }
    var key = new CacheKey(center, seed, salt);
    if (CACHE.containsKey(key)) {
      return CACHE.get(key);
    }
    var candidateChunks = generateCandidateChunks(center, minDistance, maxDistance);
    var result = pickStartChunks(candidateChunks, count, random);
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
