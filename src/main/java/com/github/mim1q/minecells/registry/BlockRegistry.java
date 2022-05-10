package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ElevatorBlock;
import com.github.mim1q.minecells.block.blockentity.ElevatorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static final ElevatorBlock ELEVATOR_BLOCK = new ElevatorBlock(FabricBlockSettings.of(Material.WOOD).strength(4.0F));

    public static final BlockEntityType<ElevatorBlockEntity> ELEVATOR_BLOCK_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(MineCells.MOD_ID, "elevator_block_entity"),
            FabricBlockEntityTypeBuilder.create(ElevatorBlockEntity::new, ELEVATOR_BLOCK).build(null));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(MineCells.MOD_ID, "elevator_block"), ELEVATOR_BLOCK);
    }
}
