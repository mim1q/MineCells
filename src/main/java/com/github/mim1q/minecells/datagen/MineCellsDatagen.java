package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.datagen.util.AllDatagenUtils;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class MineCellsDatagen extends AllDatagenUtils {
  @Override
  public void initialize() {
    initializeSimpleBlocks();
    initializeWoodSets();
    initializeStoneSets();
    initializeLeavesSets();
    initializeTorches();

    initializeCustomItemModels();
  }

  private void initializeSimpleBlocks() {
    var blocks = List.of(
      MineCellsBlocks.ELEVATOR_ASSEMBLER,
      MineCellsBlocks.CRATE,
      MineCellsBlocks.HARDSTONE,
      MineCellsBlocks.CHAIN_PILE_BLOCK
    );

    getInitializers().blockState().add(it -> {
      blocks.forEach(it::registerSimpleCubeAll);

      List.of(MineCellsBlocks.CELL_CRAFTER, MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER).forEach(it::registerNorthDefaultHorizontalRotation);
    });

    addSingleBlockstate(MineCellsBlocks.SPAWNER_RUNE);

    getInitializers().blockLootTable().add(it -> blocks.forEach(it::addDrop));
  }

  private void initializeWoodSets() {
    addWoodSet(MineCellsBlocks.PUTRID_WOOD);
  }

  private void initializeStoneSets() {
    addSimpleSet(MineCellsBlocks.PUTRID_BOARD);

    // Prison Stone
    addFullStoneSet(MineCellsBlocks.PRISON_STONE);
    addStoneSet(MineCellsBlocks.PRISON_BRICKS);
    addStoneSet(MineCellsBlocks.PRISON_COBBLESTONE);
    addStoneSet(MineCellsBlocks.CRACKED_PRISON_BRICKS);
    addStoneSet(MineCellsBlocks.SMALL_PRISON_BRICKS);
    var grassOverlay = MineCells.createId("block/wilted_grass_block_overlay");
    addGrass(MineCellsBlocks.WILTED_GRASS_BLOCK, MineCellsBlocks.PRISON_STONE.block, grassOverlay);

    // Bloomrock
    addStoneSet(MineCellsBlocks.BLOOMROCK);
    addStoneSet(MineCellsBlocks.BLOOMROCK_BRICKS);
    addStoneSet(MineCellsBlocks.BLOOMROCK_TILES);
    addStoneSet(MineCellsBlocks.CRACKED_BLOOMROCK_BRICKS);
    addGrass(MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK, MineCellsBlocks.BLOOMROCK.block, grassOverlay);

    // Septite
    addStoneSet(MineCellsBlocks.SEPTITE);
    addStoneSet(MineCellsBlocks.COBBLED_SEPTITE);
    addStoneSet(MineCellsBlocks.SEPTITE_BRICKS);
    addStoneSet(MineCellsBlocks.POLISHED_SEPTITE);
    addStoneSet(MineCellsBlocks.SMALL_SEPTITE_BRICKS);

    // Ancient Septite
    addStoneSet(MineCellsBlocks.ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.COBBLED_ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.ANCIENT_SEPTITE_BRICKS);
    addStoneSet(MineCellsBlocks.POLISHED_ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.SMALL_ANCIENT_SEPTITE_BRICKS);

    // Corpses
    addCorpse(MineCellsBlocks.CORPSE, MineCellsBlocks.HANGED_CORPSE, Items.ROTTEN_FLESH, MineCellsItems.GUTS);
    addCorpse(MineCellsBlocks.SKELETON, MineCellsBlocks.HANGED_SKELETON, Items.BONE, Items.SKELETON_SKULL);
    addCorpse(MineCellsBlocks.ROTTING_CORPSE, MineCellsBlocks.HANGED_ROTTING_CORPSE, Items.ROTTEN_FLESH, MineCellsItems.GUTS);

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
    initializeHandheldItemModels();

    MineCellsItems.BOWS.forEach(this::addBow);
    MineCellsItems.CROSSBOWS.forEach(this::addCrossbow);
    MineCellsItems.SHIELDS.forEach(this::addShield);

    var parented = List.of(
      MineCellsBlocks.FLAG_POLE,
      MineCellsBlocks.BRITTLE_BARREL,
      MineCellsBlocks.CELL_CRAFTER,
      MineCellsBlocks.SKELETON,
      MineCellsBlocks.CORPSE,
      MineCellsBlocks.ROTTING_CORPSE
    );

    getInitializers().blockState().add(it -> {
      parented.forEach(block -> it.registerParentedItemModel(block.asItem(), getBlockId(block)));
      it.registerItemModel(MineCellsBlocks.SPIKES);
    });
  }

  private void initializeGeneratedItemModels() {
    var doorways = MineCellsItems.DOORWAY_COLORS.keySet();
    var spawnEggs = MineCellsEntities.SPAWN_EGGS;
    var flags = MineCellsBlocks.FLAG_BLOCKS;

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
      MineCellsItems.ARCANE_GOO,
      MineCellsItems.PHASER,
      MineCellsItems.FROST_BLAST
    );

    getInitializers().itemModel().add(it -> {
      doorways.forEach(item -> {
        var model = new Model(Optional.of(MineCells.createId("item/doorway")), Optional.of("inventory"));
        model.upload(ModelIds.getItemModelId(item), new TextureMap(), it.writer);
      });
    });

    misc.forEach(this::addGeneratedItem);
    flags.forEach(this::addFlag);

    for (var egg : spawnEggs) {
      var id = getItemId(egg, "spawn_eggs/");
      addGeneratedItem(egg, Identifier.of(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf("_spawn_egg"))));
    }

    getInitializers().itemModel().add(it -> {
      Models.GENERATED.upload(MineCells.createId("item/guidebook"), TextureMap.layer0(MineCells.createId("item/guidebook")), it.writer);
    });
  }

  private void initializeHandheldItemModels() {
    var handheldItems = List.of(
      MineCellsItems.ASSASSINS_DAGGER,
      MineCellsItems.BLOOD_SWORD,
      MineCellsItems.BROADSWORD,
      MineCellsItems.BALANCED_BLADE,
      MineCellsItems.CROWBAR,
      MineCellsItems.NUTCRACKER,
      MineCellsItems.CURSED_SWORD,
      MineCellsItems.HATTORIS_KATANA,
      MineCellsItems.TENTACLE,
      MineCellsItems.SPITE_SWORD,
      MineCellsItems.FLINT,
      MineCellsItems.LIGHTNING_BOLT
    );

    handheldItems.forEach(this::addHandheldItem);
  }
}