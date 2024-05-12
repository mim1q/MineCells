package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.item.DoorwayItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.ResetRuneItem;
import com.github.mim1q.minecells.item.skill.PhaserItem;
import com.github.mim1q.minecells.item.weapon.*;
import com.github.mim1q.minecells.item.weapon.bow.*;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MineCellsItems {
  public static Map<DoorwayItem, Integer> DOORWAY_COLORS = new LinkedHashMap<>();
  public static List<DimensionalRuneItem> DIMENSIONAL_RUNES = new ArrayList<>();
  public static List<CustomBowItem> BOWS = new ArrayList<>();
  public static List<CustomCrossbowItem> CROSSBOWS = new ArrayList<>();
  public static List<Item> OTHER_RANGED = new ArrayList<>();
  public static List<Item> SHIELDS = new ArrayList<>();

  public static final Item ELEVATOR_MECHANISM = register(
    new Item(new FabricItemSettings()),
    "elevator_mechanism"
  );

  public static final Item BLANK_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1)),
    "blank_rune"
  );

  public static final Item CONJUNCTIVIUS_RESPAWN_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1)),
    "conjunctivius_respawn_rune"
  );

  public static final Item CONCIERGE_RESPAWN_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1)),
    "concierge_respawn_rune"
  );

  public static final Item VINE_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1).maxDamage(8)),
    "vine_rune"
  );

  public static final Item RESET_RUNE = register(
    new ResetRuneItem(new FabricItemSettings().maxCount(1)),
    "reset_rune"
  );

  public static final Item GUTS = register(
    new Item(new FabricItemSettings().food(FoodComponents.BEEF)),
    "guts"
  );

  public static final Item MONSTERS_EYE = register(
    new Item(new FabricItemSettings().food(FoodComponents.COOKED_BEEF)),
    "monsters_eye"
  );

  public static final Item SEWAGE_BUCKET = register(
    new BucketItem(MineCellsFluids.STILL_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
    ),
    "sewage_bucket"
  );

  public static final Item ANCIENT_SEWAGE_BUCKET = register(
    new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
    ),
    "ancient_sewage_bucket"
  );

  // Weapons
  public static final ToolMaterial CELL_INFUSED_STEEL_MATERIAL = new ToolMaterial() {
    @Override
    public int getDurability() {
      return 700;
    }

    @Override
    public float getMiningSpeedMultiplier() {
      return 6f;
    }

    @Override
    public float getAttackDamage() {
      return 2f;
    }

    @Override
    public int getMiningLevel() {
      return 2;
    }

    @Override
    public int getEnchantability() {
      return 18;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return Ingredient.ofItems(CELL_INFUSED_STEEL);
    }
  };

  public static final Item ASSASSINS_DAGGER = register(new AssassinsDaggerItem(2, -2.1F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.UNCOMMON)
    ), "assassins_dagger"
  );

  public static final Item BLOOD_SWORD = register(new BloodSwordItem(4, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.UNCOMMON)
    ), "blood_sword"
  );

  public static final Item CURSED_SWORD = register(new CursedSwordItem(22, -3.0F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(600)
    ), "cursed_sword"
  );

  public static final TentacleItem TENTACLE = register(
    new TentacleItem(5, -3.0F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(800)
      .rarity(Rarity.EPIC)
    ), "tentacle"
  );

  public static final Item HATTORIS_KATANA = register(
    new HattorisKatanaItem(4, -2.2F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.RARE)
    ), "hattoris_katana"
  );

  public static final Item BROADSWORD = register(new SwordItem(CELL_INFUSED_STEEL_MATERIAL, 7, -2.9F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1000)
        .rarity(Rarity.COMMON)
    ), "broadsword"
  );

  public static final Item BALANCED_BLADE = register(new BalancedBladeItem(CELL_INFUSED_STEEL_MATERIAL, 2, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.COMMON)
    ), "balanced_blade"
  );

  public static final Item CROWBAR = register(new CrowbarItem(CELL_INFUSED_STEEL_MATERIAL, 3, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1100)
        .rarity(Rarity.COMMON)
    ), "crowbar"
  );

  public static final Item NUTCRACKER = register(new NutcrackerItem(7.0F, -3.0F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1000)
        .rarity(Rarity.COMMON)
    ), "nutcracker"
  );

  public static final Item FROST_BLAST = register(new FrostBlastItem(
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(32)
        .rarity(Rarity.COMMON)
    ), "frost_blast"
  );

  public static final Item FLINT = register(new FlintItem(5, -3.1F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1000)
        .rarity(Rarity.EPIC)
    ), "flint"
  );

  public static final Item SPITE_SWORD = register(new SpiteSwordItem(4, -2.5f,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.UNCOMMON)
    ), "spite_sword"
  );

  // Skills
  public static final PhaserItem PHASER = register(new PhaserItem(
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(32)
        .rarity(Rarity.COMMON)
    ), "phaser"
  );

  public static final Item HEALTH_FLASK = register(
    new HealthFlaskItem(new FabricItemSettings()
      .maxCount(16)
    ), "health_flask"
  );

  //#region Ranged weapons
  // Bows
  public static final CustomBowItem MULTIPLE_NOCKS_BOW = registerBowItem(
    "multiple_nocks_bow", new MultipleNocksBowItem(new FabricItemSettings().maxCount(1).maxDamage(500))
  );
  public static final CustomBowItem BOW_AND_ENDLESS_QUIVER = registerBowItem(
    "bow_and_endless_quiver", new CustomBowItem(new FabricItemSettings().maxCount(1).maxDamage(400), CustomArrowType.ENDLESS)
  );
  public static final CustomBowItem MARKSMANS_BOW = registerBowItem(
    "marksmans_bow", new CustomBowItem(new FabricItemSettings().maxCount(1).maxDamage(450), CustomArrowType.MARKSMAN)
  );
  public static final CustomBowItem INFANTRY_BOW = registerBowItem(
    "infantry_bow", new CustomBowItem(new FabricItemSettings().maxCount(1).maxDamage(450), CustomArrowType.INFANTRY)
  );
  public static final CustomBowItem QUICK_BOW = registerBowItem(
    "quick_bow", new QuickBowItem(new FabricItemSettings().maxCount(1).maxDamage(800))
  );
  public static final CustomBowItem ICE_BOW = registerBowItem(
    "ice_bow", new CustomBowItem(new FabricItemSettings().maxCount(1).maxDamage(400), CustomArrowType.ICE)
  );
  public static final CustomBowItem NERVES_OF_STEEL = registerBowItem(
    "nerves_of_steel", new NervesOfSteelItem(new FabricItemSettings().maxCount(1).maxDamage(450))
  );

  // Crossbows
  public static final CustomBowItem HEAVY_CROSSBOW = registerCrossbowItem(
    "heavy_crossbow", new HeavyCrossbowItem(new FabricItemSettings().maxCount(1).maxDamage(600))
  );
  public static final CustomBowItem EXPLOSIVE_CROSSBOW = registerCrossbowItem(
    "explosive_crossbow", new CustomCrossbowItem(new FabricItemSettings().maxCount(1).maxDamage(500), CustomArrowType.EXPLOSIVE_BOLT)
  );

  // Shields
  public static final CustomShieldItem CUDGEL = registerShield(
    "cudgel", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(500), CustomShieldType.CUDGEL)
  );

  public static final CustomShieldItem RAMPART = registerShield(
    "rampart", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(400), CustomShieldType.RAMPART)
  );

  public static final CustomShieldItem ASSAULT_SHIELD = registerShield(
    "assault_shield", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(450), CustomShieldType.ASSAULT)
  );

  public static final CustomShieldItem BLOODTHIRSTY_SHIELD = registerShield(
    "bloodthirsty_shield", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(500), CustomShieldType.BLOOD)
  );

  public static final CustomShieldItem GREED_SHIELD = registerShield(
    "greed_shield", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(300), CustomShieldType.GREED)
  );

  public static final CustomShieldItem ICE_SHIELD = registerShield(
    "ice_shield", new CustomShieldItem(new FabricItemSettings().maxCount(1).maxDamage(360), CustomShieldType.ICE)
  );

  // Other
  public static final Item ELECTRIC_WHIP = registerOtherRanged(
    "electric_whip", new ElectricWhipItem(new FabricItemSettings().maxCount(1).maxDamage(450))
  );
  public static final Item LIGHTNING_BOLT = registerOtherRanged(
    "lightning_bolt", new LightningBoltItem(new FabricItemSettings().maxCount(1).maxDamage(600))
  );
  public static final Item THROWING_KNIFE = registerOtherRanged(
    "throwing_knife", new SingleUseProjectileItem(new FabricItemSettings(), CustomArrowType.THROWING_KNIFE)
  );
  public static final Item FIREBRANDS = registerOtherRanged(
    "firebrands", new SingleUseProjectileItem(new FabricItemSettings(), CustomArrowType.FIREBRANDS)
  );
  //#endregion

  //#region Simple crafting ingredients
  public static final Item MONSTER_CELL = registerSimpleItem("monster_cell");
  public static final Item BOSS_STEM_CELL = registerSimpleItem("boss_stem_cell");
  public static final Item EXPLOSIVE_BULB = registerSimpleItem("explosive_bulb");
  public static final Item INFECTED_FLESH = register(new Item(
    new FabricItemSettings().food(
      new FoodComponent.Builder()
        .hunger(2)
        .saturationModifier(0.3F)
        .meat()
        .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 1)
        .build()
    )
  ), "infected_flesh");
  public static final Item CELL_INFUSED_STEEL = registerSimpleItem("cell_infused_steel");
  public static final Item METAL_SHARDS = registerSimpleItem("metal_shards");
  public static final Item BUZZCUTTER_FANG = registerSimpleItem("buzzcutter_fang");
  public static final Item MOLTEN_CHUNK = registerSimpleItem("molten_chunk");
  public static final Item SEWER_CALAMARI = register(new Item(
    new FabricItemSettings().food((new FoodComponent.Builder()).hunger(3).saturationModifier(0.3F).meat().build())
  ), "sewer_calamari");
  public static final Item COOKED_SEWER_CALAMARI = register(new Item(
    new FabricItemSettings().food((new FoodComponent.Builder()).hunger(8).saturationModifier(0.8F).meat().build())
  ), "cooked_sewer_calamari");
  public static final Item TRANSPOSITION_CORE = registerSimpleItem("transposition_core");
  public static final Item BLOOD_BOTTLE = registerSimpleItem("blood_bottle");
  public static final Item ARCANE_GOO = registerSimpleItem("arcane_goo");

  public static final Item ICE_ARROW = registerSimpleItem("ice_arrow");
  public static final Item EXPLOSIVE_BOLT = registerSimpleItem("explosive_bolt");
  //#endregion

  static {
    registerDoorwayItem(MineCellsBlocks.PRISON_DOORWAY);
    registerDoorwayItem(MineCellsBlocks.PROMENADE_DOORWAY);
    registerDoorwayItem(MineCellsBlocks.RAMPARTS_DOORWAY);
    registerDoorwayItem(MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY);
    registerDoorwayItem(MineCellsBlocks.BLACK_BRIDGE_DOORWAY);

    registerDimensionalRuneItem(MineCellsBlocks.PRISON_DOORWAY);
    registerDimensionalRuneItem(MineCellsBlocks.PROMENADE_DOORWAY);
    registerDimensionalRuneItem(MineCellsBlocks.RAMPARTS_DOORWAY);
    registerDimensionalRuneItem(MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY);
    registerDimensionalRuneItem(MineCellsBlocks.BLACK_BRIDGE_DOORWAY);
  }

  public static void init() {
    AttackBlockCallback.EVENT.register(
      (player, world, hand, pos, direction) -> {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(CROWBAR) && world.getBlockState(pos).isIn(BlockTags.WOODEN_DOORS)) {
          if (world.getBlockState(pos.down()).isIn(BlockTags.WOODEN_DOORS)) {
            world.breakBlock(pos.down(), false, player);
          }
          world.breakBlock(pos, false, player);
          stack.getOrCreateNbt().putLong("lastDoorBreakTime", world.getTime());
          return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
      }
    );
  }

  public static <E extends Item> E register(E item, String name) {
    Registry.register(Registries.ITEM, MineCells.createId(name), item);
    return item;
  }

  public static Item registerSimpleItem(String name) {
    return register(new Item(new FabricItemSettings()), name);
  }

  public static DoorwayItem registerDoorwayItem(DoorwayPortalBlock doorway) {
    var item = register(
      new DoorwayItem(new FabricItemSettings(), doorway),
      doorway.type.dimension.key.getValue().getPath() + "_doorway"
    );
    DOORWAY_COLORS.put(item, doorway.type.color);
    return item;
  }

  public static DimensionalRuneItem registerDimensionalRuneItem(DoorwayPortalBlock doorway) {
    var item = register(
      new DimensionalRuneItem(new FabricItemSettings(), doorway),
      doorway.type.dimension.key.getValue().getPath() + "_dimensional_rune"
    );
    DIMENSIONAL_RUNES.add(item);
    return item;
  }

  public static CustomBowItem registerBowItem(String name, CustomBowItem bow) {
    var item = register(bow, name);
    BOWS.add(item);
    return item;
  }

  public static CustomCrossbowItem registerCrossbowItem(String name, CustomCrossbowItem crossbow) {
    var item = register(crossbow, name);
    CROSSBOWS.add(item);
    return item;
  }

  public static Item registerOtherRanged(String name, Item item) {
    var registered = register(item, name);
    OTHER_RANGED.add(registered);
    return registered;
  }

  public static CustomShieldItem registerShield(String name, CustomShieldItem item) {
    var registered = register(item, name);
    SHIELDS.add(registered);
    return registered;
  }
}
