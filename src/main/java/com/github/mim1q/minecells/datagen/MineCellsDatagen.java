package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.block.RunicVineBlock;
import com.github.mim1q.minecells.datagen.util.AllDatagenUtils;
import com.github.mim1q.minecells.datagen.util.specific.ItemConversionUtils;
import com.github.mim1q.minecells.item.MineCellsItemTags;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

import static com.github.mim1q.minecells.datagen.util.DatagenModelUtils.getBlockId;
import static com.github.mim1q.minecells.datagen.util.DatagenModelUtils.getItemId;
import static com.github.mim1q.minecells.registry.MineCellsBlocks.LARGE_RIBBON_FLAGS;
import static com.github.mim1q.minecells.registry.MineCellsBlocks.RIBBON_FLAGS;
import static net.minecraft.data.client.BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates;

public class MineCellsDatagen extends AllDatagenUtils {
  @Override
  public void initialize() {
    initializeSimpleBlocks();
    initializeWoodSets();
    initializeStoneSets();
    initializeLeavesSets();
    initializeTorches();
    initializeDoorways();
    initializeMiscBlocks();

    initializeCustomItemModels();

    initializeTags();
  }

  private void initializeSimpleBlocks() {
    var blocks = List.of(
      MineCellsBlocks.ELEVATOR_ASSEMBLER,
      MineCellsBlocks.CRATE,
      MineCellsBlocks.HARDSTONE,
      MineCellsBlocks.CHAIN_PILE_BLOCK,
      MineCellsBlocks.RUNIC_VINE_STONE
    );

    getInitializers().blockState().add(it -> {
      blocks.forEach(it::registerSimpleCubeAll);

      it.registerNorthDefaultHorizontalRotation(MineCellsBlocks.CELL_CRAFTER);
      it.blockStateCollector.accept(
        VariantsBlockStateSupplier.create(
            MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER,
            BlockStateVariant.create().put(VariantSettings.MODEL, getBlockId(MineCellsBlocks.CELL_CRAFTER))
          )
          .coordinate(createNorthDefaultHorizontalRotationStates()));
    });

    getInitializers().blockLootTable().add(it -> blocks.forEach(it::addDrop));
  }

  private void initializeWoodSets() {
    addWoodSet(MineCellsBlocks.PUTRID_WOOD);
  }

