package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.MineCells;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class MapImageReader<T> {
    protected final static String PATH = "data/minecells/dimension/map/";

    private final List<T> array = new ArrayList<>();
    private int width;
    private int height;

    protected BufferedImage loadImage(String path) {
        BufferedImage mapImage = null;
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        try {
            if (stream != null) {
                mapImage = ImageIO.read(stream);
            } else {
                throw new IOException();
            }
            stream.close();
        } catch (IOException e) {
            MineCells.LOGGER.error("Could not load resource: " + path);
            e.printStackTrace();
        }
        assert mapImage != null;
        this.width = mapImage.getWidth();
        this.height = mapImage.getHeight();
        return mapImage;
    }

    protected void populateArray(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                this.array.add(this.dataOf(color));
            }
        }
    }

    protected abstract T dataOf(Color color);

    protected String getPath(String filename) {
        return PATH + filename + ".png";
    }

    public T getDataAt(int x, int z) {
        x = Math.floorMod(x, this.width);
        z = Math.floorMod(z, this.height);
        return this.array.get(x * this.width + z);
    }
}
