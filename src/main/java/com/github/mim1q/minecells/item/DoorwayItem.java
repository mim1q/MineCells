package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DoorwayItem extends AliasedBlockItem {
  public DoorwayItem(Settings settings) {
    super(MineCellsBlocks.DOORWAY_FRAME, settings);
  }

  @Override
  protected boolean canPlace(ItemPlacementContext context, BlockState state) {
    var world = context.getWorld();
    if (MineCellsDimension.of(world) != MineCellsDimension.OVERWORLD) {
      return false;
    }
    var pos = context.getBlockPos();
    if (
      world.getBlockState(pos.down()).isOpaqueFullCube(world, pos)
      || world.getBlockState(pos.down(2)).isOpaqueFullCube(world, pos)
    ) {
      var side = context.getSide().getAxis().isVertical()
        ? context.getPlayerFacing().getOpposite()
        : context.getSide();
      var dy = world.getBlockState(pos.down()).isOpaqueFullCube(world, pos) ? 1 : 0;
      var x = side.getAxis() == Direction.Axis.X ? 0 : 1;
      var z = side.getAxis() == Direction.Axis.Z ? 0 : 1;
      for (var y = -1; y <= 1; y++) {
        for (var xz = -1; xz <= 1; xz++) {
          var newPos = pos
            .subtract(side.getVector())
            .add(xz * x, y + dy, xz * z);
          var blockState = world.getBlockState(newPos);
          if (!blockState.isOpaqueFullCube(world, pos)) {
            return false;
          }
          if (!world.getBlockState(newPos.add(side.getVector())).canReplace(context)) {
            return false;
          }
        }
      }
      return super.canPlace(context, state);
    }
    return false;
  }

  @Override
  protected boolean place(ItemPlacementContext context, BlockState state) {
    var direction = context.getSide().getAxis().isVertical()
      ? context.getPlayerFacing().getOpposite()
      : context.getSide();
    var world = context.getWorld();
    var pos = context.getBlockPos();
    var dy = world.getBlockState(pos.down()).isOpaqueFullCube(world, pos) ? 0 : -1;
    var rightVec = direction.rotateYCounterclockwise().getVector();
    var leftVec = direction.rotateYClockwise().getVector();
    world.setBlockState(pos.add(0, dy, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.MIDDLE, direction));
    world.setBlockState(pos.add(0, dy + 1, 0), MineCellsBlocks.PRISON_DOORWAY.getDefaultState().with(DoorwayPortalBlock.FACING, direction));
    world.setBlockState(pos.add(0, dy + 2, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.TOP, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy, 0),  MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.LEFT, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy + 1, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.LEFT, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy + 2, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.TOP_LEFT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.RIGHT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy + 1, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.RIGHT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy + 2, 0), MineCellsBlocks.DOORWAY_FRAME.getState(DoorwayPortalBlock.Frame.FillerType.TOP_RIGHT, direction));
    return true;
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (
      !world.isClient
      && selected
      && entity instanceof ServerPlayerEntity player
      && MineCellsDimension.of(world) == MineCellsDimension.OVERWORLD
    ) {
      var area =
           "[x: " + Math.round(entity.getPos().x / 1024F) * 1024
        + ", z: " + Math.round(entity.getPos().z / 1024F) * 1024 + "]";
      var message = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData().hasVisitedDimension(MineCellsDimension.PRISONERS_QUARTERS)
        ? "You have visited the Prisoners' Quarters in this area "
        : "You haven't visited the Prisoners' Quarters in this area yet ";
      player.sendMessage(Text.literal(message + area), true);
    }
  }
}