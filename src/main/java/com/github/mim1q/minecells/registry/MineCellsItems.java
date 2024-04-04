package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.item.DimensionalRuneItem;
import com.github.mim1q.minecells.item.DoorwayItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.ResetRuneItem;
import com.github.mim1q.minecells.item.skill.PhaserItem;
import com.github.mim1q.minecells.item.weapon.*;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
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

  public static final Item BROADSWORD = register(new SwordItem(ToolMaterials.IRON, 7, -2.9F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1000)
        .rarity(Rarity.COMMON)
    ), "broadsword"
  );

  public static final Item BALANCED_BLADE = register(new BalancedBladeItem(ToolMaterials.IRON, 2, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.COMMON)
    ), "balanced_blade"
  );

  public static final Item CROWBAR = register(new CrowbarItem(ToolMaterials.IRON, 3, -2.4F,
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

  // Bows
  public static final CustomBowItem MULTIPLE_NOCKS_BOW = registerBowItem("multiple_nocks_bow", CustomArrowType.DEFAULT);
  public static final CustomBowItem BOW_AND_ENDLESS_QUIVER = registerBowItem("bow_and_endless_quiver", CustomArrowType.DEFAULT);
  public static final CustomBowItem MARKSMANS_BOW = registerBowItem("marksmans_bow", CustomArrowType.MARKSMAN);
  public static final CustomBowItem INFANTRY_BOW = registerBowItem("infantry_bow", CustomArrowType.INFANTRY);
  public static final CustomBowItem QUICK_BOW = registerBowItem("quick_bow", CustomArrowType.DEFAULT);
  public static final CustomBowItem ICE_BOW = registerBowItem("ice_bow", CustomArrowType.ICE);
  public static final CustomBowItem HEAVY_CROSSBOW = registerBowItem("heavy_crossbow", CustomArrowType.DEFAULT);
  public static final CustomBowItem NERVES_OF_STEEL = registerBowItem("nerves_of_steel", CustomArrowType.DEFAULT);
  public static final CustomBowItem EXPLOSIVE_CROSSBOW = registerBowItem("explosive_crossbow", CustomArrowType.EXPLOSIVE_BOLT);

  // Simple crafting ingredients

  public static final Item MONSTER_CELL = registerCraftingIngredient("monster_cell");
  public static final Item BOSS_STEM_CELL = registerCraftingIngredient("boss_stem_cell");

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

  public static Item registerCraftingIngredient(String name) {
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

  public static CustomBowItem registerBowItem(String name, CustomArrowType arrowType) {
    var item = register(new CustomBowItem(new FabricItemSettings().maxCount(1), arrowType), name);
    BOWS.add(item);
    return item;
  }
}
