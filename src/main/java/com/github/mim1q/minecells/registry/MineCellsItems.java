package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.BiomeBannerItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.SpawnerRuneItem;
import com.github.mim1q.minecells.item.weapon.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class MineCellsItems {

  private static final Set<Item> simpleItems = new HashSet<>();

  public static final Item ELEVATOR_MECHANISM = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)),
    "elevator_mechanism"
  );

  // AOF 6 uses this as an icon for the mod
  public static final Item CHARGED_INTERDIMENSIONAL_RUNE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS).maxCount(1)),
    "charged_interdimensional_rune"
  );

  public static final BiomeBannerItem BIOME_BANNER = registerSimpleItem(
    new BiomeBannerItem(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)),
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

  public static final Item ASSASSINS_DAGGER = register(new AssassinsDaggerItem(6, 5, -2.0F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
      .rarity(Rarity.UNCOMMON)
    ), "assassins_dagger"
  );

  public static final Item BLOOD_SWORD = register(new BloodSwordItem(4, -2.4F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
      .rarity(Rarity.UNCOMMON)
    ), "blood_sword"
  );

  public static final Item CURSED_SWORD = register(new CursedSwordItem(22, -3.0F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(600)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
    ), "cursed_sword"
  );

  public static final Item TENTACLE = register(
    new TentacleItem(9, 0, -3.0F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(800)
      .rarity(Rarity.EPIC)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
    ), "tentacle"
  );

  public static final Item HATTORIS_KATANA = register(
    new HattorisKatanaItem(5, -2.2F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.RARE)
      .group(MineCellsItemGroups.MINECELLS_WEAPONS)
    ), "hattoris_katana"
  );

  public static final Item HEALTH_FLASK = registerSimpleItem(
    new HealthFlaskItem(new FabricItemSettings()
      .maxCount(16)
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)
    ), "health_flask"
  );

  public static final SpawnerRuneItem SPAWNER_RUNE = register(
    new SpawnerRuneItem(new FabricItemSettings()
      .maxCount(1)
      .group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)
    ), "spawner_rune"
  );

  public static void init() {
  }

  public static <E extends Item> E register(E item, String name) {
    Registry.register(Registry.ITEM, MineCells.createId(name), item);
    return item;
  }

  public static <E extends Item> E registerSimpleItem(E item, String name) {
    simpleItems.add(item);
    return register(item, name);
  }

  public static Set<Item> getSimpleItems() {
    return simpleItems;
  }
}
