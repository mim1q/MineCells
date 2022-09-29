package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.AssassinsDaggerItem;
import com.github.mim1q.minecells.item.InterdimensionalRuneItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class MineCellsItems {

  private static final Set<Item> simpleItems = new HashSet<>();

  public static final Item ELEVATOR_MECHANISM = new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final Item INTERDIMENSIONAL_RUNE = new InterdimensionalRuneItem(new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1).maxDamage(20));
  public static final Item CHARGED_INTERDIMENSIONAL_RUNE = new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1));
  public static final Item BIOME_BANNER = new AliasedBlockItem(MineCellsBlocks.BIOME_BANNER, new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final BucketItem SEWAGE_BUCKET = new BucketItem(MineCellsFluids.STILL_SEWAGE, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
  public static final BucketItem ANCIENT_SEWAGE_BUCKET = new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));

  public static final AssassinsDaggerItem ASSASSINS_DAGGER = new AssassinsDaggerItem(new FabricItemSettings()
    .group(MineCellsItemGroups.MINECELLS_WEAPONS)
    .maxDamage(1200));

  public static void init() {
    registerSimpleItem(ELEVATOR_MECHANISM, "elevator_mechanism");
    registerSimpleItem(SEWAGE_BUCKET, "sewage_bucket");
    registerSimpleItem(ANCIENT_SEWAGE_BUCKET, "ancient_sewage_bucket");
    registerSimpleItem(INTERDIMENSIONAL_RUNE, "interdimensional_rune");
    registerSimpleItem(CHARGED_INTERDIMENSIONAL_RUNE, "charged_interdimensional_rune");
    registerSimpleItem(BIOME_BANNER, "biome_banner");

    Registry.register(Registry.ITEM, MineCells.createId("assassins_dagger"), ASSASSINS_DAGGER);
  }

  public static Item registerSimpleItem(Item item, String name) {
    Registry.register(Registry.ITEM, MineCells.createId(name), item);
    simpleItems.add(item);
    return item;
  }

  public static Set<Item> getSimpleItems() {
    return simpleItems;
  }
}
