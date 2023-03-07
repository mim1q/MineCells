package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.world.feature.placementmodifier.ExcludeChunkMultiplesPlacementModifier;
import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class MineCellsPlacementModifiers {
  public static final PlacementModifierType<ExcludeChunkMultiplesPlacementModifier> EXCLUDE_CHUNK_MULTIPLES = register(
    "minecells:exclude_chunk_multiples",
    ExcludeChunkMultiplesPlacementModifier.CODEC
  );

  public static void init() { }

  private static <P extends PlacementModifier> PlacementModifierType<P> register(String id, Codec<P> codec) {
    return Registry.register(Registry.PLACEMENT_MODIFIER_TYPE, id, () -> codec);
  }
}
