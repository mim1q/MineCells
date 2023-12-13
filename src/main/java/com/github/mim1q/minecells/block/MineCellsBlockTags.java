package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class MineCellsBlockTags {
  public static final TagKey<Block> CONJUNCTIVIUS_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("conjunctivius_breakable"));
  public static final TagKey<Block> ELEVATOR_CHAINS = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("elevator_chains"));
  public static final TagKey<Block> TREE_ROOT_REPLACEABLE = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("tree_root_replaceable"));
  public static final TagKey<Block> RETURN_STONE_TARGETS = TagKey.of(RegistryKeys.BLOCK, MineCells.createId("return_stone_targets"));
}
