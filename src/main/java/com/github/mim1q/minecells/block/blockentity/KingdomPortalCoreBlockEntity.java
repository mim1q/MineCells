package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class KingdomPortalCoreBlockEntity extends BlockEntity {
  public KingdomPortalCoreBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityRegistry.KINGDOM_PORTAL_CORE_BLOCK_ENTITY, pos, state);
  }
}
