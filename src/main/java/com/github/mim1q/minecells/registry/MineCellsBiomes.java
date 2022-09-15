package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.feature.MineCellsPlacedFeatures;
import com.github.mim1q.minecells.world.generator.KingdomBiomeSource;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class MineCellsBiomes {
  public static final RegistryKey<Biome> PROMENADE_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("promenade"));
  public static final RegistryKey<Biome> PUTRID_WATERS_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("putrid_waters"));

  public static void init() {
    Registry.register(BuiltinRegistries.BIOME, PROMENADE_KEY.getValue(), createPromenade());
    Registry.register(BuiltinRegistries.BIOME, PUTRID_WATERS_KEY.getValue(), createPutridWaters());

    Registry.register(
      Registry.BIOME_SOURCE,
      MineCells.createId("kingdom_biome_source"),
      KingdomBiomeSource.CODEC
    );
  }

  private static Biome createPromenade() {
    SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder()
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.LEAPING_ZOMBIE, 150, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.GRENADIER, 75, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.MUTATED_BAT, 100, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.RUNNER, 100, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.PROTECTOR, 50, 1, 1));

    GenerationSettings.Builder generationSettings = new GenerationSettings.Builder()
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_TREE);
    DefaultBiomeFeatures.addJungleGrass(generationSettings);
    DefaultBiomeFeatures.addGiantTaigaGrass(generationSettings);

    return new Biome.Builder()
      .precipitation(Biome.Precipitation.RAIN)
      .downfall(0.5F)
      .temperature(0.8F)
      .effects(new BiomeEffects.Builder()
        .waterColor(0x61D8FF)
        .waterFogColor(0x61D8FF)
        .fogColor(0x4F9FFF)
        .skyColor(0x61D8FF)
        .grassColor(0x3FC558)
        .build())
      .spawnSettings(spawnSettings.build())
      .generationSettings(generationSettings.build())
      .build();
  }

  private static Biome createPutridWaters() {
    SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();

    GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

    return new Biome.Builder()
      .precipitation(Biome.Precipitation.RAIN)
      .downfall(0.5F)
      .temperature(0.8F)
      .effects(new BiomeEffects.Builder()
        .waterColor(0x60B6FF)
        .waterFogColor(0x61D8FF)
        .fogColor(0x4F9FFF)
        .skyColor(0x61D8FF)
        .grassColor(0x4AB96D)
        .build())
      .spawnSettings(spawnSettings.build())
      .generationSettings(generationSettings.build())
      .build();
  }
}
