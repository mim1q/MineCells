package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.feature.MineCellsPlacedFeatures;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class MineCellsBiomes {
  public static final RegistryKey<Biome> PROMENADE_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("promenade"));
  public static final RegistryKey<Biome> PRISON_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("prison"));
  public static final RegistryKey<Biome> INSUFFERABLE_CRYPT_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("insufferable_crypt"));

  public static void init() {
    Registry.register(BuiltinRegistries.BIOME, PROMENADE_KEY.getValue(), createPromenade());
    Registry.register(BuiltinRegistries.BIOME, PRISON_KEY.getValue(), createPrison());
    Registry.register(BuiltinRegistries.BIOME, INSUFFERABLE_CRYPT_KEY.getValue(), createInsufferableCrypt());
  }

  private static Biome createPrison() {
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
      .generationSettings(new GenerationSettings.Builder().build())
      .build();
  }

  private static Biome createInsufferableCrypt() {
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
      .generationSettings(new GenerationSettings.Builder().build())
      .build();
  }

  private static Biome createPromenade() {
    GenerationSettings.Builder generationSettings = new GenerationSettings.Builder()
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_SHRUB)
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_CHAINS)
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_KING_STATUE)
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_GALLOWS)
      .feature(GenerationStep.Feature.VEGETAL_DECORATION, MineCellsPlacedFeatures.PROMENADE_TREE);
    DefaultBiomeFeatures.addPlainsTallGrass(generationSettings);
    DefaultBiomeFeatures.addJungleGrass(generationSettings);

    SpawnSettings spawnSettings = new SpawnSettings.Builder()
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.LEAPING_ZOMBIE, 12, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.GRENADIER, 3, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.MUTATED_BAT, 1, 1, 1))
      .spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(MineCellsEntities.RUNNER, 1, 1, 1))
      .build();

    return new Biome.Builder()
      .precipitation(Biome.Precipitation.RAIN)
      .downfall(0.5F)
      .temperature(0.8F)
      .effects(new BiomeEffects.Builder()
        .waterColor(0x61D8FF)
        .waterFogColor(0x61D8FF)
        .fogColor(0x67E8FA)
        .skyColor(0x67E8FA)
        .grassColor(0x3C8787)
        .foliageColor(0x33797F)
        .particleConfig(new BiomeParticleConfig(MineCellsParticles.FALLING_LEAF.get(0xC5161F), 0.001F))
        .build())
      .spawnSettings(spawnSettings)
      .generationSettings(generationSettings.build())
      .build();
  }
}
