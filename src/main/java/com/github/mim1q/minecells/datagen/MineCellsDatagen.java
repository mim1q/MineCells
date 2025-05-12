package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.datagen.util.AllDatagenUtils;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;

import java.util.List;
import java.util.Optional;

public class MineCellsDatagen extends AllDatagenUtils {
  @Override
  public void initialize() {
    initializeWoodSets();
    initializeStoneSets();
    initializeLeavesSets();
    initializeTorches();

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

  private void initializeLeavesSets() {
    addLeavesSet(MineCellsBlocks.WILTED_LEAVES, MineCellsBlocks.PUTRID_SAPLING);
    addLeavesSet(MineCellsBlocks.ORANGE_WILTED_LEAVES, MineCellsBlocks.ORANGE_PUTRID_SAPLING);
    addLeavesSet(MineCellsBlocks.RED_WILTED_LEAVES, MineCellsBlocks.RED_PUTRID_SAPLING);
  }

  private void initializeTorches() {
    addColoredTorch(MineCellsBlocks.PRISON_TORCH, "prison");
    addColoredTorch(MineCellsBlocks.PROMENADE_TORCH, "promenade");
    addColoredTorch(MineCellsBlocks.RAMPARTS_TORCH, "ramparts");
    addColoredTorch(MineCellsBlocks.SEWERS_TORCH, "sewers");
    addColoredTorch(MineCellsBlocks.ANCIENT_SEWERS_TORCH, "ancient_sewers");
  }

  private void initializeCustomItemModels() {
    initializeGeneratedItemModels();
  }

  private void initializeGeneratedItemModels() {
    var doorways = MineCellsItems.DOORWAY_COLORS.keySet();

    var misc = List.of(
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
    );

    getInitializers().itemModel().add(it -> {
      doorways.forEach(item -> {
        var model = new Model(Optional.of(MineCells.createId("item/doorway")), Optional.of("inventory"));
        model.upload(ModelIds.getItemModelId(item), new TextureMap(), it.writer);
      });
    });

    misc.forEach(this::addGeneratedItem);
  }
}