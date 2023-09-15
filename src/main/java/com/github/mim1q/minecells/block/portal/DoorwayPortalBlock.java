package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoorwayPortalBlock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
  private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
  public final DoorwayType type;

  public DoorwayPortalBlock(Settings settings, DoorwayType type) {
    super(settings);
    this.type = type;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), SHAPE);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    if (context instanceof EntityShapeContext entityShapeContext) {
      var entity = entityShapeContext.getEntity();
      var blockEntity = world.getBlockEntity(pos, MineCellsBlockEntities.DOORWAY).orElse(null);
      if (entity instanceof PlayerEntity player && blockEntity != null) {
        if (!blockEntity.canPlayerEnter(player)) {
          return getOutlineShape(state, world, pos, context);
        }
      }
    }
    return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), COLLISION_SHAPE);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DoorwayPortalBlockEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (world instanceof ServerWorld serverWorld) {
      var box = ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), COLLISION_SHAPE).getBoundingBox()
        .offset(pos)
        .expand(0.01);
      if (entity.getBoundingBox().intersects(box) && entity instanceof ServerPlayerEntity player) {
        var blockentity = world.getBlockEntity(pos);
        if (blockentity instanceof DoorwayPortalBlockEntity doorway) {
          doorway.teleportPlayer(player, serverWorld, type.dimension);
        }
      }
    }
  }

  @Override
  public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    var direction = state.get(FACING);
    var rotatedVector = Vec3d.of(direction.rotateYClockwise().getVector());
    for (int i = 0; i < 3; i++) {
      var dx = rotatedVector.getX() * (random.nextDouble() * 1.4 - 0.7);
      var dy = random.nextDouble() * 2.4 - 1.5;
      var dz = rotatedVector.getZ() * (random.nextDouble() * 1.4 - 0.7);
      var particlePos = Vec3d.ofCenter(pos)
        .add(Vec3d.of(direction.getOpposite().getVector()).multiply(0.48))
        .add(dx, dy, dz);
      world.addParticle(
        MineCellsParticles.SPECKLE.get(type.color),
        particlePos.x, particlePos.y, particlePos.z,
        (random.nextDouble() * 0.02 + 0.03) * direction.getOffsetX(),
        random.nextDouble() * 0.02 - 0.01,
        (random.nextDouble() * 0.02 + 0.03) * direction.getOffsetZ()
      );
    }
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return (entityWorld, entityPos, entityState, entity) -> {
      if (entityWorld.getTime() % 40 == 0 && entity instanceof DoorwayPortalBlockEntity doorway) {
        doorway.updateClientVisited();
      }
    };
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.with(FACING, mirror.apply(state.get(FACING)));
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    super.onStateReplaced(state, world, pos, newState, moved);
    if (world.isClient()) return;
    var facing = state.get(FACING);
    var x = facing.rotateYClockwise().getOffsetX();
    var z = facing.rotateYClockwise().getOffsetZ();
    for (int xz = -1; xz <= 1; xz++) {
      for (int y = -1; y <= 1; y++) {
        world.breakBlock(pos.add(x * xz, y, z * xz), true);
      }
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    MineCellsBlocks.DOORWAY_FRAME.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
  }

  @Override
  @SuppressWarnings("deprecation")
  public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
    var stacks = super.getDroppedStacks(state, builder);
    var blockEntity = builder.get(LootContextParameters.BLOCK_ENTITY);
    for (var stack : stacks) {
      if (stack.isOf(MineCellsItems.PRISON_DOORWAY) && blockEntity instanceof DoorwayPortalBlockEntity doorway) {
        doorway.setStackNbt(stack);
      }
    }
    return stacks;
  }

  public enum DoorwayType {
    OVERWORLD(MineCellsDimension.OVERWORLD, 0x8EF96D),
    PRISON(MineCellsDimension.PRISONERS_QUARTERS, 0xC1FCC4),
    PROMENADE(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED, 0x93FFF7),
    INSUFFERABLE_CRYPT(MineCellsDimension.INSUFFERABLE_CRYPT, 0xFF4CF4),
    RAMPARTS(MineCellsDimension.RAMPARTS, 0xFFC540);

    public final MineCellsDimension dimension;
    public final Identifier texture;
    public final int color;
    DoorwayType(MineCellsDimension dimension, int color) {
      this.dimension = dimension;
      this.texture = MineCells.createId("textures/block/doorway/" + dimension.key.getValue().getPath() + ".png");
      this.color = color;
    }
  }

  public static class Frame extends Block {
    private static final EnumProperty<FillerType> TYPE = EnumProperty.of("type", FillerType.class);

    public Frame(Settings settings) {
      super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(FACING, TYPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), state.get(TYPE).outlineShape);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return ModelUtils.rotateShape(Direction.NORTH, state.get(FACING), state.get(TYPE).collisionShape);
    }

    public BlockState getState(FillerType type, Direction direction) {
      return getDefaultState().with(FACING, direction).with(TYPE, type);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
      return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.with(FACING, mirror.apply(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView blockView, BlockPos pos) {
      if (blockView instanceof World world && MineCellsDimension.of(world) == MineCellsDimension.OVERWORLD) {
        return super.calcBlockBreakingDelta(state, player, blockView, pos);
      }
      return 0F;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
      if (
        MineCellsDimension.of(world) == MineCellsDimension.OVERWORLD
        && sourcePos.equals(pos.subtract(state.get(FACING).getVector()))
        && !world.getBlockState(sourcePos).isSideSolidFullSquare(world, sourcePos, state.get(FACING))
      ) {
        onBroken(world, pos, state);
        world.breakBlock(pos, true);
      }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
      super.onStateReplaced(state, world, pos, newState, moved);
      if (world.isClient()) return;
      var direction = state.get(FACING);
      var offset = state.get(TYPE).breakOffset;
      var x = direction.rotateYCounterclockwise().getOffsetX();
      var z = direction.rotateYCounterclockwise().getOffsetZ();
      var breakPos = pos.add(offset.getX() * x, offset.getY(), offset.getX() * z);
      world.getBlockState(breakPos).getBlock().onBroken(world, breakPos, world.getBlockState(breakPos));
      world.breakBlock(breakPos, true);
    }

    public enum FillerType implements StringIdentifiable {
      MIDDLE(SHAPE, COLLISION_SHAPE, "middle", new Vec3i(0, 1, 0)),
      RIGHT(
        createCuboidShape(4.0, 0.0, 8.0, 16.0, 16.0, 16.0),
        createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        "right",
        new Vec3i(-1, 0, 0)
      ),
      LEFT(
        createCuboidShape(0.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        "left",
        new Vec3i(1, 0, 0)
      ),
      TOP_RIGHT(
        createCuboidShape(4.0, 0.0, 8.0, 16.0, 16.0, 16.0),
        VoxelShapes.union(
          createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
          createCuboidShape(12.0, 8.0, 8.0, 16.0, 16.0, 16.0)
        ),
        "top_right",
        new Vec3i(-1, 0, 0)
      ),
      TOP_LEFT(
        createCuboidShape(0.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        VoxelShapes.union(
          createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
          createCuboidShape(0.0, 8.0, 8.0, 4.0, 16.0, 16.0)
        ),
        "top_left",
        new Vec3i(1, 0, 0)
      ),
      TOP(
        SHAPE,
        createCuboidShape(0.0, 8.0, 8.0, 16.0, 16.0, 16.0),
        "top",
        new Vec3i(0, -1, 0)
      );

      public final VoxelShape outlineShape;
      public final VoxelShape collisionShape;
      private final String name;
      private final Vec3i breakOffset;

      FillerType(VoxelShape outlineShape, VoxelShape collisionShape, String name, Vec3i breakOffset) {
        this.outlineShape = outlineShape;
        this.collisionShape = collisionShape;
        this.name = name;
        this.breakOffset = breakOffset;
      }

      @Override
      public String asString() {
        return name;
      }
    }
  }
}