  private void initializeStoneSets() {
    addSimpleSet(MineCellsBlocks.PUTRID_BOARD);

    // Prison Stone
    addFullStoneSet(MineCellsBlocks.PRISON_STONE, false);
    addSilkTouchDrop(MineCellsBlocks.PRISON_STONE.block, MineCellsBlocks.PRISON_COBBLESTONE.block);
    addStoneSet(MineCellsBlocks.PRISON_BRICKS, true);
    addStoneSet(MineCellsBlocks.PRISON_COBBLESTONE, true);
    addStoneSet(MineCellsBlocks.CRACKED_PRISON_BRICKS, true);
    addStoneSet(MineCellsBlocks.SMALL_PRISON_BRICKS, true);
    var grassOverlay = MineCells.createId("block/wilted_grass_block_overlay");
    addGrass(MineCellsBlocks.WILTED_GRASS_BLOCK, MineCellsBlocks.PRISON_STONE.block, grassOverlay);

    // Bloomrock
    addStoneSet(MineCellsBlocks.BLOOMROCK, true);
    addStoneSet(MineCellsBlocks.BLOOMROCK_BRICKS, true, MineCellsBlocks.BLOOMROCK);
    addStoneSet(MineCellsBlocks.BLOOMROCK_TILES, true, MineCellsBlocks.BLOOMROCK);
    addStoneSet(MineCellsBlocks.CRACKED_BLOOMROCK_BRICKS, true, MineCellsBlocks.BLOOMROCK);
    addGrass(MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK, MineCellsBlocks.BLOOMROCK.block, grassOverlay);

    // Septite
    addStoneSet(MineCellsBlocks.SEPTITE, false);
    addSilkTouchDrop(MineCellsBlocks.SEPTITE.block, MineCellsBlocks.COBBLED_SEPTITE.block);
    addStoneSet(MineCellsBlocks.COBBLED_SEPTITE, true, MineCellsBlocks.SEPTITE);
    addStoneSet(MineCellsBlocks.SEPTITE_BRICKS, true, MineCellsBlocks.SEPTITE);
    addStoneSet(MineCellsBlocks.POLISHED_SEPTITE, true, MineCellsBlocks.SEPTITE);
    addStoneSet(MineCellsBlocks.SMALL_SEPTITE_BRICKS, true, MineCellsBlocks.SEPTITE);

    // Ancient Septite
    addStoneSet(MineCellsBlocks.ANCIENT_SEPTITE, false);
    addSilkTouchDrop(MineCellsBlocks.ANCIENT_SEPTITE.block, MineCellsBlocks.COBBLED_ANCIENT_SEPTITE.block);
    addStoneSet(MineCellsBlocks.COBBLED_ANCIENT_SEPTITE, true, MineCellsBlocks.ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.ANCIENT_SEPTITE_BRICKS, true, MineCellsBlocks.ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.POLISHED_ANCIENT_SEPTITE, true, MineCellsBlocks.ANCIENT_SEPTITE);
    addStoneSet(MineCellsBlocks.SMALL_ANCIENT_SEPTITE_BRICKS, true, MineCellsBlocks.ANCIENT_SEPTITE);

    // Corpses
    addCorpse(MineCellsBlocks.CORPSE, MineCellsBlocks.HANGED_CORPSE, Items.ROTTEN_FLESH, MineCellsItems.GUTS);
    addCorpse(MineCellsBlocks.SKELETON, MineCellsBlocks.HANGED_SKELETON, Items.BONE, Items.SKELETON_SKULL);
    addCorpse(MineCellsBlocks.ROTTING_CORPSE, MineCellsBlocks.HANGED_ROTTING_CORPSE, Items.ROTTEN_FLESH,
      MineCellsItems.GUTS
    );

  }

