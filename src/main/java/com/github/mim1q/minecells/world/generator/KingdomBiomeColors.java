package com.github.mim1q.minecells.world.generator;

import com.github.mim1q.minecells.registry.MineCellsBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.awt.*;
import java.util.Optional;


public class KingdomBiomeColors {
  public static final int PROMENADE = 0x00FF00;

  public static RegistryEntry<Biome> biomeOf(Color color, Registry<Biome> biomeRegistry) {
    RegistryKey<Biome> key;
    if (colorCode(color) == PROMENADE) {
      key = MineCellsBiomes.PROMENADE_KEY;
    } else {
      key = MineCellsBiomes.PUTRID_WATERS_KEY;
    }
    Optional<RegistryEntry<Biome>> entry = biomeRegistry.getEntry(key);
    return entry.orElse(null);
  }

  protected static int colorCode(Color color) {
    int rgb = color.getRed();
    rgb = (rgb << 8) + color.getGreen();
    rgb = (rgb << 8) + color.getBlue();
    return rgb;
  }
}
