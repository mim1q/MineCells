package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.item.DoorwayItem;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.screen.ScreenUtils;
import com.github.mim1q.minecells.util.ModelUtils;
import dev.mim1q.gimm1q.interpolation.Easing;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoorwayPortalBlock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty CLOSED = BooleanProperty.of("closed");

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
    builder.add(CLOSED);
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (world.isClient()) {
      ScreenUtils.openDoorwaySelectionScreen(pos);
      return ActionResult.SUCCESS;
    }
    return ActionResult.SUCCESS;
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
      if (entityWorld.getTime() % 5 == 0 && entity instanceof DoorwayPortalBlockEntity doorway) {
        if (entityWorld.isClient()) {
          var closed = doorway.getCachedState().get(CLOSED);
          doorway.closedBarsAnimation.transitionTo(closed ? 1f : 0f, 10f, closed ? Easing::easeOutBounce : Easing::easeOutCubic);
          return;
        }

        var dir = Vec3d.of(entityState.get(FACING).getVector().multiply(2));
        var players = entityWorld.getEntitiesByClass(PlayerEntity.class, Box.of(entityPos.toCenterPos(), 3.0, 2.0, 3.0).offset(dir), it -> true);
        var closed = players.isEmpty() || players.stream().anyMatch(it -> !doorway.canPlayerEnter(it));
        entityWorld.setBlockState(entityPos, entityState.with(CLOSED, closed));
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
    if (newState.getBlock() instanceof DoorwayPortalBlock) return;
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
      if (stack.getItem() instanceof DoorwayItem && blockEntity instanceof DoorwayPortalBlockEntity doorway) {
        doorway.setStackNbt(stack);
      }
    }
    return stacks;
  }

  public enum DoorwayType {
    OVERWORLD(MineCellsDimension.OVERWORLD, 0x8EF96D),
    PRISON(MineCellsDimension.PRISONERS_QUARTERS, 0x54EF88),
    PROMENADE(MineCellsDimension.PROMENADE_OF_THE_CONDEMNED, 0x93FFF7),
    INSUFFERABLE_CRYPT(MineCellsDimension.INSUFFERABLE_CRYPT, 0xFF4CF4),
    RAMPARTS(MineCellsDimension.RAMPARTS, 0xFFC540),
    BLACK_BRIDGE(MineCellsDimension.BLACK_BRIDGE, 0x623cc9);

    public final MineCellsDimension dimension;
    public final Identifier texture;
    public final Identifier backgroundTexture;
    public final int color;

    DoorwayType(MineCellsDimension dimension, int color) {
      this.dimension = dimension;
      this.texture = MineCells.createId("textures/block/doorway/" + dimension.key.getValue().getPath() + ".png");
      this.backgroundTexture = MineCells.createId("textures/block/doorway/" + dimension.key.getValue().getPath() + "_background.png");
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
      var breakPos = getBreakPos(state, pos);
      world.getBlockState(breakPos).getBlock().onBroken(world, breakPos, world.getBlockState(breakPos));
      world.breakBlock(breakPos, true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      var portalPos = getBreakPos(state, pos);
      var portal = world.getBlockState(portalPos);

      if (portal.getBlock() instanceof DoorwayPortalBlock || portal.getBlock() instanceof Frame) {
        return portal.getBlock().onUse(portal, world, portalPos, player, hand, hit);
      }

      return ActionResult.PASS;
    }

    private BlockPos getBreakPos(BlockState state, BlockPos pos) {
      var direction = state.get(FACING);
      var offset = state.get(TYPE).breakOffset;
      var x = direction.rotateYCounterclockwise().getOffsetX();
      var z = direction.rotateYCounterclockwise().getOffsetZ();
      return pos.add(offset.getX() * x, offset.getY(), offset.getX() * z);
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
