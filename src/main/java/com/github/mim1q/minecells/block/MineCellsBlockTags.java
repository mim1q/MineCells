package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockTags {
  public static final TagKey<Block> CONJUNCTIVIUS_BREAKABLE = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("conjunctivius_breakable"));
  public static final TagKey<Block> ELEVATOR_CHAINS = TagKey.of(Registry.BLOCK_KEY, MineCells.createId("elevator_chains"));
}
