package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.CellCrafterBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
    craftedItems.add(itemStack.copy());
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    if (world.isClient()) return;
    if (cooldown > 0) {
      if (cooldown % 10 == 0) {
        world.playSound(null, pos, MineCellsSounds.BUZZ, SoundCategory.BLOCKS, 0.2f, 0.8f + world.random.nextFloat() * 0.4f);
      }
      ((ServerWorld) world).spawnParticles(
        MineCellsParticles.ELECTRICITY.get(Vec3d.ZERO.addRandom(world.getRandom(), 1f), 1, 0xFFFFFF, 0.5f),
        pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 1,
        0, 0, 0, 0
      );
      ((ServerWorld) world).spawnParticles(
        MineCellsParticles.SPECKLE.get(0x00FFFF),
        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1,
        0.5, 0, 0.5, 0.01
      );
      cooldown--;
    } else if (!craftedItems.isEmpty()) {
      var itemStack = craftedItems.poll();
      dropStack(world, pos.up(), itemStack);
      setCooldown(10);
      world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 0.8f + world.random.nextFloat() * 0.4f);

      ((ServerWorld) world).spawnParticles(
        MineCellsParticles.SPECKLE.get(0xEE4460),
        pos.getX() + 0.5, pos.getY() + 1.75, pos.getZ() + 0.5, 15,
        0.2, 0.2, 0.2, 0.02
      );

    } else if (state.get(CellCrafterBlock.STATUS) == CellCrafterBlock.Status.CRAFTING) {
      world.setBlockState(pos, state.with(CellCrafterBlock.STATUS, CellCrafterBlock.Status.IDLE));
    }
  }
}
