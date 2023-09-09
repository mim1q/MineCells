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
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MineCellsBlocks {
  public static final Block ELEVATOR_ASSEMBLER = registerBlockWithItem(new ElevatorAssemblerBlock(), "elevator_assembler");
  public static final Block CELL_FORGE = registerBlockWithItem(new CellForgeBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)), "cell_forge");
  public static final Block BIG_CHAIN = registerBlockWithItem(new BigChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN)), "big_chain");
  public static final Block HARDSTONE = registerBlockWithItem(new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK)), "hardstone");
  public static final Block WILTED_GRASS_BLOCK = registerBlockWithItem(new Block(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).mapColor(MapColor.TEAL)), "wilted_grass_block");

  // Block sets
  public static final WoodSet PUTRID_WOOD = new WoodSet(MineCells.createId("putrid"), MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
  public static final FullStoneSet PRISON_STONE = new FullStoneSet(MineCells.createId("prison_stone"), "", MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.STONE));
  public static final StoneSet PRISON_COBBLESTONE = new StoneSet(MineCells.createId("prison_cobblestone"), "", MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.COBBLESTONE));
  public static final StoneSet PRISON_BRICKS = new StoneSet(MineCells.createId("prison_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.STONE_BRICKS));
  public static final StoneSet SMALL_PRISON_BRICKS = new StoneSet(MineCells.createId("small_prison_brick"), "s", MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.STONE_BRICKS));

  // Putrid boards
  public static final Block PUTRID_BOARDS = registerBlockWithItem(new WoodenBoardBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).nonOpaque()), "putrid_boards");
  public static final SimpleSet PUTRID_BOARD = new SimpleSet(MineCells.createId("putrid_board"), "_block", MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));

  // Leaves
  public static final LeavesSet WILTED_LEAVES = new LeavesSet(
    MineCells.createId("wilted"),
    MineCellsBlocks::defaultItemSettings,
    () -> FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).sounds(BlockSoundGroup.GRASS).nonOpaque().strength(0.2F)
  );
  public static final LeavesSet ORANGE_WILTED_LEAVES = new LeavesSet(MineCells.createId("orange_wilted"), MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(WILTED_LEAVES.leaves).mapColor(MapColor.ORANGE));
  public static final LeavesSet RED_WILTED_LEAVES = new LeavesSet(MineCells.createId("red_wilted"), MineCellsBlocks::defaultItemSettings, () -> FabricBlockSettings.copyOf(WILTED_LEAVES.leaves).mapColor(MapColor.RED));

  public static final SaplingBlock RED_PUTRID_SAPLING = registerSapling("red_putrid_sapling", "promenade_tree_sapling");

  // ----------------------------

  public static final Block RUNIC_VINE = registerBlock(new RunicVineBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)), "runic_vine");
  public static final Block RUNIC_VINE_PLANT = registerBlock(new RunicVinePlantBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).sounds(BlockSoundGroup.WET_GRASS).luminance(8).nonOpaque().ticksRandomly()), "runic_vine_plant");
  public static final Block RUNIC_VINE_STONE = registerBlock(new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK)), "runic_vine_stone");

  // Decoration - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public static final Block CRATE = registerBlockWithItem(new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F)), "crate");
  public static final Block CHAIN_PILE_BLOCK = registerBlockWithItem(new Block(FabricBlockSettings.copyOf(Blocks.CHAIN)), "chain_pile_block");
  public static final Block CHAIN_PILE = registerBlockWithItem(new GroundDecoration(FabricBlockSettings.copyOf(Blocks.CHAIN), GroundDecoration.Shape.PILE), "chain_pile");
  public static final Block SMALL_CRATE = registerBlockWithItem(new GroundDecoration(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F), GroundDecoration.Shape.BLOCK_12), "small_crate");
  public static final Block BRITTLE_BARREL = registerBlockWithItem(new GroundDecoration(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F), GroundDecoration.Shape.BARREL), "brittle_barrel");
  public static final Block CAGE = registerBlockWithItem(new CageBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS), false), "cage");
  public static final Block BROKEN_CAGE = registerBlockWithItem(new CageBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS), true), "broken_cage");
  public static final Block SPIKES = registerBlockWithItem(new SpikesBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS)), "spikes");
  public static final Block HANGED_SKELETON = registerBlock(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(Blocks.DIRT).strength(0.5F).sounds(BlockSoundGroup.BONE)), "hanged_skeleton");
  public static final Block SKELETON = registerBlockWithItem(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_SKELETON).dropsLike(HANGED_SKELETON), HANGED_SKELETON), "skeleton");
  public static final Block HANGED_CORPSE = registerBlock(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(Blocks.DIRT).strength(0.5F).sounds(BlockSoundGroup.MUD).ticksRandomly()), "hanged_corpse");
  public static final Block CORPSE = registerBlockWithItem(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).dropsLike(HANGED_CORPSE).ticksRandomly(), HANGED_CORPSE), "corpse");
  public static final Block HANGED_ROTTING_CORPSE = registerBlock(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).ticksRandomly()), "hanged_rotting_corpse");
  public static final Block ROTTING_CORPSE = registerBlockWithItem(new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).dropsLike(HANGED_ROTTING_CORPSE).ticksRandomly(), HANGED_ROTTING_CORPSE), "rotting_corpse");
  public static final Block KING_STATUE = registerBlockWithItem(new DecorativeStatueBlock(FabricBlockSettings.create().solid().nonOpaque().noCollision().hardness(5.0F)), "king_statue");
  public static final Block FLAG_POLE = registerBlockWithItem(new FlagPoleBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)), "flag_pole");
  public static final Block BIOME_BANNER = registerBlock(new BiomeBannerBlock(FabricBlockSettings.copyOf(Blocks.WHITE_BANNER)), "biome_banner");
  public static final Block ALCHEMY_EQUIPMENT_0 = registerBlockWithItem(new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS).offset(AbstractBlock.OffsetType.XZ)), "alchemy_equipment_0");
  public static final Block ALCHEMY_EQUIPMENT_1 = registerBlockWithItem(new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS)), "alchemy_equipment_1");
  public static final Block ALCHEMY_EQUIPMENT_2 = registerBlockWithItem(new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS).offset(AbstractBlock.OffsetType.XZ)), "alchemy_equipment_2");
  public static final ColoredTorchBlock PRISON_TORCH = registerBlockWithItem(new ColoredTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH).breakInstantly().luminance(15).emissiveLighting((s, w, p) -> true).ticksRandomly().noCollision()), "prison_torch");
  public static final ColoredTorchBlock PROMENADE_TORCH = registerBlockWithItem(new ColoredTorchBlock(FabricBlockSettings.copyOf(PRISON_TORCH)), "promenade_torch");
  public static final ReturnStoneBlock RETURN_STONE = registerBlockWithItem(new ReturnStoneBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).luminance(7)), "return_stone");
  public static final Block KINGDOM_PORTAL_CORE = registerBlock(new Block(FabricBlockSettings.copyOf(Blocks.DIRT)), "kingdom_portal_core");
  public static final Block CONJUNCTIVIUS_BOX = registerBlock(new MonsterBoxBlock(MineCellsEntities.CONJUNCTIVIUS), "conjunctivius_box");
  public static final Block BEAM_PLACER = registerBlock(new BeamPlacerBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK)), "beam_placer");
  public static final FluidBlock SEWAGE = new FluidBlock(MineCellsFluids.STILL_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));
  public static final FluidBlock ANCIENT_SEWAGE = new FluidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));

  // Barriers
  public static final Block BARRIER_RUNE = registerBlockWithItem(new BarrierRuneBlock(FabricBlockSettings.copyOf(Blocks.BARRIER).noCollision(), false), "barrier_rune");
  public static final Block SOLID_BARRIER = registerBlock(new BarrierRuneBlock(FabricBlockSettings.copyOf(Blocks.BARRIER), true), "solid_barrier_rune");
  public static final Block CONDITIONAL_BARRIER = registerBlock(new ConditionalBarrierBlock(FabricBlockSettings.copyOf(Blocks.BARRIER)), "conditional_barrier");
  public static final Block BOSS_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(FabricBlockSettings.copyOf(BARRIER_RUNE), BarrierControllerBlock::bossPredicate), "boss_barrier_controller");
  public static final Block BOSS_ENTRY_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(FabricBlockSettings.copyOf(BARRIER_RUNE), BarrierControllerBlock::bossEntryPredicate), "boss_entry_barrier_controller");
  public static final Block PLAYER_BARRIER_CONTROLLER = registerBlock(new BarrierControllerBlock(FabricBlockSettings.copyOf(BARRIER_RUNE), BarrierControllerBlock::playerPredicate), "player_barrier_controller");

  // Portals
  public static final TeleporterBlock TELEPORTER_CORE = registerBlock(new TeleporterBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).noCollision()), "teleporter_core");
  public static final TeleporterBlock.Filler TELEPORTER_FRAME = registerBlock(new TeleporterBlock.Filler(FabricBlockSettings.copyOf(Blocks.BEDROCK)), "teleporter_frame");
  public static final DoorwayPortalBlock.Frame DOORWAY_FRAME = registerBlock(new DoorwayPortalBlock.Frame(FabricBlockSettings.create().nonOpaque().strength(50F, 1200F).pistonBehavior(PistonBehavior.BLOCK)), "doorway_frame");
  public static final DoorwayPortalBlock OVERWORLD_DOORWAY = registerBlock(new DoorwayPortalBlock(FabricBlockSettings.copyOf(DOORWAY_FRAME).strength(-1.0F, 3600000.0F), DoorwayType.OVERWORLD), "overworld_doorway");
  public static final DoorwayPortalBlock PRISON_DOORWAY = registerBlock(new DoorwayPortalBlock(FabricBlockSettings.copyOf(OVERWORLD_DOORWAY), DoorwayType.PRISON), "prison_doorway");
  public static final DoorwayPortalBlock PROMENADE_DOORWAY = registerBlock(new DoorwayPortalBlock(FabricBlockSettings.copyOf(OVERWORLD_DOORWAY), DoorwayType.PROMENADE), "promenade_doorway");
  public static final DoorwayPortalBlock INSUFFERABLE_CRYPT_DOORWAY = registerBlock(new DoorwayPortalBlock(FabricBlockSettings.copyOf(OVERWORLD_DOORWAY), DoorwayType.INSUFFERABLE_CRYPT), "insufferable_crypt_doorway");

  public static void init() {
    Registry.register(Registries.BLOCK, MineCells.createId("sewage"), SEWAGE);
    Registry.register(Registries.BLOCK, MineCells.createId("ancient_sewage"), ANCIENT_SEWAGE);
  }

  public static  <T extends Block> T registerBlock(T block, String id) {
    Registry.register(Registries.BLOCK, MineCells.createId(id), block);
    return block;
  }

  public static <T extends Block> T registerBlockWithItem(T block, String id) {
    registerBlock(block, id);
    Registry.register(
      Registries.ITEM,
      MineCells.createId(id), 
      new BlockItem(block, new FabricItemSettings())
    );
    return block;
  }

  private static SaplingBlock registerSapling(String id, String key) {
    var registryKey = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, MineCells.createId(key));
    return registerBlockWithItem(new SaplingBlock(
      new SaplingGenerator() {
        @Override
        protected RegistryKey<ConfiguredFeature<?, ?>> getTreeFeature(Random random, boolean bees) {
          return registryKey;
        }
      },
      FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)
    ), id);
  }

  private static FabricItemSettings defaultItemSettings() {
    return new FabricItemSettings();
  }
}
