package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.EntryList;
import com.github.mim1q.minecells.item.SpawnerRuneItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class MineCellsItemGroups {
  public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(MineCells.createId("eggs"))
    .icon(() -> new ItemStack(MineCellsEntities.SHOCKER_SPAWN_EGG))
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
    .icon(() -> new ItemStack(MineCellsItems.BLOOD_SWORD))
    .appendItems(stacks -> stacks.addAll(List.of(
      MineCellsItems.ASSASSINS_DAGGER.getDefaultStack(),
      MineCellsItems.BLOOD_SWORD.getDefaultStack(),
      MineCellsItems.CURSED_SWORD.getDefaultStack(),
      MineCellsItems.HATTORIS_KATANA.getDefaultStack(),
      MineCellsItems.TENTACLE.getDefaultStack()
    ))).build();

  public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(MineCells.createId("blocks_and_items"))
    .icon(() -> new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER))
    .appendItems(stacks -> stacks.addAll(List.of(
      MineCellsBlocks.PRISON_STONE.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_STONE_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_STONE_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_STONE_WALL.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_COBBLESTONE.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_COBBLESTONE_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_COBBLESTONE_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_COBBLESTONE_WALL.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_BRICKS.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_BRICK_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_BRICK_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_BRICK_WALL.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_PRISON_BRICKS.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_PRISON_BRICK_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_PRISON_BRICK_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_PRISON_BRICK_WALL.asItem().getDefaultStack(),
      MineCellsBlocks.WILTED_GRASS_BLOCK.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_LOG.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_WOOD.asItem().getDefaultStack(),
      MineCellsBlocks.STRIPPED_PUTRID_LOG.asItem().getDefaultStack(),
      MineCellsBlocks.STRIPPED_PUTRID_WOOD.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_PLANKS.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_STAIRS.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_SLAB.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_FENCE.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_FENCE_GATE.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_DOOR.asItem().getDefaultStack(),
      MineCellsBlocks.PUTRID_TRAPDOOR.asItem().getDefaultStack(),
      MineCellsBlocks.WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.WILTED_HANGING_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.WILTED_WALL_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.ORANGE_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.ORANGE_WILTED_HANGING_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.ORANGE_WILTED_WALL_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.RED_WILTED_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.RED_WILTED_HANGING_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.RED_WILTED_WALL_LEAVES.asItem().getDefaultStack(),
      MineCellsBlocks.CRATE.asItem().getDefaultStack(),
      MineCellsBlocks.SMALL_CRATE.asItem().getDefaultStack(),
      MineCellsBlocks.BRITTLE_BARREL.asItem().getDefaultStack(),
      MineCellsBlocks.SKELETON.asItem().getDefaultStack(),
      MineCellsBlocks.ROTTING_CORPSE.asItem().getDefaultStack(),
      MineCellsBlocks.CORPSE.asItem().getDefaultStack(),
      MineCellsBlocks.ELEVATOR_ASSEMBLER.asItem().getDefaultStack(),
      MineCellsBlocks.CELL_FORGE.asItem().getDefaultStack(),
      MineCellsBlocks.HARDSTONE.asItem().getDefaultStack(),
      MineCellsBlocks.CHAIN_PILE_BLOCK.asItem().getDefaultStack(),
      MineCellsBlocks.CHAIN_PILE.asItem().getDefaultStack(),
      MineCellsBlocks.BIG_CHAIN.asItem().getDefaultStack(),
      MineCellsBlocks.CAGE.asItem().getDefaultStack(),
      MineCellsBlocks.BROKEN_CAGE.asItem().getDefaultStack(),
      MineCellsBlocks.SPIKES.asItem().getDefaultStack(),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.TORN_KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.PROMENADE),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.INSUFFERABLE_CRYPT),
      MineCellsBlocks.ALCHEMY_EQUIPMENT_0.asItem().getDefaultStack(),
      MineCellsBlocks.ALCHEMY_EQUIPMENT_1.asItem().getDefaultStack(),
      MineCellsBlocks.ALCHEMY_EQUIPMENT_2.asItem().getDefaultStack(),
      MineCellsBlocks.PRISON_TORCH.asItem().getDefaultStack(),
      MineCellsItems.SEWAGE_BUCKET.getDefaultStack(),
      MineCellsItems.ANCIENT_SEWAGE_BUCKET.getDefaultStack(),
      MineCellsItems.ELEVATOR_MECHANISM.getDefaultStack(),
      MineCellsItems.GUTS.getDefaultStack(),
      MineCellsItems.MONSTERS_EYE.getDefaultStack(),
      MineCellsItems.HEALTH_FLASK.getDefaultStack(),
      MineCellsItems.BLANK_RUNE.getDefaultStack(),
      MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE.getDefaultStack()
    ))).build();

  public static ItemGroup MINECELLS_DEVELOPMENT = null;

  public static void init() {
    if (MineCells.COMMON_CONFIG.items.enableDevelopmentTab) {
      MINECELLS_DEVELOPMENT = FabricItemGroupBuilder.create(MineCells.createId("development"))
        .icon(() -> new ItemStack(Blocks.BARRIER))
        .appendItems(stacks -> stacks.addAll(List.of(
          Items.DEBUG_STICK.getDefaultStack(),
          Items.WOODEN_AXE.getDefaultStack(),
          Items.JIGSAW.getDefaultStack(),
          Items.STRUCTURE_BLOCK.getDefaultStack(),
          Items.STRUCTURE_VOID.getDefaultStack(),
          Items.BARRIER.getDefaultStack(),
          SpawnerRuneItem.withData("Test", EntryList.PRISON, 100, 1, 10, 10.0F, 3.0F),
          SpawnerRuneItem.withData("Prison", EntryList.PRISON, 30 * 20, 1, 2, 6.0F, 10.0F),
          SpawnerRuneItem.withData("Promenade of The Condemnded", EntryList.PROMENADE_OF_THE_CONDEMNED, 60 * 20, 3, 5, 5.0F, 10.0F),
          SpawnerRuneItem.withData("Protector", EntryList.PROTECTOR, 120 * 20, 1, 1, 0.0F, 32.0F),
          SpawnerRuneItem.withData("Shocker", EntryList.SHOCKER, 120 * 20, 1, 1, 0.0F, 32.0F)
        ))).build();
    }
  }
}
