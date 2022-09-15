package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BigChainBlock;
import com.github.mim1q.minecells.block.ElevatorAssemblerBlock;
import com.github.mim1q.minecells.block.MonsterBoxBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class MineCellsBlocks {

  public static final ElevatorAssemblerBlock ELEVATOR_ASSEMBLER = new ElevatorAssemblerBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0F));
  public static final BigChainBlock BIG_CHAIN = new BigChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN));
  public static final Block HARDSTONE = new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK));

  public static final MonsterBoxBlock CONJUNCTIVIUS_BOX = new MonsterBoxBlock(MineCellsEntities.CONJUNCTIVIUS);

  public static final FluidBlock SEWAGE = new FluidBlock(MineCellsFluids.STILL_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));
  public static final FluidBlock ANCIENT_SEWAGE = new FluidBlock(MineCellsFluids.STILL_ANCIENT_SEWAGE, FabricBlockSettings.copyOf(Blocks.WATER));

  public static final BlockItem HARDSTONE_ITEM = new BlockItem(HARDSTONE, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final BlockItem ELEVATOR_ASSEMBLER_BLOCK_ITEM = new BlockItem(ELEVATOR_ASSEMBLER, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));
  public static final BlockItem BIG_CHAIN_BLOCK_ITEM = new BlockItem(BIG_CHAIN, new Item.Settings().group(MineCellsItemGroups.MINECELLS_BLOCKS_AND_ITEMS));

  public static void init() {
    Registry.register(Registry.BLOCK, MineCells.createId("elevator_assembler"), ELEVATOR_ASSEMBLER);
    Registry.register(Registry.BLOCK, MineCells.createId("big_chain"), BIG_CHAIN);
    Registry.register(Registry.BLOCK, MineCells.createId("hardstone"), HARDSTONE);

    Registry.register(Registry.BLOCK, MineCells.createId("conjunctivius_box"), CONJUNCTIVIUS_BOX);

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
}
