package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.feature.MineCellsPlacedFeatures;
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
  public static final RegistryKey<Biome> PRISON_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("prison"));

  public static void init() {
    Registry.register(BuiltinRegistries.BIOME, PROMENADE_KEY.getValue(), createPromenade());
    Registry.register(BuiltinRegistries.BIOME, PRISON_KEY.getValue(), createPrison());
  }

  private static Biome createPrison() {
    GenerationSettings.Builder generationSettings = new GenerationSettings.Builder()
      .feature(GenerationStep.Feature.UNDERGROUND_DECORATION, MineCellsPlacedFeatures.WILTED_LEAVES);

    return new Biome.Builder()
      .precipitation(Biome.Precipitation.NONE)
      .downfall(0.5F)
      .temperature(0.5F)
      .effects(new BiomeEffects.Builder()
        .waterColor(0x3f76e4)
        .waterFogColor(0x050533)
        .fogColor(0xc0d8ff)
        .skyColor(0x80a0ff)
        .build())
        .spawnSettings(new SpawnSettings.Builder().build())
      .generationSettings(generationSettings.build())
      .build();
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
}
