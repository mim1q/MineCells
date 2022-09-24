package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BigChainBlock;
import com.github.mim1q.minecells.block.GroundDecoration;
import com.github.mim1q.minecells.block.setupblocks.ElevatorAssemblerBlock;
import com.github.mim1q.minecells.block.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.block.setupblocks.MonsterBoxBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class MineCellsBlocks {
  public static final Block ELEVATOR_ASSEMBLER = registerBlock(new ElevatorAssemblerBlock(), "elevator_assembler");
  public static final Block BIG_CHAIN = new BigChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN));
  public static final Block HARDSTONE = new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK));
  public static final Block CHAIN_PILE_BLOCK = registerBlockWithItem(
    new Block(FabricBlockSettings.copyOf(Blocks.CHAIN).hardness(0.5F)),
    "chain_pile_block"
  );
  public static final Block CHAIN_PILE = registerBlockWithItem(
    new GroundDecoration(FabricBlockSettings.copyOf(Blocks.CHAIN).hardness(0.5F)),
    "chain_pile"
  );

  public static final Block KINGDOM_PORTAL_CORE = registerBlock(
    new KingdomPortalCoreBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).luminance(
      state -> state.get(KingdomPortalCoreBlock.LIT) ? 8 : 0)
    ),
    "kingdom_portal_core"
  );
  public static final Block KINGDOM_PORTAL_FILLER = registerBlock(
    new KingdomPortalCoreBlock.Filler(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)),
    "kingdom_portal_filler"
  );

  public static final Block PRISON_BOX = registerBlock(
    new MonsterBoxBlock(
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

  public static final FluidBlock SEWAGE = new FluidBlock(MineCellsFluids.STILL_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));
  public static final FluidBlock ANCIENT_SEWAGE = new FluidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));

  public static final BlockItem HARDSTONE_ITEM = new BlockItem(HARDSTONE, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final BlockItem ELEVATOR_ASSEMBLER_BLOCK_ITEM = new BlockItem(ELEVATOR_ASSEMBLER, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final BlockItem BIG_CHAIN_BLOCK_ITEM = new BlockItem(BIG_CHAIN, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));

  public static void init() {
    Registry.register(Registry.BLOCK, MineCells.createId("big_chain"), BIG_CHAIN);
    Registry.register(Registry.BLOCK, MineCells.createId("hardstone"), HARDSTONE);

    Registry.register(Registry.BLOCK, MineCells.createId("sewage"), SEWAGE);
    Registry.register(Registry.BLOCK, MineCells.createId("ancient_sewage"), ANCIENT_SEWAGE);

    Registry.register(Registry.ITEM, MineCells.createId("hardstone"), HARDSTONE_ITEM);
    Registry.register(Registry.ITEM, MineCells.createId("elevator_assembler"), ELEVATOR_ASSEMBLER_BLOCK_ITEM);
    Registry.register(Registry.ITEM, MineCells.createId("big_chain"), BIG_CHAIN_BLOCK_ITEM);
  }

  public static Block registerBlock(Block block, String id) {
    Registry.register(Registry.BLOCK, MineCells.createId(id), block);
    return block;
  }

  public static Block registerBlockWithItem(Block block, String id) {
    registerBlock(block, id);
    Registry.register(Registry.ITEM, MineCells.createId(id), new BlockItem(block, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS)));
    return block;
  }
}
