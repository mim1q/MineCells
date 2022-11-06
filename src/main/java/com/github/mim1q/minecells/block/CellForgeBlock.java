package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CellForgeBlock extends Block {

  public static final String CELL_FORGE_TITLE_KEY = "gui.minecells.cell_forge.title";
  private static final VoxelShape SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0, 0, 0, 16, 2, 16),
    Block.createCuboidShape(0, 14, 0, 16, 16, 16),
    Block.createCuboidShape(2, 2, 2, 14, 14, 14)
  );

  public CellForgeBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(Properties.HORIZONTAL_FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
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
      Text.translatable(CELL_FORGE_TITLE_KEY)
    );
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }
}
