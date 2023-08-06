package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class MineCellsBlockTags {
  public static final TagKey<Block> CONJUNCTIVIUS_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("conjunctivius_breakable"));
  public static final TagKey<Block> ELEVATOR_CHAINS = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("elevator_chains"));
}
