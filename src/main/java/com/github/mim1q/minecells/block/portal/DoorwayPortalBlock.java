package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.FillerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ModelUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DoorwayPortalBlock extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
  private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
  private static final Filler FILLER = MineCellsBlocks.DOORWAY_FRAME;
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

  public enum DoorwayType {
    PROMENADE(MineCells.createId("promenade"), "promenade", 0x93FFF7);
    public final Identifier dimension;
    public final Identifier texture;
    public final int color;
    DoorwayType(Identifier dimension, String name, int color) {
      this.dimension = dimension;
      this.texture = MineCells.createId("textures/block/doorway/" + name + ".png");
      this.color = color;
    }
  }

  public static class Filler extends FillerBlock {
    private static final EnumProperty<FillerType> TYPE = EnumProperty.of("type", FillerType.class);

    public Filler(Settings settings) {
      super(settings, (block) -> block instanceof DoorwayPortalBlock, true);
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
    public float getHardness() {
      return super.getHardness();
    }

    private enum FillerType implements StringIdentifiable {
      MIDDLE(SHAPE, COLLISION_SHAPE, "middle"),
      RIGHT(
        createCuboidShape(4.0, 0.0, 8.0, 16.0, 16.0, 16.0),
        createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        "right"
      ),
      LEFT(
        createCuboidShape(0.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        "left"
      ),
      TOP_RIGHT(
        createCuboidShape(4.0, 0.0, 8.0, 16.0, 16.0, 16.0),
        VoxelShapes.union(
          createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
          createCuboidShape(12.0, 8.0, 8.0, 16.0, 16.0, 16.0)
        ),
        "top_right"
      ),
      TOP_LEFT(
        createCuboidShape(0.0, 0.0, 8.0, 12.0, 16.0, 16.0),
        VoxelShapes.union(
          createCuboidShape(4.0, 0.0, 8.0, 12.0, 16.0, 16.0),
          createCuboidShape(0.0, 8.0, 8.0, 4.0, 16.0, 16.0)
        ),
        "top_left"
      ),
      TOP(
        SHAPE,
        createCuboidShape(0.0, 8.0, 8.0, 16.0, 16.0, 16.0),
        "top"
      );

      public final VoxelShape outlineShape;
      public final VoxelShape collisionShape;
      private final String name;
      FillerType(VoxelShape outlineShape, VoxelShape collisionShape, String name) {
        this.outlineShape = outlineShape;
        this.collisionShape = collisionShape;
        this.name = name;
      }

      @Override
      public String asString() {
        return name;
      }
    }
  }
}
