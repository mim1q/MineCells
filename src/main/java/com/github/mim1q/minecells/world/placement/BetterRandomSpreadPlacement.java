package com.github.mim1q.minecells.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;

import java.util.List;
import java.util.Optional;

public class BetterRandomSpreadPlacement extends RandomSpreadStructurePlacement {
  public static final Codec<BetterRandomSpreadPlacement> CODEC = RecordCodecBuilder.create(
    instance -> instance.group(
      Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(BetterRandomSpreadPlacement::getLocateOffset),
      Codec.floatRange(0.0F, 1.0F).optionalFieldOf("frequency", 1.0F).forGetter(BetterRandomSpreadPlacement::getFrequency),
      Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(BetterRandomSpreadPlacement::getSalt),
      BetterExclusionZone.CODEC.listOf().optionalFieldOf("exclusion_zones", List.of()).forGetter(BetterRandomSpreadPlacement::getExclusionZones),
      // Using the minecells prefix as a workaround for Sparse Structures...
      Codecs.NONNEGATIVE_INT.fieldOf("minecells_spacing").forGetter(BetterRandomSpreadPlacement::getSpacing),
      Codecs.NONNEGATIVE_INT.fieldOf("minecells_separation").forGetter(BetterRandomSpreadPlacement::getSeparation),
      SpreadType.CODEC.optionalFieldOf("spread_type", SpreadType.LINEAR).forGetter(BetterRandomSpreadPlacement::getSpreadType)
    ).apply(instance, BetterRandomSpreadPlacement::new)
  );

  private final List<BetterExclusionZone> exclusionZones;
  private final float actualFrequency;

  private BetterRandomSpreadPlacement(
    Vec3i locateOffset,
    float frequency,
    int salt,
    List<BetterExclusionZone> exclusionZones,
    int spacing,
    int separation,
    SpreadType spreadType
  ) {
    super(locateOffset, FrequencyReductionMethod.DEFAULT, 1F, salt, Optional.empty(), spacing, separation, spreadType);
    this.exclusionZones = exclusionZones;
    this.actualFrequency = frequency;
  }

  private List<BetterExclusionZone> getExclusionZones() {
    return exclusionZones;
  }

  @Override
  public boolean shouldGenerate(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
    var result = super.shouldGenerate(calculator, chunkX, chunkZ);
    ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
    chunkRandom.setCarverSeed(calculator.getStructureSeed() + getSalt(), chunkX, chunkZ);
    return result
      && chunkRandom.nextFloat() <= actualFrequency
      && exclusionZones.stream().noneMatch(zone -> zone.shouldExclude(calculator, chunkX, chunkZ));
  }

  private static class BetterExclusionZone {
    public static final Codec<BetterExclusionZone> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
        RegistryElementCodec.of(RegistryKeys.STRUCTURE_SET, StructureSet.CODEC, false).fieldOf("other_set").forGetter(it -> it.otherSet),
        Codec.intRange(1, 32).fieldOf("chunk_count").forGetter(it -> it.chunkCount)
      ).apply(instance, BetterExclusionZone::new)
    );

    private final RegistryEntry<StructureSet> otherSet;
    private final int chunkCount;

    public BetterExclusionZone(RegistryEntry<StructureSet> registryEntry, int i) {
      this.otherSet = registryEntry;
      this.chunkCount = i;
    }

    boolean shouldExclude(StructurePlacementCalculator calculator, int x, int z) {
      return calculator.canGenerate(this.otherSet, x, z, this.chunkCount);
    }
  }
}
