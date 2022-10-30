package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MineCellsItemGroups {

  public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(MineCells.createId("eggs"))
    .icon(() -> new ItemStack(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG))
    .appendItems(stacks -> stacks.addAll(List.of(
      MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.SHOCKER_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.GRENADIER_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.DISGUSTING_WORM_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.INQUISITOR_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.KAMIKAZE_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.PROTECTOR_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.UNDEAD_ARCHER_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.SHIELDBEARER_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.MUTATED_BAT_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.SEWERS_TENTACLE_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.RANCID_RAT_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.RUNNER_SPAWN_EGG.getDefaultStack(),
      MineCellsEntities.SCORPION_SPAWN_EGG.getDefaultStack()
    ))).build();

  public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(MineCells.createId("weapons"))
    .icon(() -> new ItemStack(MineCellsItems.ASSASSINS_DAGGER))
    .appendItems(stacks -> stacks.addAll(List.of(
      MineCellsItems.ASSASSINS_DAGGER.getDefaultStack(),
      MineCellsItems.BLOOD_SWORD.getDefaultStack(),
      MineCellsItems.CURSED_SWORD.getDefaultStack(),
      MineCellsItems.TENTACLE.getDefaultStack()
    ))).build();

  public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(MineCells.createId("blocks_and_items"))
    .icon(() -> new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER))
    .appendItems(stacks -> stacks.addAll(List.of(
      MineCellsBlocks.PUTRID_LOG.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_WOOD.asItem().getDefaultStack(),
      MineCellsBlocks.STRIPPED_PUTRID_LOG.asItem().getDefaultStack(),
      MineCellsBlocks.STRIPPED_PUTRID_WOOD.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_PLANKS.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.HANGING_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.WALL_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.ORANGE_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.HANGING_ORANGE_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.ORANGE_WALL_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.CRATE.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_CRATE.asItem().getDefaultStack(),
      MineCellsBlocks.BRITTLE_BARREL.asItem().getDefaultStack(),
      MineCellsBlocks.ELEVATOR_ASSEMBLER.asItem().getDefaultStack(),
      MineCellsBlocks.HARDSTONE.asItem().getDefaultStack(),
      MineCellsBlocks.CHAIN_PILE_BLOCK.asItem().getDefaultStack(),
      MineCellsBlocks.CHAIN_PILE.asItem().getDefaultStack(),
      MineCellsBlocks.BIG_CHAIN.asItem().getDefaultStack(),
      MineCellsBlocks.CAGE.asItem().getDefaultStack(),
      MineCellsBlocks.BROKEN_CAGE.asItem().getDefaultStack(),
      MineCellsBlocks.SPIKES.asItem().getDefaultStack(),
      MineCellsItems.BIOME_BANNER.getOf(BiomeBannerBlock.BannerPattern.KING_CREST),
      MineCellsItems.BIOME_BANNER.getOf(BiomeBannerBlock.BannerPattern.PROMENADE),
      MineCellsItems.SEWAGE_BUCKET.getDefaultStack(),
      MineCellsItems.ANCIENT_SEWAGE_BUCKET.getDefaultStack(),
      MineCellsItems.ELEVATOR_MECHANISM.getDefaultStack(),
      MineCellsItems.HEALTH_FLASK.getDefaultStack()
    ))).build();

  public static void init() { }
}
