package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.BigChainBlock;
import com.github.mim1q.minecells.block.ElevatorAssemblerBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static final ElevatorAssemblerBlock ELEVATOR_ASSEMBLER = new ElevatorAssemblerBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0F));
    public static final BlockItem ELEVATOR_ASSEMBLER_BLOCK_ITEM = new BlockItem(ELEVATOR_ASSEMBLER, new Item.Settings().group(ItemGroupRegistry.MINECELLS_BLOCKS_AND_ITEMS));
    public static final BigChainBlock BIG_CHAIN = new BigChainBlock(FabricBlockSettings.copyOf(Blocks.CHAIN));
    public static final BlockItem BIG_CHAIN_BLOCK_ITEM = new BlockItem(BIG_CHAIN, new Item.Settings().group(ItemGroupRegistry.MINECELLS_BLOCKS_AND_ITEMS));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(MineCells.MOD_ID, "elevator_assembler"), ELEVATOR_ASSEMBLER);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "elevator_assembler"), ELEVATOR_ASSEMBLER_BLOCK_ITEM);
        Registry.register(Registry.BLOCK, new Identifier(MineCells.MOD_ID, "big_chain"), BIG_CHAIN);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "big_chain"), BIG_CHAIN_BLOCK_ITEM);
    }

    public static void registerClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BIG_CHAIN, RenderLayer.getCutout());
    }
}
