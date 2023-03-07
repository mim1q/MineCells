package com.github.mim1q.minecells.world.feature.placementmodifier;

import com.github.mim1q.minecells.world.feature.MineCellsPlacementModifiers;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

import java.util.stream.Stream;

public class ExcludeChunkMultiplesPlacementModifier extends PlacementModifier {
  public static final Codec<ExcludeChunkMultiplesPlacementModifier> CODEC = Codec.INT
    .fieldOf("multiple").xmap(ExcludeChunkMultiplesPlacementModifier::new, (m) -> m.multiple).codec();

  private final int multiple;
  private ExcludeChunkMultiplesPlacementModifier(int multiple) {
    this.multiple = multiple;
  }

  public static ExcludeChunkMultiplesPlacementModifier of(int multiple) {
    return new ExcludeChunkMultiplesPlacementModifier(multiple);
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
