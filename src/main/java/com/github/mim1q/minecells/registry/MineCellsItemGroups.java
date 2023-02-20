package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BiomeBannerBlock;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.EntryList;
import com.github.mim1q.minecells.item.SpawnerRuneItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class MineCellsItemGroups {
  private static ItemStack Stack(ItemConvertible item) {
    return new ItemStack(item);
  }

  public static final ItemGroup MINECELLS = FabricItemGroupBuilder.create(MineCells.createId("minecells"))
    .icon(() -> Stack(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE))
    .appendItems(stacks -> stacks.addAll(List.of(
      Stack(MineCellsBlocks.PRISON_STONE),
      Stack(MineCellsBlocks.PRISON_STONE_SLAB),
      Stack(MineCellsBlocks.PRISON_STONE_STAIRS),
      Stack(MineCellsBlocks.PRISON_STONE_WALL),
      Stack(MineCellsBlocks.PRISON_COBBLESTONE),
      Stack(MineCellsBlocks.PRISON_COBBLESTONE_SLAB),
      Stack(MineCellsBlocks.PRISON_COBBLESTONE_STAIRS),
      Stack(MineCellsBlocks.PRISON_COBBLESTONE_WALL),
      Stack(MineCellsBlocks.PRISON_BRICKS),
      Stack(MineCellsBlocks.PRISON_BRICK_SLAB),
      Stack(MineCellsBlocks.PRISON_BRICK_STAIRS),
      Stack(MineCellsBlocks.PRISON_BRICK_WALL),
      Stack(MineCellsBlocks.SMALL_PRISON_BRICKS),
      Stack(MineCellsBlocks.SMALL_PRISON_BRICK_SLAB),
      Stack(MineCellsBlocks.SMALL_PRISON_BRICK_STAIRS),
      Stack(MineCellsBlocks.SMALL_PRISON_BRICK_WALL),
      Stack(MineCellsBlocks.WILTED_GRASS_BLOCK),
      Stack(MineCellsBlocks.PUTRID_LOG),
      Stack(MineCellsBlocks.PUTRID_WOOD),
      Stack(MineCellsBlocks.STRIPPED_PUTRID_LOG),
      Stack(MineCellsBlocks.STRIPPED_PUTRID_WOOD),
      Stack(MineCellsBlocks.PUTRID_PLANKS),
      Stack(MineCellsBlocks.PUTRID_STAIRS),
      Stack(MineCellsBlocks.PUTRID_SLAB),
      Stack(MineCellsBlocks.PUTRID_FENCE),
      Stack(MineCellsBlocks.PUTRID_FENCE_GATE),
      Stack(MineCellsBlocks.PUTRID_DOOR),
      Stack(MineCellsBlocks.PUTRID_TRAPDOOR),
      Stack(MineCellsBlocks.PUTRID_BOARD_BLOCK),
      Stack(MineCellsBlocks.PUTRID_BOARD_STAIRS),
      Stack(MineCellsBlocks.PUTRID_BOARD_SLAB),
      Stack(MineCellsBlocks.PUTRID_BOARDS),
      Stack(MineCellsBlocks.WILTED_LEAVES),
      Stack(MineCellsBlocks.WILTED_HANGING_LEAVES),
      Stack(MineCellsBlocks.WILTED_WALL_LEAVES),
      Stack(MineCellsBlocks.ORANGE_WILTED_LEAVES),
      Stack(MineCellsBlocks.ORANGE_WILTED_HANGING_LEAVES),
      Stack(MineCellsBlocks.ORANGE_WILTED_WALL_LEAVES),
      Stack(MineCellsBlocks.RED_WILTED_LEAVES),
      Stack(MineCellsBlocks.RED_WILTED_HANGING_LEAVES),
      Stack(MineCellsBlocks.RED_WILTED_WALL_LEAVES),
      Stack(MineCellsBlocks.CRATE),
      Stack(MineCellsBlocks.SMALL_CRATE),
      Stack(MineCellsBlocks.BRITTLE_BARREL),
      Stack(MineCellsBlocks.KING_STATUE),
      Stack(MineCellsBlocks.SKELETON),
      Stack(MineCellsBlocks.ROTTING_CORPSE),
      Stack(MineCellsBlocks.CORPSE),
      Stack(MineCellsBlocks.ELEVATOR_ASSEMBLER),
      Stack(MineCellsBlocks.CELL_FORGE),
      Stack(MineCellsBlocks.HARDSTONE),
      Stack(MineCellsBlocks.CHAIN_PILE_BLOCK),
      Stack(MineCellsBlocks.CHAIN_PILE),
      Stack(MineCellsBlocks.BIG_CHAIN),
      Stack(MineCellsBlocks.CAGE),
      Stack(MineCellsBlocks.BROKEN_CAGE),
      Stack(MineCellsBlocks.SPIKES),
      Stack(MineCellsBlocks.FLAG_POLE),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.TORN_KING_CREST),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.PROMENADE),
      MineCellsItems.BIOME_BANNER.stackOf(BiomeBannerBlock.BannerPattern.INSUFFERABLE_CRYPT),
      Stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_0),
      Stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_1),
      Stack(MineCellsBlocks.ALCHEMY_EQUIPMENT_2),
      Stack(MineCellsBlocks.PRISON_TORCH),
      Stack(MineCellsBlocks.PROMENADE_TORCH),
      Stack(MineCellsItems.SEWAGE_BUCKET),
      Stack(MineCellsItems.ANCIENT_SEWAGE_BUCKET),
      Stack(MineCellsItems.ELEVATOR_MECHANISM),
      Stack(MineCellsItems.GUTS),
      Stack(MineCellsItems.MONSTERS_EYE),
      Stack(MineCellsItems.HEALTH_FLASK),
      Stack(MineCellsItems.BLANK_RUNE),
      Stack(MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE),
      Stack(MineCellsItems.ASSASSINS_DAGGER),
      Stack(MineCellsItems.BLOOD_SWORD),
      Stack(MineCellsItems.CURSED_SWORD),
      Stack(MineCellsItems.HATTORIS_KATANA),
      Stack(MineCellsItems.TENTACLE),
      Stack(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG),
      Stack(MineCellsEntities.SHOCKER_SPAWN_EGG),
      Stack(MineCellsEntities.GRENADIER_SPAWN_EGG),
      Stack(MineCellsEntities.DISGUSTING_WORM_SPAWN_EGG),
      Stack(MineCellsEntities.INQUISITOR_SPAWN_EGG),
      Stack(MineCellsEntities.KAMIKAZE_SPAWN_EGG),
      Stack(MineCellsEntities.PROTECTOR_SPAWN_EGG),
      Stack(MineCellsEntities.UNDEAD_ARCHER_SPAWN_EGG),
      Stack(MineCellsEntities.SHIELDBEARER_SPAWN_EGG),
      Stack(MineCellsEntities.MUTATED_BAT_SPAWN_EGG),
      Stack(MineCellsEntities.SEWERS_TENTACLE_SPAWN_EGG),
      Stack(MineCellsEntities.RANCID_RAT_SPAWN_EGG),
      Stack(MineCellsEntities.RUNNER_SPAWN_EGG),
      Stack(MineCellsEntities.SCORPION_SPAWN_EGG)
    ))).build();

  public static ItemGroup MINECELLS_DEVELOPMENT = null;

  public static void init() {
    if (MineCells.COMMON_CONFIG.items.enableDevelopmentTab) {
      MINECELLS_DEVELOPMENT = FabricItemGroupBuilder.create(MineCells.createId("development"))
        .icon(() -> new ItemStack(Blocks.BARRIER))
        .appendItems(stacks -> stacks.addAll(List.of(
          Stack(Items.DEBUG_STICK),
          Stack(Items.WOODEN_AXE),
          Stack(Items.JIGSAW),
          Stack(Items.STRUCTURE_BLOCK),
          Stack(Items.STRUCTURE_VOID),
          Stack(Items.BARRIER),
          SpawnerRuneItem.of("Arena", EntryList.TEST, 60 * 20, 2, 4, 8.0F, 16.0F),
          SpawnerRuneItem.of("Prison", EntryList.PRISON, 30 * 20, 1, 2, 6.0F, 10.0F),
          SpawnerRuneItem.of("Promenade - Ranged", EntryList.PROMENADE_RANGED, 90 * 20, 1, 1, 3.0F, 20.0F),
          SpawnerRuneItem.of("Promenade - Melee", EntryList.PROMENADE_MELEE, 90 * 20, 1, 1, 3.0F, 10.0F),
          SpawnerRuneItem.of("Runner", EntryList.single(MineCellsEntities.RUNNER), 3 * 60 * 20, 1, 1, 3.0F, 8.0F),
          SpawnerRuneItem.of("Mutated Bats", EntryList.single(MineCellsEntities.MUTATED_BAT), 90 * 20, 1, 2, 5.0F, 10.0F),
          SpawnerRuneItem.of("Protector", EntryList.PROTECTOR, 5 * 60 * 20, 1, 1, 0.0F, 32.0F),
          SpawnerRuneItem.of("Shocker", EntryList.SHOCKER, 120 * 20, 1, 1, 0.0F, 32.0F),
          Stack(MineCellsBlocks.BARRIER_RUNE)
        ))).build();
    }
  }
}
