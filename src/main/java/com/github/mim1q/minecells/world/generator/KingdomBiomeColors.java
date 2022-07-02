package com.github.mim1q.minecells.world.generator;

import com.github.mim1q.minecells.registry.BiomeRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.Optional;


public class KingdomBiomeColors {
    public static final int PROMENADE = 0x00FF00FF;

    public static RegistryEntry<Biome> biomeOf(Color color, Registry<Biome> biomeRegistry) {
        RegistryKey<Biome> key = BiomeRegistry.PUTRID_WATERS_KEY;
        if (color.getRGB() == PROMENADE) {
            key = BiomeRegistry.PROMENADE_KEY;
        }
        Optional<RegistryEntry<Biome>> entry = biomeRegistry.getEntry(key);
        return entry.orElse(null);
    }
}
