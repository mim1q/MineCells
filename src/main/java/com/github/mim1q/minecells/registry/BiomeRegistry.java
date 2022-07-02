package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.generator.KingdomBiomeSource;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class BiomeRegistry {
    public static final RegistryKey<Biome> PROMENADE_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("promenade"));
    public static final RegistryKey<Biome> PUTRID_WATERS_KEY = RegistryKey.of(Registry.BIOME_KEY, MineCells.createId("putrid_waters"));

    public static void register() {
        Registry.register(BuiltinRegistries.BIOME, PROMENADE_KEY.getValue(), createPromenade());
        Registry.register(BuiltinRegistries.BIOME, PUTRID_WATERS_KEY.getValue(), createPutridWaters());

        Registry.register(
            Registry.BIOME_SOURCE,
            MineCells.createId("kingdom_biome_source"),
            KingdomBiomeSource.CODEC
        );
    }

    private static Biome createPromenade() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(spawnSettings);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

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
        DefaultBiomeFeatures.addFarmAnimals(spawnSettings);

        GenerationSettings.Builder generationSettings = new GenerationSettings.Builder();

        return new Biome.Builder()
            .precipitation(Biome.Precipitation.RAIN)
            .downfall(0.5F)
            .temperature(0.8F)
            .effects(new BiomeEffects.Builder()
                .waterColor(0x6100FF)
                .waterFogColor(0x61D8FF)
                .fogColor(0x4F9FFF)
                .skyColor(0x61D8FF)
                .grassColor(0x3F0058)
                .build())
            .spawnSettings(spawnSettings.build())
            .generationSettings(generationSettings.build())
            .build();
    }
}
