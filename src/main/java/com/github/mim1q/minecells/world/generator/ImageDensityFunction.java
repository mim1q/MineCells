package com.github.mim1q.minecells.world.generator;

import com.github.mim1q.minecells.util.MapImageReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageDensityFunction implements DensityFunction {
    private final String filename;
    private final double scale;
    private final double from;
    private final double to;
    private final DensityImageMap densityMap;

    public static final MapCodec<ImageDensityFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
        .group(
            Codec.STRING.fieldOf("filename").forGetter(ImageDensityFunction::filename),
            Codec.DOUBLE.fieldOf("scale").forGetter(ImageDensityFunction::scale),
            Codec.DOUBLE.fieldOf("from").forGetter(ImageDensityFunction::from),
            Codec.DOUBLE.fieldOf("to").forGetter(ImageDensityFunction::to)
            )
        .apply(instance, ImageDensityFunction::new)
    );

    public static final CodecHolder<ImageDensityFunction> CODEC = CodecHolder.of(MAP_CODEC);

    protected ImageDensityFunction(String filename, double scale, double from, double to) {
        this.filename = filename;
        this.densityMap = new DensityImageMap(filename, from, to);
        this.scale = scale;
        this.from = from;
        this.to = to;
    }

    @Override
    public double sample(NoisePos pos) {
        return this.densityMap.getDataAt((int)(pos.blockX() * scale), (int)(pos.blockZ() * scale));
    }

    @Override
    public void method_40470(double[] ds, class_6911 arg) {
        arg.method_40478(ds, this);
    }

    @Override
    public DensityFunction apply(DensityFunctionVisitor visitor) {
        return visitor.apply(this);
    }

    @Override
    public double minValue() {
        return Math.min(this.from, this.to);
    }

    @Override
    public double maxValue() {
        return Math.max(this.to, this.from);
    }

    public String filename() {
        return this.filename;
    }

    public double from() {
        return this.from;
    }

    public double to() {
        return this.to;
    }

    public double scale() {
        return this.scale;
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodec() {
        return CODEC;
    }

    public static class DensityImageMap extends MapImageReader<Double> {

        private final double from;
        private final double to;

        public DensityImageMap(String name, double from, double to) {
            this.from = from;
            this.to = to;
            BufferedImage image = this.loadImage(this.getPath(name));
            this.populateArray(image);
        }

        @Override
        protected Double dataOf(Color color) {
            return MathHelper.clampedLerp(this.from, this.to, color.getBlue() / 255.0F);
        }
    }
}
