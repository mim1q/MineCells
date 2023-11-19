package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.FlagBlockEntity;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FlagBlock extends BlockWithEntity {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty WAVING = BooleanProperty.of("waving");
  public static final EnumProperty<Placement> PLACEMENT = EnumProperty.of("placement", Placement.class);

  public static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
  public static final VoxelShape CENTERED_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);
  public static final VoxelShape HORIZONTAL_SHAPE = createCuboidShape(7.0, 0.0, 0.0, 8.0, 16.0, 16.0);

  public final Identifier texture;
  public final boolean large;

  public FlagBlock(Settings settings, String name, boolean large) {
    super(settings);
    this.texture = MineCells.createId("textures/blockentity/banner/" + name + ".png");
    this.large = large;
    this.setDefaultState(getDefaultState()
      .with(FACING, Direction.NORTH)
      .with(WAVING, true)
      .with(PLACEMENT, Placement.SIDE)
    );
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new FlagBlockEntity(pos, state);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, WAVING, PLACEMENT);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.UP) {
      return null;
    }
    if (ctx.getSide() == Direction.DOWN) {
      return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(PLACEMENT, Placement.CENTERED);
    }
    if (ctx.getWorld().getBlockState(BlockPos.ofFloored(ctx.getHitPos())).isIn(BlockTags.FENCES)) {
      return getDefaultState()
        .with(FACING, ctx.getSide())
        .with(WAVING, true)
        .with(PLACEMENT, Placement.HORIZONTAL);
    }
    var offset = ctx.getSide().getOpposite().getVector();
    var state0 = ctx.getWorld().getBlockState(ctx.getBlockPos().add(offset).down());
    var state1 = ctx.getWorld().getBlockState(ctx.getBlockPos().add(offset).down(2));
    return getDefaultState()
      .with(FACING, ctx.getSide())
      .with(WAVING, state0.isAir() && state1.isAir());
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    world.setBlockState(pos, state.with(WAVING, !state.get(WAVING)));
    return ActionResult.SUCCESS;
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    var shape = switch (state.get(PLACEMENT)) {
      case SIDE -> SHAPE;
      case CENTERED -> CENTERED_SHAPE;
      case HORIZONTAL -> HORIZONTAL_SHAPE;
    };
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), shape);
  }

  @Override
  public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  public enum Placement implements StringIdentifiable {
    SIDE("side"),
    CENTERED("centered"),
    HORIZONTAL("horizontal");

    private final String name;

    Placement(String name) {
      this.name = name;
    }

    @Override
    public String asString() {
      return this.name;
    }
  }
}
