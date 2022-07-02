package com.github.mim1q.minecells.world.generator;

import com.github.mim1q.minecells.MineCells;
import com.mojang.serialization.Codec;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KingdomBiomeSource extends BiomeSource {

    public static final Codec<KingdomBiomeSource> CODEC = RegistryOps.createRegistryCodec(Registry.BIOME_KEY)
        .xmap(KingdomBiomeSource::new, KingdomBiomeSource::getBiomeRegistry).codec();

    private final Registry<Biome> biomeRegistry;
    private final BiomeImageMap biomeMap;

    protected KingdomBiomeSource(Registry<Biome> biomeRegistry) {
        super(getBiomeEntries(biomeRegistry));
        this.biomeRegistry = biomeRegistry;
        BiomeImageMap biomeImageMap = null;
        try {
            biomeImageMap = new BiomeImageMap(biomeRegistry);
        } catch (IOException e) {
            MineCells.LOGGER.error("Could not load biome image map.");
            e.printStackTrace();
        }
        this.biomeMap = biomeImageMap;
    }

    protected static List<RegistryEntry<Biome>> getBiomeEntries(Registry<Biome> biomeRegistry) {
        List<RegistryEntry<Biome>> entryList = new ArrayList<>();
        biomeRegistry.getKeys().forEach(key -> {
            Optional<RegistryEntry<Biome>> entry = biomeRegistry.getEntry(key);
            entry.ifPresent(entryList::add);
        });
        return entryList;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return this.biomeMap.getBiomeAt(x, z);
    }
}
