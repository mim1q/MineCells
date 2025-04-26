package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.*;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.DoorwayType;
import com.github.mim1q.minecells.block.portal.TeleporterBlock;
import com.github.mim1q.minecells.block.setupblocks.BeamPlacerBlock;
import com.github.mim1q.minecells.block.setupblocks.ElevatorAssemblerBlock;
import com.github.mim1q.minecells.block.setupblocks.MonsterBoxBlock;
import com.github.mim1q.minecells.registry.featureset.*;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MineCellsBlocks {
  private static final List<DyeColor> ORDERED_COLORS = List.of(
    DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK,
    DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW,
    DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE,
    DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK
  );

  public static final List<FlagBlock> FLAG_BLOCKS = new ArrayList<>();
  public static final HashMap<Identifier, DoorwayPortalBlock> DOORWAY_PORTALS = new HashMap<>();

  public static final Block ELEVATOR_ASSEMBLER = registerBlockWithItem(new ElevatorAssemblerBlock(), "elevator_assembler");
  public static final Block CELL_CRAFTER = registerBlockWithItem(new CellCrafterBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).nonOpaque()), "cell_crafter");
  public static final Block UNBREAKABLE_CELL_CRAFTER = registerBlockWithItem(new CellCrafterBlock(AbstractBlock.Settings.copy(CELL_CRAFTER).strength(-1.0F, 3600000.0F)), "unbreakable_cell_crafter");
  public static final Block BIG_CHAIN = registerBlockWithItem(new BigChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN)), "big_chain");
  public static final Block HARDSTONE = registerBlockWithItem(new Block(AbstractBlock.Settings.copy(Blocks.BEDROCK)), "hardstone");
  public static final Block WILTED_GRASS_BLOCK = registerBlockWithItem(new Block(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK).mapColor(MapColor.TEAL)), "wilted_grass_block");
  public static final Block BLOOMROCK_WILTED_GRASS_BLOCK = registerBlockWithItem(new Block(AbstractBlock.Settings.copy(Blocks.GRASS_BLOCK).mapColor(MapColor.TEAL)), "bloomrock_wilted_grass_block");
  public static final ChainBlock UNBREAKABLE_CHAIN = registerBlockWithItem(new ChainBlock(AbstractBlock.Settings.copy(Blocks.CHAIN).strength(-1.0F, 3600000.0F)), "unbreakable_chain");

  // Block sets
  public static final WoodSet PUTRID_WOOD = new WoodSet(MineCells.createId("putrid"), MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.OAK_PLANKS));
  public static final FullStoneSet PRISON_STONE = new FullStoneSet(MineCells.createId("prison_stone"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet PRISON_COBBLESTONE = new StoneSet(MineCells.createId("prison_cobblestone"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.COBBLESTONE));
  public static final StoneSet PRISON_BRICKS = new StoneSet(MineCells.createId("prison_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet SMALL_PRISON_BRICKS = new StoneSet(MineCells.createId("small_prison_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet CRACKED_PRISON_BRICKS = new StoneSet(MineCells.createId("cracked_prison_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet BLOOMROCK = new StoneSet(MineCells.createId("bloomrock"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet BLOOMROCK_BRICKS = new StoneSet(MineCells.createId("bloomrock_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet CRACKED_BLOOMROCK_BRICKS = new StoneSet(MineCells.createId("cracked_bloomrock_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet BLOOMROCK_TILES = new StoneSet(MineCells.createId("bloomrock_tile"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet SEPTITE = new StoneSet(MineCells.createId("septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet SEPTITE_BRICKS = new StoneSet(MineCells.createId("septite_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet SMALL_SEPTITE_BRICKS = new StoneSet(MineCells.createId("small_septite_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet POLISHED_SEPTITE = new StoneSet(MineCells.createId("polished_septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet COBBLED_SEPTITE = new StoneSet(MineCells.createId("cobbled_septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet ANCIENT_SEPTITE = new StoneSet(MineCells.createId("ancient_septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet ANCIENT_SEPTITE_BRICKS = new StoneSet(MineCells.createId("ancient_septite_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet SMALL_ANCIENT_SEPTITE_BRICKS = new StoneSet(MineCells.createId("small_ancient_septite_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE_BRICKS));
  public static final StoneSet POLISHED_ANCIENT_SEPTITE = new StoneSet(MineCells.createId("polished_ancient_septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));
  public static final StoneSet COBBLED_ANCIENT_SEPTITE = new StoneSet(MineCells.createId("cobbled_ancient_septite"), "", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.STONE));

  // Putrid boards
  public static final Block PUTRID_BOARDS = registerBlockWithItem(new WoodenBoardBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).nonOpaque()), "putrid_boards");
  public static final SimpleSet PUTRID_BOARD = new SimpleSet(MineCells.createId("putrid_board"), "_block", MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(Blocks.OAK_PLANKS));
  public static final ArrowSignBlock ARROW_SIGN = registerBlockWithItem(new ArrowSignBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).nonOpaque().noCollision()), "arrow_sign");

  // Leaves
  public static final LeavesSet WILTED_LEAVES = new LeavesSet(
    MineCells.createId("wilted"),
    MineCellsBlocks::defaultItemSettings,
    () -> AbstractBlock.Settings.copy(Blocks.OAK_LEAVES).sounds(BlockSoundGroup.GRASS).nonOpaque().strength(0.2F)
  );
  public static final LeavesSet ORANGE_WILTED_LEAVES = new LeavesSet(MineCells.createId("orange_wilted"), MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(WILTED_LEAVES.leaves).mapColor(MapColor.ORANGE));
  public static final LeavesSet RED_WILTED_LEAVES = new LeavesSet(MineCells.createId("red_wilted"), MineCellsBlocks::defaultItemSettings, () -> AbstractBlock.Settings.copy(WILTED_LEAVES.leaves).mapColor(MapColor.RED));

  public static final SaplingBlock PUTRID_SAPLING = registerSapling("putrid_sapling", "promenade_tree_sapling");
  public static final SaplingBlock ORANGE_PUTRID_SAPLING = registerSapling("orange_putrid_sapling", "orange_promenade_tree_sapling");
  public static final SaplingBlock RED_PUTRID_SAPLING = registerSapling("red_putrid_sapling", "red_promenade_tree_sapling");

  // ----------------------------

  public static final Block RUNIC_VINE = registerBlock(new RunicVineBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)), "runic_vine");
  public static final Block RUNIC_VINE_PLANT = registerBlock(new RunicVinePlantBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).sounds(BlockSoundGroup.WET_GRASS).luminance(s -> 8).nonOpaque().ticksRandomly().solid()), "runic_vine_plant");
  public static final Block RUNIC_VINE_STONE = registerBlock(new Block(AbstractBlock.Settings.copy(Blocks.BEDROCK)), "runic_vine_stone");

  // Shockwaves

  public static final Block SHOCKWAVE_FLAME = registerBlock(new ShockwaveBlock.ShockwaveFlame(
    AbstractBlock.Settings
      .copy(Blocks.FIRE)
      .luminance(s -> 0)
      .emissiveLighting(Blocks::always)
      .noBlockBreakParticles()
      .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY),
    false
  ), "shockwave_flame");

  public static final Block SHOCKWAVE_FLAME_PLAYER = registerBlock(new ShockwaveBlock.ShockwaveFlame(
    AbstractBlock.Settings.copy(SHOCKWAVE_FLAME),
    true
  ), "shockwave_flame_player");

  // Decoration - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public static final Block CRATE = registerBlockWithItem(new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(1.0F)), "crate");
  public static final Block CHAIN_PILE_BLOCK = registerBlockWithItem(new Block(AbstractBlock.Settings.copy(Blocks.CHAIN)), "chain_pile_block");
  public static final Block CHAIN_PILE = registerBlockWithItem(new GroundDecorationBlock(AbstractBlock.Settings.copy(Blocks.CHAIN), GroundDecorationBlock.Shape.PILE), "chain_pile");
  public static final Block SMALL_CRATE = registerBlockWithItem(new SmallCrateBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(1.0F)), "small_crate");
  public static final Block BRITTLE_BARREL = registerBlockWithItem(new GroundDecorationBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(1.0F), GroundDecorationBlock.Shape.BARREL), "brittle_barrel");
  public static final Block CAGE = registerBlockWithItem(new CageBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS), false), "cage");
  public static final Block BROKEN_CAGE = registerBlockWithItem(new CageBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS), true), "broken_cage");
  public static final Block SPIKES = registerBlockWithItem(new SpikesBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).solid()), "spikes");
  public static final Block HANGED_SKELETON = registerBlock(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(Blocks.DIRT).noCollision().strength(0.5F).sounds(BlockSoundGroup.BONE)), "hanged_skeleton");
  public static final Block SKELETON = registerBlockWithItem(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(HANGED_SKELETON).dropsLike(HANGED_SKELETON), HANGED_SKELETON), "skeleton");
  public static final Block HANGED_CORPSE = registerBlock(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(Blocks.DIRT).noCollision().strength(0.5F).sounds(BlockSoundGroup.MUD).ticksRandomly()), "hanged_corpse");
  public static final Block CORPSE = registerBlockWithItem(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(HANGED_CORPSE).dropsLike(HANGED_CORPSE).ticksRandomly(), HANGED_CORPSE), "corpse");
  public static final Block HANGED_ROTTING_CORPSE = registerBlock(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(HANGED_CORPSE).ticksRandomly()), "hanged_rotting_corpse");
  public static final Block ROTTING_CORPSE = registerBlockWithItem(new SkeletonDecorationBlock(AbstractBlock.Settings.copy(HANGED_CORPSE).dropsLike(HANGED_ROTTING_CORPSE).ticksRandomly(), HANGED_ROTTING_CORPSE), "rotting_corpse");
  public static final Block KING_STATUE = registerBlockWithItem(new DecorativeStatueBlock(AbstractBlock.Settings.create().solid().nonOpaque().noCollision().hardness(5.0F)), "king_statue");
  public static final Block FLAG_POLE = registerBlockWithItem(new FlagPoleBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)), "flag_pole");
  public static final Block ALCHEMY_EQUIPMENT_0 = registerBlockWithItem(new AlchemyEquipmentBlock(AbstractBlock.Settings.copy(Blocks.GLASS).offset(AbstractBlock.OffsetType.XZ)), "alchemy_equipment_0");
  public static final Block ALCHEMY_EQUIPMENT_1 = registerBlockWithItem(new AlchemyEquipmentBlock(AbstractBlock.Settings.copy(Blocks.GLASS)), "alchemy_equipment_1");
  public static final Block ALCHEMY_EQUIPMENT_2 = registerBlockWithItem(new AlchemyEquipmentBlock(AbstractBlock.Settings.copy(Blocks.GLASS).offset(AbstractBlock.OffsetType.XZ)), "alchemy_equipment_2");
  public static final ColoredTorchBlock PRISON_TORCH = registerBlockWithItem(new ColoredTorchBlock(AbstractBlock.Settings.copy(Blocks.TORCH).breakInstantly().luminance(s -> 15).emissiveLighting((s, w, p) -> true).ticksRandomly().noCollision()), "prison_torch");
  public static final ColoredTorchBlock PROMENADE_TORCH = registerBlockWithItem(new ColoredTorchBlock(AbstractBlock.Settings.copy(PRISON_TORCH)), "promenade_torch");
  public static final ColoredTorchBlock RAMPARTS_TORCH = registerBlockWithItem(new ColoredTorchBlock(AbstractBlock.Settings.copy(PRISON_TORCH)), "ramparts_torch");
  public static final ColoredTorchBlock SEWERS_TORCH = registerBlockWithItem(new ColoredTorchBlock(AbstractBlock.Settings.copy(PRISON_TORCH)), "sewers_torch");
  public static final ColoredTorchBlock ANCIENT_SEWERS_TORCH = registerBlockWithItem(new ColoredTorchBlock(AbstractBlock.Settings.copy(PRISON_TORCH)), "ancient_sewers_torch");
  public static final ReturnStoneBlock RETURN_STONE = registerBlockWithItem(new ReturnStoneBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).luminance(s -> 7)), "return_stone");
  public static final Block KINGDOM_PORTAL_CORE = registerBlock(new Block(AbstractBlock.Settings.copy(Blocks.DIRT)), "kingdom_portal_core");
  public static final Block CONJUNCTIVIUS_BOX = registerBlock(new MonsterBoxBlock(MineCells.createId("boss/conjunctivius")), "conjunctivius_box");
  public static final Block CONCIERGE_BOX = registerBlock(new MonsterBoxBlock(MineCells.createId("boss/concierge")), "concierge_box");
  public static final Block BEAM_PLACER = registerBlock(new BeamPlacerBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK)), "beam_placer");
  public static final FluidBlock SEWAGE = new FluidBlock(MineCellsFluids.STILL_SEWAGE, AbstractBlock.Settings.copy(Blocks.WATER));
  public static final FluidBlock ANCIENT_SEWAGE = new FluidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, AbstractBlock.Settings.copy(Blocks.WATER));

  // Flags
  public static final FlagBlock KINGS_CREST_FLAG = registerFlag("kings_crest", false);
  public static final FlagBlock TORN_KINGS_CREST_FLAG = registerFlag("torn_kings_crest", false);
  public static final FlagBlock PROMENADE_OF_THE_CONDEMNED_FLAG = registerFlag("promenade_of_the_condemned", false);
  public static final FlagBlock RAMPARTS_FLAG = registerFlag("ramparts", false);
  public static final FlagBlock INSUFFERABLE_CRYPT_FLAG = registerFlag("insufferable_crypt", false);
  public static final FlagBlock BLACK_BRIDGE_FLAG = registerFlag("black_bridge", false);

  public static final Map<DyeColor, FlagBlock> RIBBON_FLAGS = ORDERED_COLORS.stream()
    .collect(Collectors.toMap(Function.identity(), it -> registerFlag(it.getName() + "_ribbon", false)));

  public static final Map<DyeColor, FlagBlock> LARGE_RIBBON_FLAGS = ORDERED_COLORS.stream()
    .collect(Collectors.toMap(Function.identity(), it -> registerFlag("large_" + it.getName() + "_ribbon", true)));

  public static final SpawnerRuneBlock SPAWNER_RUNE = registerBlock(new SpawnerRuneBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).noCollision().nonOpaque()), "spawner_rune");

  // Barriers
  public static final Block BARRIER_RUNE = registerBlockWithItem(new BarrierRuneBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).noCollision(), false), "barrier_rune");
  public static final Block SOLID_BARRIER = registerBlockWithItem(new BarrierRuneBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK), true), "solid_barrier_rune");
  public static final Block CONDITIONAL_BARRIER = registerBlock(new ConditionalBarrierBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK)), "conditional_barrier");
  public static final Block BOSS_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).solid().noCollision().nonOpaque(), BarrierControllerBlock::bossPredicate), "boss_barrier_controller");
  public static final Block BOSS_ENTRY_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(AbstractBlock.Settings.copy(BOSS_BARRIER_CONTROLLER), BarrierControllerBlock::bossEntryPredicate), "boss_entry_barrier_controller");
  public static final Block PLAYER_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(AbstractBlock.Settings.copy(BOSS_BARRIER_CONTROLLER), BarrierControllerBlock::playerPredicate), "player_barrier_controller");

  // Portals
  public static final TeleporterBlock TELEPORTER_CORE = registerBlock(new TeleporterBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).noCollision()), "teleporter_core");
  public static final TeleporterBlock.Filler TELEPORTER_FRAME = registerBlock(new TeleporterBlock.Filler(AbstractBlock.Settings.copy(Blocks.BEDROCK)), "teleporter_frame");
  public static final DoorwayPortalBlock.Frame DOORWAY_FRAME = registerBlock(new DoorwayPortalBlock.Frame(AbstractBlock.Settings.copy(Blocks.NETHER_PORTAL).nonOpaque().strength(10F, 1200F).pistonBehavior(PistonBehavior.BLOCK)), "doorway_frame");
  public static final DoorwayPortalBlock.Frame UNBREAKABLE_DOORWAY_FRAME = registerBlock(new DoorwayPortalBlock.Frame(AbstractBlock.Settings.copy(DOORWAY_FRAME).strength(-1.0F, 3600000.0F).solid()), "unbreakable_doorway_frame");
  public static final RiftBlock RIFT = registerBlock(new RiftBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).solid()), "rift");

  public static final DoorwayPortalBlock OVERWORLD_DOORWAY = registerDoorway(DoorwayType.OVERWORLD, "overworld_doorway");
  public static final DoorwayPortalBlock PRISON_DOORWAY = registerDoorway(DoorwayType.PRISON, "prison_doorway");
  public static final DoorwayPortalBlock PROMENADE_DOORWAY = registerDoorway(DoorwayType.PROMENADE, "promenade_doorway");
  public static final DoorwayPortalBlock INSUFFERABLE_CRYPT_DOORWAY = registerDoorway(DoorwayType.INSUFFERABLE_CRYPT, "insufferable_crypt_doorway");
  public static final DoorwayPortalBlock RAMPARTS_DOORWAY = registerDoorway(DoorwayType.RAMPARTS, "ramparts_doorway");
  public static final DoorwayPortalBlock BLACK_BRIDGE_DOORWAY = registerDoorway(DoorwayType.BLACK_BRIDGE, "black_bridge_doorway");

  public static void init() {
    Registry.register(Registries.BLOCK, MineCells.createId("sewage"), SEWAGE);
    Registry.register(Registries.BLOCK, MineCells.createId("ancient_sewage"), ANCIENT_SEWAGE);
  }

  public static <T extends Block> T registerBlock(T block, String id) {
    Registry.register(Registries.BLOCK, MineCells.createId(id), block);
    return block;
  }

  public static <T extends Block> T registerBlockWithItem(T block, String id) {
    registerBlock(block, id);
    Registry.register(
      Registries.ITEM,
      MineCells.createId(id),
      new BlockItem(block, new Item.Settings())
    );
    return block;
  }

  private static SaplingBlock registerSapling(String id, String key) {
    var actualKey = "sapling/" + key;
    var registryKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, MineCells.createId(actualKey));
    return registerBlockWithItem(new SaplingBlock(
      new SaplingGenerator(
        registryKey.getValue().toString(),
        Optional.empty(),
        Optional.of(registryKey),
        Optional.empty()
      ),
      AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)
    ), id);
  }

  private static FlagBlock registerFlag(String name, boolean large) {
    var flag = registerBlockWithItem(
      new FlagBlock((preventZFighting(AbstractBlock.Settings.copy(Blocks.WHITE_BANNER))), name, large),
      name + "_flag"
    );
    FLAG_BLOCKS.add(flag);
    return flag;
  }

  private static DoorwayPortalBlock registerDoorway(DoorwayType type, String name) {
    var doorway = registerBlock(new DoorwayPortalBlock(AbstractBlock.Settings.copy(UNBREAKABLE_DOORWAY_FRAME), type), name);
    DOORWAY_PORTALS.put(type.dimension.key.getValue(), doorway);
    return doorway;
  }

  @SuppressWarnings("UnstableApiUsage")
  private static AbstractBlock.Settings preventZFighting(AbstractBlock.Settings settings) {
    ((AbstractBlockSettingsAccessor) settings).setOffsetter((state, world, pos) -> {
      var x = pos.getX() % 3;
      var y = pos.getY() % 3;
      var z = pos.getZ() % 3;
      return (new Vec3d(
        (z * 0.001) + (y * 0.0015),
        (x * 0.001) + (z * 0.0015),
        (y * 0.001) + (x * 0.0015)
      ));
    });

    return settings;
  }

  @SuppressWarnings({"UnstableApiUsage", "deprecation"})
  public static AbstractBlock.Settings wallLeafOffset(AbstractBlock.Settings settings) {
    ((AbstractBlockSettingsAccessor) settings).setOffsetter((state, world, pos) -> {
      long l = MathHelper.hashCode(pos.getX(), pos.getY(), pos.getZ());
      var offset = 0.3f;
      var localZScale = 0.2f;
      double x = MathHelper.clamp((((l & 15L) / 15.0F) - 0.5) * 0.5, -offset, offset);
      double y = MathHelper.clamp((((l >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (-offset), offset);
      double z = MathHelper.clamp((((l >> 4 & 15L) / 15.0F) - 0.5) * 0.5, (-offset), offset);

      var direction = state.get(WallLeavesBlock.DIRECTION);

      if (direction.getOffsetX() != 0) x = localZScale * Math.abs(x) * -direction.getOffsetX();
      if (direction.getOffsetY() != 0) y = localZScale * Math.abs(y) * -direction.getOffsetY();
      if (direction.getOffsetZ() != 0) z = localZScale * Math.abs(z) * -direction.getOffsetZ();

      return new Vec3d(x, y, z);
    });

    return settings;
  }

  private static Item.Settings defaultItemSettings() {
    return new Item.Settings();
  }
}
