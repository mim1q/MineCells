package com.github.mim1q.minecells.block.setupblocks;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElevatorAssemblerBlock extends SetupBlock {
  public static final Property<Boolean> WAITING = BooleanProperty.of("waiting");
  public static final Property<Boolean> UNBREAKABLE = BooleanProperty.of("unbreakable");

  public ElevatorAssemblerBlock() {
    super(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(0.5F));
    this.setDefaultState(
      this.stateManager.getDefaultState()
        .with(WAITING, false)
        .with(UNBREAKABLE, false)
    );
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    boolean result = this.setup(world, pos, state);
    return result ? ActionResult.SUCCESS : ActionResult.FAIL;
  }

  @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(WAITING, UNBREAKABLE);
  }

  @Override
  public boolean setup(World world, BlockPos pos, BlockState state) {
    if (state.get(WAITING)) {
      return false;
    }

    int maxHeight = MineCells.COMMON_CONFIG.elevator.maxAssemblyHeight;
    int minHeight = MineCells.COMMON_CONFIG.elevator.minAssemblyHeight;
    // Search for another elevator assembler
    int minY = Math.max(world.getBottomY(), pos.getY() - maxHeight);
    int maxY = Math.min(world.getTopY(), pos.getY() + maxHeight);
    int second = pos.getY();
    for (int y = minY; y <= maxY; y++) {
      if (y == pos.getY()) {
        continue;
      }
      if (world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).getBlock() instanceof ElevatorAssemblerBlock) {
        second = y;
        break;
      }
    }

    if (Math.abs(pos.getY() - second) < minHeight) {
      return false;
    }

    boolean goingUp = second < pos.getY();

    int elevatorMinY = Math.min(pos.getY(), second);
    int elevatorMaxY = Math.max(pos.getY(), second);

    Block north = world.getBlockState(pos.north()).getBlock();
    Block south = world.getBlockState(pos.south()).getBlock();

    boolean rotated = north instanceof ChainBlock && south instanceof ChainBlock;

    if (ElevatorEntity.validateShaft(world, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated)) {
      if (!world.isClient()) {
        var elevator = ElevatorEntity.spawn(world, pos.getX(), pos.getZ(), elevatorMinY, elevatorMaxY, rotated, goingUp);
        world.removeBlockEntity(pos);
        world.breakBlock(pos, false);
        world.removeBlockEntity(pos.withY(second));
        world.breakBlock(pos.withY(second), false);
        elevator.setUnbreakable(true);
      }
      return true;
    }
    return false;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
