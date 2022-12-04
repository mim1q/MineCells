package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockTags {
  public static final TagKey<Block> CONJUNCTIVIUS_UNBREAKABLE = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("conjunctivius_unbreakable"));
  public static final TagKey<Block> VEGETATION_REPLACEABLE = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("vegetation_replaceable"));
  public static final TagKey<Block> CEILING_DECORATION_SUPPORT = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("ceiling_decoration_support"));
}
