package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.datagen.util.AllDatagenUtils;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;

import java.util.List;

public class MineCellsDatagen extends AllDatagenUtils {
  @Override
  public void initialize() {
    initializeWoodSets();
    initializeStoneSets();

    initializeCustomItemModels();
  }

  private void initializeWoodSets() {
    addWoodSet(MineCellsBlocks.PUTRID_WOOD);
  }

  private void initializeStoneSets() {
    // Prison Stone
    addFullStoneSet(MineCellsBlocks.PRISON_STONE);
    addStoneSet(MineCellsBlocks.PRISON_BRICKS);
    addStoneSet(MineCellsBlocks.PRISON_COBBLESTONE);
    addStoneSet(MineCellsBlocks.CRACKED_PRISON_BRICKS);
    addStoneSet(MineCellsBlocks.SMALL_PRISON_BRICKS);

    // Bloomrock
    addStoneSet(MineCellsBlocks.BLOOMROCK);
    addStoneSet(MineCellsBlocks.BLOOMROCK_BRICKS);
    addStoneSet(MineCellsBlocks.BLOOMROCK_TILES);
    addStoneSet(MineCellsBlocks.CRACKED_BLOOMROCK_BRICKS);
  }

  private void initializeCustomItemModels() {
    initializeGeneratedItemModels();
  }

  private void initializeGeneratedItemModels() {
    List.of(
      MineCellsBlocks.CAGE.asItem(),
      MineCellsBlocks.BROKEN_CAGE.asItem(),
      MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE,
      MineCellsItems.VINE_RUNE,
      MineCellsItems.GUTS,
      MineCellsItems.MONSTERS_EYE,
      MineCellsItems.SEWAGE_BUCKET,
      MineCellsItems.ANCIENT_SEWAGE_BUCKET,
      MineCellsItems.HEALTH_FLASK,
      MineCellsBlocks.KING_STATUE.asItem(),
      MineCellsBlocks.BARRIER_RUNE.asItem(),
      MineCellsItems.ELEVATOR_MECHANISM,
      MineCellsItems.RESET_RUNE,
      MineCellsItems.CONCIERGE_RESPAWN_RUNE,
      MineCellsItems.MONSTER_CELL,
      MineCellsItems.BOSS_STEM_CELL,
      MineCellsBlocks.ARROW_SIGN.asItem(),
      MineCellsItems.ELECTRIC_WHIP,
      MineCellsItems.THROWING_KNIFE,
      MineCellsItems.FIREBRANDS,
      MineCellsItems.EXPLOSIVE_BOLT,
      MineCellsItems.ICE_ARROW,
      MineCellsItems.EXPLOSIVE_BULB,
      MineCellsItems.INFECTED_FLESH,
      MineCellsItems.CELL_INFUSED_STEEL,
      MineCellsItems.METAL_SHARDS,
      MineCellsItems.BUZZCUTTER_FANG,
      MineCellsItems.MOLTEN_CHUNK,
      MineCellsItems.SEWER_CALAMARI,
      MineCellsItems.COOKED_SEWER_CALAMARI,
      MineCellsItems.TRANSPOSITION_CORE,
      MineCellsItems.BLOOD_BOTTLE,
      MineCellsItems.ARCANE_GOO
    ).forEach(this::addGeneratedItem);
  }
}