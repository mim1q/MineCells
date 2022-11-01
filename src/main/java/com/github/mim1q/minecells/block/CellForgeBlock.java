package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CellForgeBlock extends Block {
  public CellForgeBlock(Settings settings) {
    super(settings);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.isClient()) {
      return ActionResult.SUCCESS;
    }
    player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
    return ActionResult.CONSUME;
  }

  @Override
  @SuppressWarnings("deprecation")
  public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
    return new SimpleNamedScreenHandlerFactory(
      (i, inventory, player) -> new CellForgeScreenHandler(i, inventory, pos),
      Text.of("Cell forge")
    );
  }
}
