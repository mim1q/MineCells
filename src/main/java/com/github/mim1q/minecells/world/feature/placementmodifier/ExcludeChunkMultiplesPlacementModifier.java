package com.github.mim1q.minecells.world.feature.placementmodifier;

import com.github.mim1q.minecells.world.feature.MineCellsPlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

import java.util.stream.Stream;

public class ExcludeChunkMultiplesPlacementModifier extends PlacementModifier {
  public static final Codec<ExcludeChunkMultiplesPlacementModifier> CODEC = RecordCodecBuilder
    .<ExcludeChunkMultiplesPlacementModifier>mapCodec(
      instance -> instance.group(
        Codec.INT.fieldOf("multiple").forGetter(it -> it.multiple),
        Codec.INT.optionalFieldOf("x_offset", 0).forGetter(it -> it.xOffset),
        Codec.INT.optionalFieldOf("z_offset", 0).forGetter(it -> it.zOffset)
      ).apply(instance, ExcludeChunkMultiplesPlacementModifier::new)
    ).codec();

  private final int multiple;
  private final int xOffset;
  private final int zOffset;

  private ExcludeChunkMultiplesPlacementModifier(int multiple, int xOffset, int zOffset) {
    this.multiple = multiple;
    this.xOffset = xOffset;
    this.zOffset = zOffset;
  }

  @Override
  public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
    ChunkPos chunkPos = new ChunkPos(pos);
    return (chunkPos.x % multiple == 0 && chunkPos.z % multiple == 0) ? Stream.empty() : Stream.of(pos);
  }

  @Override
  public PlacementModifierType<?> getType() {
    return MineCellsPlacementModifiers.EXCLUDE_CHUNK_MULTIPLES;
  }
}
