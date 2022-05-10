package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ElevatorBlockEntity extends BlockEntity {
    public ElevatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.ELEVATOR_BLOCK_ENTITY, pos, state);
    }
}
