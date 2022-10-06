package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.BiomeBannerBlockEntity;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BiomeBannerBlock extends BlockWithEntity {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty WAVING = BooleanProperty.of("waving");
  public static EnumProperty<BannerPattern> PATTERN = EnumProperty.of("pattern", BannerPattern.class);
  public static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);

  public BiomeBannerBlock(Settings settings) {
    super(settings);
    this.setDefaultState(getDefaultState()
      .with(FACING, Direction.NORTH)
      .with(WAVING, true)
      .with(PATTERN, BannerPattern.KING_CREST)
    );
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new BiomeBannerBlockEntity(pos, state);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, WAVING, PATTERN);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    if (ctx.getSide() == Direction.UP || ctx.getSide() == Direction.DOWN) {
      return Blocks.AIR.getDefaultState();
    }
    Vec3i offset = ctx.getSide().getOpposite().getVector();
    BlockState state0 = ctx.getWorld().getBlockState(ctx.getBlockPos().add(offset).down());
    BlockState state1 = ctx.getWorld().getBlockState(ctx.getBlockPos().add(offset).down(2));
    BlockState resultState = getDefaultState().with(FACING, ctx.getSide());
    if (state0.isAir() && state1.isAir()) {
      return resultState.with(WAVING, true);
    }
    return resultState.with(WAVING, false);
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
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  public enum BannerPattern implements StringIdentifiable {
    PROMENADE("promenade_of_the_condemned"),
    KING_CREST("king_crest");

    private final String name;
    private final Identifier texture;

    BannerPattern(String name) {
      this.name = name;
      this.texture = MineCells.createId("textures/blockentity/banner/" + name + ".png");
    }

    @Override
    public String asString() {
      return this.name;
    }

    public Identifier getTexture() {
      return this.texture;
    }
  }
}
