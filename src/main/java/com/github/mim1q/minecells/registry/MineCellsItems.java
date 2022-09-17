package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.AssassinsDaggerItem;
import com.github.mim1q.minecells.item.InterdimensionalRuneItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class MineCellsItems {

  private static final Set<Item> simpleItems = new HashSet<>();

  public static final Item ELEVATOR_MECHANISM = new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));

  public static final Item INTERDIMENSIONAL_RUNE = new InterdimensionalRuneItem(new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1).maxDamage(20));
  public static final Item CHARGED_INTERDIMENSIONAL_RUNE = new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1));
  public static final BucketItem SEWAGE_BUCKET = new BucketItem(MineCellsFluids.STILL_SEWAGE, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
  public static final BucketItem ANCIENT_SEWAGE_BUCKET = new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));

  public static final AssassinsDaggerItem ASSASSINS_DAGGER = new AssassinsDaggerItem(new FabricItemSettings()
    .group(MineCellsItemGroups.MINECELLS_WEAPONS)
    .maxDamage(1200));

  public static void init() {
    registerSimpleItem("elevator_mechanism", ELEVATOR_MECHANISM);
    registerSimpleItem("sewage_bucket", SEWAGE_BUCKET);
    registerSimpleItem("ancient_sewage_bucket", ANCIENT_SEWAGE_BUCKET);
    registerSimpleItem("interdimensional_rune", INTERDIMENSIONAL_RUNE);
    registerSimpleItem("charged_interdimensional_rune", CHARGED_INTERDIMENSIONAL_RUNE);

    Registry.register(Registry.ITEM, MineCells.createId("assassins_dagger"), ASSASSINS_DAGGER);
  }

  public static void registerSimpleItem(String name, Item item) {
    Registry.register(Registry.ITEM, MineCells.createId(name), item);
    simpleItems.add(item);
  }

  public static Set<Item> getSimpleItems() {
    return simpleItems;
  }
}
