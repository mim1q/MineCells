package com.github.mim1q.minecells.world.generator;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BiomeImageMap {
    protected final static String IMAGE_URL = "data/minecells/dimension/map/biomes.png";

    private final List<RegistryEntry<Biome>> biomeArray = new ArrayList<>();
    private final int width;
    private final int height;

    public BiomeImageMap(Registry<Biome> biomeRegistry) throws IOException {
        BufferedImage mapImage;
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(IMAGE_URL);
        if (stream != null) {
            mapImage = ImageIO.read(stream);
        } else {
            throw new IOException();
        }
        this.width = mapImage.getWidth();
        this.height = mapImage.getHeight();
        this.populateBiomeArray(mapImage, biomeRegistry);
        stream.close();
    }

    private void populateBiomeArray(BufferedImage image, Registry<Biome> biomeRegistry) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                this.biomeArray.add(KingdomBiomeColors.biomeOf(color, biomeRegistry));
            }
        }
    }

    public RegistryEntry<Biome> getBiomeAt(int x, int z) {
        x = Math.floorMod(x, this.width);
        z = Math.floorMod(z, this.height);
        return this.biomeArray.get(x * this.width + z);
    }
}
