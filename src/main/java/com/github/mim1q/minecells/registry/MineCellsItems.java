package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.BiomeBannerItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.SpawnerRuneItem;
import com.github.mim1q.minecells.item.skill.PhaserItem;
import com.github.mim1q.minecells.item.weapon.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class MineCellsItems {

  private static final Set<Item> simpleItems = new HashSet<>();

  public static final Item ELEVATOR_MECHANISM = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS)),
    "elevator_mechanism"
  );

  // AOF 6 uses this as an icon for the mod
  public static final Item CHARGED_INTERDIMENSIONAL_RUNE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS).maxCount(1)),
    "charged_interdimensional_rune"
  );

  public static final Item BLANK_RUNE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS).maxCount(1)),
    "blank_rune"
  );

  public static final Item CONJUNCTIVIUS_RESPAWN_RUNE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS).maxCount(1)),
    "conjunctivius_respawn_rune"
  );

  public static final Item GUTS = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS).food(FoodComponents.BEEF)),
    "guts"
  );

  public static final Item MONSTERS_EYE = registerSimpleItem(
    new Item(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS).food(FoodComponents.COOKED_BEEF)),
    "monsters_eye"
  );

  public static final BiomeBannerItem BIOME_BANNER = register(
    new BiomeBannerItem(new FabricItemSettings().group(MineCellsItemGroups.MINECELLS)),
    "biome_banner"
  );

  public static final Item SEWAGE_BUCKET = registerSimpleItem(
    new BucketItem(MineCellsFluids.STILL_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      .group(MineCellsItemGroups.MINECELLS)
    ),
    "sewage_bucket"
  );

  public static final Item ANCIENT_SEWAGE_BUCKET = registerSimpleItem(
    new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      .group(MineCellsItemGroups.MINECELLS)
    ),
    "ancient_sewage_bucket"
  );

  public static final Item ASSASSINS_DAGGER = register(new AssassinsDaggerItem(2, -2.1F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .group(MineCellsItemGroups.MINECELLS)
      .rarity(Rarity.UNCOMMON)
    ), "assassins_dagger"
  );

  public static final Item BLOOD_SWORD = register(new BloodSwordItem(4, -2.4F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.UNCOMMON)
      .group(MineCellsItemGroups.MINECELLS)
    ), "blood_sword"
  );

  public static final Item CURSED_SWORD = register(new CursedSwordItem(22, -3.0F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(600)
      .group(MineCellsItemGroups.MINECELLS)
    ), "cursed_sword"
  );

  public static final Item TENTACLE = register(
    new TentacleItem(9, 0, -3.0F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(800)
      .rarity(Rarity.EPIC)
      .group(MineCellsItemGroups.MINECELLS)
    ), "tentacle"
  );

  public static final Item HATTORIS_KATANA = register(
    new HattorisKatanaItem(5, -2.2F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.RARE)
      .group(MineCellsItemGroups.MINECELLS)
    ), "hattoris_katana"
  );

  public static final Item BROADSWORD = register(new SwordItem(ToolMaterials.IRON, 7, -2.9F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.COMMON)
      .group(MineCellsItemGroups.MINECELLS)
    ), "broadsword"
  );

  public static final Item HEALTH_FLASK = registerSimpleItem(
    new HealthFlaskItem(new FabricItemSettings()
      .maxCount(16)
      .group(MineCellsItemGroups.MINECELLS)
    ), "health_flask"
  );

  public static final SpawnerRuneItem SPAWNER_RUNE = register(
    new SpawnerRuneItem(new FabricItemSettings()
      .maxCount(1)
      .group(MineCellsItemGroups.MINECELLS)
    ), "spawner_rune"
  );

  // Skills
  public static final PhaserItem PHASER = registerSimpleItem(new PhaserItem(), "phaser");

  public static void init() { }

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
