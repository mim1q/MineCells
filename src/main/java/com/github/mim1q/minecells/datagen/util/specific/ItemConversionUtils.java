package com.github.mim1q.minecells.datagen.util.specific;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.DyeColor;

import java.util.Map;

import static java.util.Map.entry;

public class ItemConversionUtils {
  public static final Map<DyeColor, Block> DYE_TO_WOOL = Map.ofEntries(
    entry(DyeColor.WHITE, Blocks.WHITE_WOOL),
    entry(DyeColor.ORANGE, Blocks.ORANGE_WOOL),
    entry(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL),
    entry(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL),
    entry(DyeColor.YELLOW, Blocks.YELLOW_WOOL),
    entry(DyeColor.LIME, Blocks.LIME_WOOL),
    entry(DyeColor.PINK, Blocks.PINK_WOOL),
    entry(DyeColor.GRAY, Blocks.GRAY_WOOL),
    entry(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL),
    entry(DyeColor.CYAN, Blocks.CYAN_WOOL),
    entry(DyeColor.PURPLE, Blocks.PURPLE_WOOL),
    entry(DyeColor.BLUE, Blocks.BLUE_WOOL),
    entry(DyeColor.BROWN, Blocks.BROWN_WOOL),
    entry(DyeColor.GREEN, Blocks.GREEN_WOOL),
    entry(DyeColor.RED, Blocks.RED_WOOL),
    entry(DyeColor.BLACK, Blocks.BLACK_WOOL)
  );
}
