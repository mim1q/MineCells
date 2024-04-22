package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.CellCrafterBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

import static net.minecraft.block.Block.dropStack;

public class CellCrafterBlockEntity extends MineCellsBlockEntity implements NamedScreenHandlerFactory {
  private final Queue<ItemStack> craftedItems = new LinkedList<>();
  private int cooldown = 0;

  public CellCrafterBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.CELL_CRAFTER, pos, state);
  }

  @Override
  public Text getDisplayName() {
    return Text.translatable(CellCrafterBlock.CELL_FORGE_TITLE_KEY);
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
    return new CellCrafterScreenHandler(syncId, playerInventory, this);
  }

  public void setCooldown(int cooldown) {
    this.cooldown = cooldown;
  }

  public void addStack(ItemStack itemStack) {
    craftedItems.add(itemStack);
  }

  public void onScreenClosed() {
    if (world == null || !world.getBlockState(pos).equals(this.getCachedState())) return;

    world.setBlockState(pos, getCachedState().with(CellCrafterBlock.STATUS,
      craftedItems.isEmpty()
        ? CellCrafterBlock.Status.IDLE
        : CellCrafterBlock.Status.CRAFTING)
    );
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    if (cooldown > 0) {
      cooldown--;
    } else if (!craftedItems.isEmpty()) {
      var itemStack = craftedItems.poll();
      dropStack(world, pos.up(), itemStack);
      setCooldown(10);
    } else {
      world.setBlockState(pos, state.with(CellCrafterBlock.STATUS, CellCrafterBlock.Status.IDLE));
    }
  }
}
