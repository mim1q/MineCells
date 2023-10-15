package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoorwayItem extends AliasedBlockItem {
  private static final String TOOLTIP_VISITED = "item.minecells.prison_doorway.visited";
  private static final String TOOLTIP_NOT_VISITED = "item.minecells.prison_doorway.not_visited";
  private static final String TOOLTIP_BOUND = "item.minecells.prison_doorway.bound";
  private static final String TOOLTIP_NOT_BOUND = "item.minecells.prison_doorway.not_bound";
  private static final String DESCRIPTION = "item.minecells.prison_doorway.description";

  public DoorwayItem(Settings settings) {
    super(MineCellsBlocks.PROMENADE_DOORWAY, settings);
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
        ? context.getHorizontalPlayerFacing().getOpposite()
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
    var frameBlock = (context.getPlayer() != null && context.getPlayer().isCreative())
      ? MineCellsBlocks.UNBREAKABLE_DOORWAY_FRAME
      : MineCellsBlocks.DOORWAY_FRAME;
    var direction = context.getSide().getAxis().isVertical()
      ? context.getHorizontalPlayerFacing().getOpposite()
      : context.getSide();
    var world = context.getWorld();
    var pos = context.getBlockPos();
    var dy = world.getBlockState(pos.down()).isOpaqueFullCube(world, pos) ? 0 : -1;
    var rightVec = direction.rotateYCounterclockwise().getVector();
    var leftVec = direction.rotateYClockwise().getVector();
    world.setBlockState(pos.add(0, dy, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.MIDDLE, direction));
    world.setBlockState(pos.add(0, dy + 1, 0), MineCellsBlocks.PRISON_DOORWAY.getDefaultState().with(DoorwayPortalBlock.FACING, direction));
    writeNbtToBlockEntity(world, context.getPlayer(), pos.add(0, dy + 1, 0), context.getStack());
    world.setBlockState(pos.add(0, dy + 2, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.TOP, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy, 0),  frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.LEFT, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy + 1, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.LEFT, direction));
    world.setBlockState(pos.add(leftVec).add(0, dy + 2, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.TOP_LEFT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.RIGHT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy + 1, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.RIGHT, direction));
    world.setBlockState(pos.add(rightVec).add(0, dy + 2, 0), frameBlock.getState(DoorwayPortalBlock.Frame.FillerType.TOP_RIGHT, direction));
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
      if (stack.getOrCreateSubNbt("BlockEntityTag").contains("posOverride")) {
        return;
      }
      var x = Math.round(entity.getPos().x / 1024F) * 1024;
      var z = Math.round(entity.getPos().z / 1024F) * 1024;
      var area = "[x: " + x + ", z: " + z + "]";
      var message = ((PlayerEntityAccessor)player).getCurrentMineCellsPlayerData().hasVisitedDimension(MineCellsDimension.PRISONERS_QUARTERS)
        ? TOOLTIP_VISITED
        : TOOLTIP_NOT_VISITED;
      player.sendMessage(Text.translatable(message, area), true);
    }
  }

  @Override
  public boolean hasGlint(ItemStack stack) {
    return super.hasGlint(stack) || stack.getOrCreateSubNbt("BlockEntityTag").contains("posOverride");
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    tooltip.add(Text.translatable(DESCRIPTION).formatted(Formatting.DARK_GRAY));
    if (stack.getOrCreateSubNbt("BlockEntityTag").contains("posOverride")) {
      var posOverride = BlockPos.fromLong(stack.getOrCreateSubNbt("BlockEntityTag").getLong("posOverride"));
      var x = posOverride.getX();
      var z = posOverride.getZ();
      var area = "[x: " + x + ", z: " + z + "]";
      tooltip.add(Text.translatable(TOOLTIP_BOUND, area).formatted(Formatting.GREEN));
    } else {
      tooltip.add(Text.translatable(TOOLTIP_NOT_BOUND).formatted(Formatting.DARK_GRAY));
    }
  }
}