  private void initializeMiscBlocks() {
    getInitializers().blockState().add(it -> {
      it.registerAxisRotated(MineCellsBlocks.UNBREAKABLE_CHAIN, getBlockId(Blocks.CHAIN));
    });

    addSingleBlockstate(MineCellsBlocks.RETURN_STONE);
    addSingleBlockstate(MineCellsBlocks.RUNIC_VINE_PLANT);

    addInvisibleBlock(MineCellsBlocks.BARRIER_RUNE);
    addInvisibleBlock(MineCellsBlocks.SOLID_BARRIER);
    addInvisibleBlock(MineCellsBlocks.CONDITIONAL_BARRIER);
    addInvisibleBlock(MineCellsBlocks.BOSS_ENTRY_BARRIER_CONTROLLER);
    addInvisibleBlock(MineCellsBlocks.BOSS_BARRIER_CONTROLLER);
    addInvisibleBlock(MineCellsBlocks.PLAYER_BARRIER_CONTROLLER);
    addInvisibleBlock(MineCellsBlocks.CONJUNCTIVIUS_BOX);
    addInvisibleBlock(MineCellsBlocks.CONCIERGE_BOX);
    addInvisibleBlock(MineCellsBlocks.RIFT);
    addInvisibleBlock(MineCellsBlocks.DOORWAY_FRAME);
    addInvisibleBlock(MineCellsBlocks.UNBREAKABLE_DOORWAY_FRAME);
    addInvisibleBlock(MineCellsBlocks.ARROW_SIGN);
    addInvisibleBlock(MineCellsBlocks.BEAM_PLACER);
    addInvisibleBlock(MineCellsBlocks.SEWAGE);
    addInvisibleBlock(MineCellsBlocks.ANCIENT_SEWAGE);
    addInvisibleBlock(MineCellsBlocks.KINGDOM_PORTAL_CORE);

    addCrossModel(MineCells.createId("block/runic_vine"));
    addCrossModel(MineCells.createId("block/runic_vine_top"));
    getInitializers().blockState().add(it -> {
      it.blockStateCollector.accept(
        VariantsBlockStateSupplier.create(MineCellsBlocks.SPAWNER_RUNE, BlockStateVariant.create()
          .put(VariantSettings.MODEL, getBlockId(MineCellsBlocks.SPAWNER_RUNE))
        ));
      it.blockStateCollector.accept(VariantsBlockStateSupplier.create(MineCellsBlocks.RUNIC_VINE)
        .coordinate(BlockStateVariantMap.create(RunicVineBlock.TOP)
          .register(false,
            BlockStateVariant.create().put(VariantSettings.MODEL, MineCells.createId("block/runic_vine"))
          )
          .register(true,
            BlockStateVariant.create().put(VariantSettings.MODEL, MineCells.createId("block/runic_vine_top"))
          )
        )
      );
    });
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

  private void initializeDoorways() {
    addDoorway(MineCellsBlocks.OVERWORLD_DOORWAY);
    addDoorway(MineCellsBlocks.PRISON_DOORWAY);
    addDoorway(MineCellsBlocks.PROMENADE_DOORWAY);
    addDoorway(MineCellsBlocks.RAMPARTS_DOORWAY);
    addDoorway(MineCellsBlocks.BLACK_BRIDGE_DOORWAY);
    addDoorway(MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY);
  }

  private void initializeCustomItemModels() {
    initializeGeneratedItemModels();
    initializeHandheldItemModels();
    initializeWeaponCopies();

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
      it.registerParentedItemModel(MineCellsBlocks.UNBREAKABLE_CELL_CRAFTER, getBlockId(MineCellsBlocks.CELL_CRAFTER));
    });
  }

  private void initializeWeaponCopies() {
    var handheldItems = CustomMeleeWeapon.getAllMeleeWeapons();
    getInitializers().itemModel().add(it -> {
      for (var item : handheldItems) {
        var id = getItemId(item);
        var newId = Identifier.of(
          id.getNamespace(),
          id.getPath().replace("item/", "item/weapon/")
        );
        new Model(Optional.of(newId), Optional.empty()).upload(id, new TextureMap(), it.writer);
      }
    });
  }

  private void initializeGeneratedItemModels() {
    var doorways = MineCellsItems.DOORWAY_COLORS.keySet();
    var spawnEggs = MineCellsEntities.SPAWN_EGGS;

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

    addGeneratedTexture(MineCells.createId("item/cell_holder/1"));
    addGeneratedTexture(MineCells.createId("item/cell_holder/2"));
    addGeneratedTexture(MineCells.createId("item/cell_holder/3"));
    addGeneratedItem(MineCellsBlocks.SOLID_BARRIER, getItemId(MineCellsBlocks.BARRIER_RUNE));

    getInitializers().itemModel().add(it -> {
      doorways.forEach(item -> {
        var model = new Model(Optional.of(MineCells.createId("item/doorway")), Optional.of("inventory"));
        model.upload(ModelIds.getItemModelId(item), new TextureMap(), it.writer);
      });
      Models.GENERATED.upload(MineCells.createId("item/guidebook"),
        TextureMap.layer0(MineCells.createId("item/guidebook")), it.writer
      );
    });

    misc.forEach(this::addGeneratedItem);
    LARGE_RIBBON_FLAGS.forEach((dye, it) -> addFlag(it, ItemConversionUtils.DYE_TO_WOOL.get(dye), null));
    RIBBON_FLAGS.forEach((dye, it) -> addFlag(it, ItemConversionUtils.DYE_TO_WOOL.get(dye), null));

    addFlag(MineCellsBlocks.KINGS_CREST_FLAG, Blocks.BLUE_WOOL, null);
    addFlag(MineCellsBlocks.TORN_KINGS_CREST_FLAG, Blocks.BLUE_WOOL, null);
    addFlag(MineCellsBlocks.PROMENADE_OF_THE_CONDEMNED_FLAG, Blocks.BLUE_WOOL, MineCells.createId("promenade"));
    addFlag(MineCellsBlocks.RAMPARTS_FLAG, Blocks.YELLOW_WOOL, MineCells.createId("ramparts"));
    addFlag(MineCellsBlocks.INSUFFERABLE_CRYPT_FLAG, Blocks.GREEN_WOOL, MineCells.createId("insufferable_crypt"));
    addFlag(MineCellsBlocks.BLACK_BRIDGE_FLAG, Blocks.PURPLE_WOOL, MineCells.createId("black_bridge"));

    for (var egg : spawnEggs) {
      var id = getItemId(egg, "spawn_eggs/");
      addGeneratedItem(egg,
        Identifier.of(id.getNamespace(), id.getPath().substring(0, id.getPath().lastIndexOf("_spawn_egg")))
      );
    }
  }

  private void initializeHandheldItemModels() {
    var handheldItems = List.of(
      MineCellsItems.LIGHTNING_BOLT
    );

    handheldItems.forEach(this::addHandheldItem);
  }

  private void initializeTags() {
    // Blocks
    addBlockTag(MineCellsBlockTags.TREE_ROOT_REPLACEABLE,
      Blocks.AIR, MineCellsBlocks.PRISON_STONE.block, MineCellsBlocks.PRISON_COBBLESTONE.block,
      MineCellsBlocks.WILTED_GRASS_BLOCK, MineCellsBlocks.BLOOMROCK_WILTED_GRASS_BLOCK
    );

    // Items
    addItemTag(MineCellsItemTags.BOWS_ACCEPTING_INFINITY,
      MineCellsItems.MULTIPLE_NOCKS_BOW, MineCellsItems.MARKSMANS_BOW, MineCellsItems.INFANTRY_BOW,
      MineCellsItems.NERVES_OF_STEEL
    );
    addItemTag(MineCellsItemTags.BOWS_ACCEPTING_FLAME,
      MineCellsItems.MULTIPLE_NOCKS_BOW, MineCellsItems.BOW_AND_ENDLESS_QUIVER, MineCellsItems.MARKSMANS_BOW,
      MineCellsItems.INFANTRY_BOW, MineCellsItems.QUICK_BOW, MineCellsItems.NERVES_OF_STEEL
    );
    addItemTag(MineCellsItemTags.BOWS_ACCEPTING_POWER,
      MineCellsItems.MULTIPLE_NOCKS_BOW, MineCellsItems.BOW_AND_ENDLESS_QUIVER, MineCellsItems.MARKSMANS_BOW,
      MineCellsItems.INFANTRY_BOW, MineCellsItems.QUICK_BOW, MineCellsItems.ICE_BOW, MineCellsItems.NERVES_OF_STEEL
    );
    addItemTag(MineCellsItemTags.BOWS_ACCEPTING_PUNCH,
      MineCellsItems.MULTIPLE_NOCKS_BOW, MineCellsItems.BOW_AND_ENDLESS_QUIVER, MineCellsItems.MARKSMANS_BOW,
      MineCellsItems.INFANTRY_BOW, MineCellsItems.QUICK_BOW, MineCellsItems.ICE_BOW, MineCellsItems.NERVES_OF_STEEL
    );
    addItemTag(MineCellsItemTags.BOWS_ACCEPTING_QUICK_CHARGE,
      MineCellsItems.MULTIPLE_NOCKS_BOW, MineCellsItems.BOW_AND_ENDLESS_QUIVER, MineCellsItems.MARKSMANS_BOW,
      MineCellsItems.INFANTRY_BOW, MineCellsItems.ICE_BOW, MineCellsItems.HEAVY_CROSSBOW,
      MineCellsItems.EXPLOSIVE_CROSSBOW
    );

    addItemTag(ItemTags.STONE_TOOL_MATERIALS, MineCellsBlocks.PRISON_COBBLESTONE.block,
      MineCellsBlocks.BLOOMROCK.block
    );
    addItemTag(ItemTags.STONE_CRAFTING_MATERIALS, MineCellsBlocks.PRISON_COBBLESTONE.block,
      MineCellsBlocks.BLOOMROCK.block
    );
  }
}