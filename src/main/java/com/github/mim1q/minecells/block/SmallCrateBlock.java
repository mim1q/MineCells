package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class SmallCrateBlock extends GroundDecorationBlock {
  private static final IntProperty ROTATION = IntProperty.of("rotation", 0, 3);

  public SmallCrateBlock(Settings settings) {
    super(settings, Shape.BLOCK_12);
  }

  @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(ROTATION);
  }

  @Nullable @Override public BlockState getPlacementState(ItemPlacementContext ctx) {
    var rotation = 3 - MathHelper.floorMod((int) ((ctx.getPlayerYaw() + 360 - 11.25f) / 22.5f), 4);
    return getDefaultState().with(ROTATION, rotation);
  }
}
