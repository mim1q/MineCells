package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MineCellsItemGroups {

  public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(MineCells.createId("eggs"))
    .icon(() -> new ItemStack(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG))
    .appendItems(stacks -> {
      stacks.add(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.SHOCKER_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.GRENADIER_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.DISGUSTING_WORM_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.INQUISITOR_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.KAMIKAZE_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.PROTECTOR_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.UNDEAD_ARCHER_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.SHIELDBEARER_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.MUTATED_BAT_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.SEWERS_TENTACLE_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.RANCID_RAT_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.RUNNER_SPAWN_EGG.getDefaultStack());
      stacks.add(MineCellsEntities.SCORPION_SPAWN_EGG.getDefaultStack());
    })
    .build();

  public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(MineCells.createId("weapons"))
    .icon(() -> new ItemStack(MineCellsItems.ASSASSINS_DAGGER))
    .appendItems(stacks -> {
      stacks.add(new ItemStack(MineCellsItems.ASSASSINS_DAGGER));
    })
    .build();

  public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(MineCells.createId("blocks_and_items"))
    .icon(() -> new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER))
    .appendItems(stacks -> {
      stacks.add(MineCellsBlocks.PUTRID_LOG.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.PUTRID_WOOD.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.STRIPPED_PUTRID_LOG.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.STRIPPED_PUTRID_WOOD.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.PUTRID_PLANKS.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.PUTRID_STAIRS.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.PUTRID_SLAB.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.WILTED_LEAVES.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.HANGING_WILTED_LEAVES.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.WALL_WILTED_LEAVES.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.ORANGE_WILTED_LEAVES.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.HANGING_ORANGE_WILTED_LEAVES.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.CRATE.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.SMALL_CRATE.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.ELEVATOR_ASSEMBLER.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.HARDSTONE.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.CHAIN_PILE_BLOCK.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.CHAIN_PILE.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.BIG_CHAIN.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.CAGE.asItem().getDefaultStack());
      stacks.add(MineCellsBlocks.BROKEN_CAGE.asItem().getDefaultStack());
      stacks.add(MineCellsItems.BIOME_BANNER.getOf(BiomeBannerBlock.BannerPattern.KING_CREST));
      stacks.add(MineCellsItems.BIOME_BANNER.getOf(BiomeBannerBlock.BannerPattern.PROMENADE));
      stacks.add(MineCellsItems.SEWAGE_BUCKET.getDefaultStack());
      stacks.add(MineCellsItems.ANCIENT_SEWAGE_BUCKET.getDefaultStack());
      stacks.add(MineCellsItems.CHARGED_INTERDIMENSIONAL_RUNE.getDefaultStack());
      stacks.add(MineCellsItems.ELEVATOR_MECHANISM.getDefaultStack());
    })
    .build();

  public static void init() { }
}
