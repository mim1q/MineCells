package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ElevatorAssemblerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static final ElevatorAssemblerBlock ELEVATOR_ASSEMBLER = new ElevatorAssemblerBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0F));
    public static final BlockItem ELEVATOR_ASSEMBLER_BLOCK_ITEM = new BlockItem(ELEVATOR_ASSEMBLER, new Item.Settings().group(ItemGroupRegistry.MINECELLS_BLOCKS));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(MineCells.MOD_ID, "elevator_assembler"), ELEVATOR_ASSEMBLER);
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "elevator_assembler"), ELEVATOR_ASSEMBLER_BLOCK_ITEM);
    }
}
