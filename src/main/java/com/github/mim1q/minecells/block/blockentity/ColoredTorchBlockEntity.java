package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ColoredTorchBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ColoredTorchBlockEntity extends MineCellsBlockEntity {
  public ColoredTorchBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.COLORED_TORCH_BLOCK_ENTITY, pos, state);
  }

  public Identifier getTexture() {
    Block block = this.getCachedState().getBlock();
    if (block instanceof ColoredTorchBlock torchBlock) {
      return torchBlock.getFlameTexture();
    }
    return MineCells.createId("textures/block/metal_torch/white.png");
  }
}
