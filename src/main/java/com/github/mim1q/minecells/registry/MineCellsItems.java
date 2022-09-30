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

  public static final Item ELEVATOR_MECHANISM = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)),
    "elevator_mechanism"
  );

  public static final Item INTERDIMENSIONAL_RUNE = registerSimpleItem(
    new InterdimensionalRuneItem(new FabricItemSettings()
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)
      .maxCount(1)
    ),
    "interdimensional_rune"
  );

  public static final Item CHARGED_INTERDIMENSIONAL_RUNE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1)),
    "charged_interdimensional_rune"
  );

  public static final Item BIOME_BANNER = registerSimpleItem(
    new AliasedBlockItem(MineCellsBlocks.BIOME_BANNER, new FabricItemSettings()
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)),
    "biome_banner"
  );

  public static final Item SEWAGE_BUCKET = registerSimpleItem(
    new BucketItem(MineCellsFluids.STILL_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)
    ),
    "sewage_bucket"
  );

  public static final Item ANCIENT_SEWAGE_BUCKET = registerSimpleItem(
    new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)
    ),
    "ancient_sewage_bucket"
  );

  public static final Item ASSASSINS_DAGGER = new AssassinsDaggerItem(new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
  );

  public static void init() {
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
