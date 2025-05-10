package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.datagen.util.AllDatagenUtils;
import com.github.mim1q.minecells.registry.MineCellsBlocks;

public class MineCellsDatagen extends AllDatagenUtils {
  @Override
  public void initialize() {
    initializeWoodSets();
    initializeStoneSets();
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
}