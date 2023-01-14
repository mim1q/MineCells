package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.*;
import com.github.mim1q.minecells.block.setupblocks.ElevatorAssemblerBlock;
import com.github.mim1q.minecells.block.setupblocks.MonsterBoxBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class MineCellsBlocks {
  public static final Block ELEVATOR_ASSEMBLER = registerBlockWithItem(
    new ElevatorAssemblerBlock(),
    "elevator_assembler"
  );

  public static final Block CELL_FORGE = registerBlockWithItem(
    new CellForgeBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)),
    "cell_forge"
  );

  public static final Block BIG_CHAIN = registerBlockWithItem(
    new BigChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN)),
    "big_chain"
  );

  public static final Block HARDSTONE = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK)),
    "hardstone"
  );

  public static final Block PRISON_STONE = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.STONE)),
    "prison_stone"
  );

  public static final Block PRISON_STONE_SLAB = registerBlockWithItem(
    new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE_SLAB)),
    "prison_stone_slab"
  );

  public static final Block PRISON_STONE_STAIRS = registerBlockWithItem(
    new StairsBlock(PRISON_STONE.getDefaultState(), FabricBlockSettings.copyOf(Blocks.STONE_STAIRS)),
    "prison_stone_stairs"
  );

  public static final Block PRISON_STONE_WALL = registerBlockWithItem(
    new WallBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICK_WALL)),
    "prison_stone_wall"
  );

  public static final Block PRISON_COBBLESTONE = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.COBBLESTONE)),
    "prison_cobblestone"
  );

  public static final Block PRISON_COBBLESTONE_SLAB = registerBlockWithItem(
    new SlabBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE_SLAB)),
    "prison_cobblestone_slab"
  );

  public static final Block PRISON_COBBLESTONE_STAIRS = registerBlockWithItem(
    new StairsBlock(PRISON_COBBLESTONE.getDefaultState(), FabricBlockSettings.copyOf(Blocks.COBBLESTONE_STAIRS)),
    "prison_cobblestone_stairs"
  );

  public static final Block PRISON_COBBLESTONE_WALL = registerBlockWithItem(
    new WallBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE_WALL)),
    "prison_cobblestone_wall"
  );

  public static final Block PRISON_BRICKS = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)),
    "prison_bricks"
  );

  public static final Block PRISON_BRICK_SLAB = registerBlockWithItem(
    new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICK_SLAB)),
    "prison_brick_slab"
  );

  public static final Block PRISON_BRICK_STAIRS = registerBlockWithItem(
    new StairsBlock(PRISON_BRICKS.getDefaultState(), FabricBlockSettings.copyOf(Blocks.STONE_BRICK_STAIRS)),
    "prison_brick_stairs"
  );

  public static final Block PRISON_BRICK_WALL = registerBlockWithItem(
    new WallBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICK_WALL)),
    "prison_brick_wall"
  );

  public static final Block SMALL_PRISON_BRICKS = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS)),
    "small_prison_bricks"
  );

  public static final Block SMALL_PRISON_BRICK_SLAB = registerBlockWithItem(
    new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICK_SLAB)),
    "small_prison_brick_slab"
  );

  public static final Block SMALL_PRISON_BRICK_STAIRS = registerBlockWithItem(
    new StairsBlock(SMALL_PRISON_BRICKS.getDefaultState(), FabricBlockSettings.copyOf(Blocks.STONE_BRICK_STAIRS)),
    "small_prison_brick_stairs"
  );

  public static final Block SMALL_PRISON_BRICK_WALL = registerBlockWithItem(
    new WallBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICK_WALL)),
    "small_prison_brick_wall"
  );

  public static final Block WILTED_GRASS_BLOCK = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK)),
    "wilted_grass_block"
  );

  public static final Block PUTRID_LOG = registerBlockWithItem(
    new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG)),
    "putrid_log"
  );

  public static final Block STRIPPED_PUTRID_LOG = registerBlockWithItem(
    new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG)),
    "stripped_putrid_log"
  );

  public static final Block PUTRID_WOOD = registerBlockWithItem(
    new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)),
    "putrid_wood"
  );

  public static final Block STRIPPED_PUTRID_WOOD = registerBlockWithItem(
    new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_WOOD)),
    "stripped_putrid_wood"
  );

  public static final Block PUTRID_PLANKS = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)),
    "putrid_planks"
  );

  public static final Block PUTRID_STAIRS = registerBlockWithItem(
    new StairsBlock(PUTRID_PLANKS.getDefaultState(), FabricBlockSettings.copyOf(Blocks.OAK_STAIRS)),
    "putrid_stairs"
  );

  public static final Block PUTRID_SLAB = registerBlockWithItem(
    new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB)),
    "putrid_slab"
  );

  public static final Block PUTRID_FENCE = registerBlockWithItem(
    new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE)),
    "putrid_fence"
  );

  public static final Block PUTRID_FENCE_GATE = registerBlockWithItem(
    new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE)),
    "putrid_fence_gate"
  );

  public static final Block PUTRID_DOOR = registerBlockWithItem(
    new DoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_DOOR)),
    "putrid_door"
  );

  public static final Block PUTRID_TRAPDOOR = registerBlockWithItem(
    new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_TRAPDOOR)),
    "putrid_trapdoor"
  );

  public static final Block PUTRID_BOARDS = registerBlockWithItem(
    new WoodenBoardBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)),
    "putrid_boards"
  );

  // Leaves - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public static final Block WILTED_LEAVES = registerBlockWithItem(
    new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)),
    "wilted_leaves"
  );

  public static final Block WILTED_HANGING_LEAVES = registerBlockWithItem(
    new HangingLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "wilted_hanging_leaves"
  );

  public static final Block WILTED_WALL_LEAVES = registerBlockWithItem(
    new WallLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "wilted_wall_leaves"
  );

  public static final Block ORANGE_WILTED_LEAVES = registerBlockWithItem(
    new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)),
    "orange_wilted_leaves"
  );

  public static final Block ORANGE_WILTED_HANGING_LEAVES = registerBlockWithItem(
    new HangingLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "orange_wilted_hanging_leaves"
  );

  public static final Block ORANGE_WILTED_WALL_LEAVES = registerBlockWithItem(
    new WallLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "orange_wilted_wall_leaves"
  );

  public static final Block RED_WILTED_LEAVES = registerBlockWithItem(
    new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)),
    "red_wilted_leaves"
  );

  public static final Block RED_WILTED_HANGING_LEAVES = registerBlockWithItem(
    new HangingLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "red_wilted_hanging_leaves"
  );

  public static final Block RED_WILTED_WALL_LEAVES = registerBlockWithItem(
    new WallLeavesBlock(FabricBlockSettings.copyOf(Blocks.GRASS)),
    "red_wilted_wall_leaves"
  );

  // Decoration - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public static final Block CRATE = registerBlockWithItem(
    new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F)),
    "crate"
  );
  public static final Block CHAIN_PILE_BLOCK = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.CHAIN)),
    "chain_pile_block"
  );
  public static final Block CHAIN_PILE = registerBlockWithItem(
    new GroundDecoration(FabricBlockSettings.copyOf(Blocks.CHAIN), GroundDecoration.Shape.PILE),
    "chain_pile"
  );

  public static final Block SMALL_CRATE = registerBlockWithItem(
    new GroundDecoration(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F), GroundDecoration.Shape.BLOCK_12),
    "small_crate"
  );

  public static final Block BRITTLE_BARREL = registerBlockWithItem(
    new GroundDecoration(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0F), GroundDecoration.Shape.BARREL),
    "brittle_barrel"
  );

  public static final Block CAGE = registerBlockWithItem(
    new CageBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS), false),
    "cage"
  );

  public static final Block BROKEN_CAGE = registerBlockWithItem(
    new CageBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS), true),
    "broken_cage"
  );

  public static final Block SPIKES = registerBlockWithItem(
    new SpikesBlock(FabricBlockSettings.copyOf(Blocks.IRON_BARS)),
    "spikes"
  );

  public static final Block HANGED_SKELETON = registerBlock(
    new SkeletonDecorationBlock(FabricBlockSettings.of(Material.STONE).strength(0.5F).sounds(BlockSoundGroup.BONE)),
    "hanged_skeleton"
  );

  public static final Block SKELETON = registerBlockWithItem(
    new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_SKELETON).dropsLike(HANGED_SKELETON), HANGED_SKELETON),
    "skeleton"
  );

  public static final Block HANGED_CORPSE = registerBlock(
    new SkeletonDecorationBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).strength(0.5F).sounds(BlockSoundGroup.MUD).ticksRandomly()),
    "hanged_corpse"
  );

  public static final Block CORPSE = registerBlockWithItem(
    new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).dropsLike(HANGED_CORPSE).ticksRandomly(), HANGED_CORPSE),
    "corpse"
  );

  public static final Block HANGED_ROTTING_CORPSE = registerBlock(
    new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).ticksRandomly()),
    "hanged_rotting_corpse"
  );

  public static final Block ROTTING_CORPSE = registerBlockWithItem(
    new SkeletonDecorationBlock(FabricBlockSettings.copyOf(HANGED_CORPSE).dropsLike(HANGED_ROTTING_CORPSE).ticksRandomly(), HANGED_ROTTING_CORPSE),
    "rotting_corpse"
  );

  public static final Block KING_STATUE = registerBlockWithItem(
    new DecorativeStatueBlock(FabricBlockSettings.copy(Blocks.STONE).nonOpaque().noCollision()),
    "king_statue"
  );

  public static final Block FLAG_POLE = registerBlockWithItem(
    new FlagPoleBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)),
    "flag_pole"
  );

  public static final Block BIOME_BANNER = registerBlock(
    new BiomeBannerBlock(FabricBlockSettings.copyOf(Blocks.WHITE_BANNER)),
    "biome_banner"
  );

  public static final Block ALCHEMY_EQUIPMENT_0 = registerBlockWithItem(
    new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS).offsetType(AbstractBlock.OffsetType.XZ)),
    "alchemy_equipment_0"
  );

  public static final Block ALCHEMY_EQUIPMENT_1 = registerBlockWithItem(
    new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS)),
    "alchemy_equipment_1"
  );

  public static final Block ALCHEMY_EQUIPMENT_2 = registerBlockWithItem(
    new AlchemyEquipmentBlock(FabricBlockSettings.copyOf(Blocks.GLASS).offsetType(AbstractBlock.OffsetType.XZ)),
    "alchemy_equipment_2"
  );

  public static final ColoredTorchBlock PRISON_TORCH = registerBlockWithItem(
    new ColoredTorchBlock(FabricBlockSettings.of(Material.METAL).breakInstantly().luminance(15).ticksRandomly(), "prison"),
    "prison_torch"
  );

  public static final ColoredTorchBlock PROMENADE_TORCH = registerBlockWithItem(
    new ColoredTorchBlock(FabricBlockSettings.of(Material.METAL).breakInstantly().luminance(15).ticksRandomly(), "promenade"),
    "promenade_torch"
  );

  public static final Block KINGDOM_PORTAL_CORE = registerBlock(
    new KingdomPortalCoreBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).luminance(
      state -> state.get(KingdomPortalCoreBlock.LIT) ? 8 : 0)
    ),
    "kingdom_portal_core"
  );
  public static final Block KINGDOM_PORTAL_FILLER = registerBlock(
    new KingdomPortalCoreBlock.Filler(FabricBlockSettings.copyOf(Blocks.BEDROCK)),
    "kingdom_portal_filler"
  );

  public static final Block PRISON_BOX = registerBlock(
    new MonsterBoxBlock(
      0.5f,
      new MonsterBoxBlock.Entry(MineCellsEntities.LEAPING_ZOMBIE, 4),
      new MonsterBoxBlock.Entry(MineCellsEntities.UNDEAD_ARCHER, 2),
      new MonsterBoxBlock.Entry(MineCellsEntities.GRENADIER, 1),
      new MonsterBoxBlock.Entry(MineCellsEntities.SHIELDBEARER, 1)
    ),
    "prison_box"
  );

  public static final Block CONJUNCTIVIUS_BOX = registerBlock(
    new MonsterBoxBlock(MineCellsEntities.CONJUNCTIVIUS),
    "conjunctivius_box"
  );

  public static final Block SHOCKER_BOX = registerBlock(
    new MonsterBoxBlock(MineCellsEntities.SHOCKER),
    "shocker_box"
  );

  public static final Block SPAWNER_RUNE = registerBlock(
    new SpawnerRuneBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).noCollision().nonOpaque().ticksRandomly()),
    "spawner_rune"
  );

  public static final FluidBlock SEWAGE = new FluidBlock(MineCellsFluids.STILL_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));
  public static final FluidBlock ANCIENT_SEWAGE = new FluidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));

  public static void init() {
    Registry.register(Registry.BLOCK, MineCells.createId("sewage"), SEWAGE);
    Registry.register(Registry.BLOCK, MineCells.createId("ancient_sewage"), ANCIENT_SEWAGE);

    StrippableBlockRegistry.register(PUTRID_LOG, STRIPPED_PUTRID_LOG);
  }

  public static Block registerBlock(Block block, String id) {
    Registry.register(Registry.BLOCK, MineCells.createId(id), block);
    return block;
  }

  public static <T extends Block> T registerBlockWithItem(T block, String id) {
    registerBlock(block, id);
    Registry.register(
      Registry.ITEM,
      MineCells.createId(id), 
      new BlockItem(block, new FabricItemSettings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS))
    );
    return block;
  }
}
