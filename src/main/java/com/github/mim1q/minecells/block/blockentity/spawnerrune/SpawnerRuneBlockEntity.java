package com.github.mim1q.minecells.block.blockentity.spawnerrune;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SpawnerRuneBlockEntity extends BlockEntity {
  private SpawnerRuneData data = new SpawnerRuneData("", new EntryList(), 0, 0, 0);

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE_BLOCK_ENTITY, pos, state);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    data = SpawnerRuneData.fromNbt(nbt);
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    data.writeNbt(nbt);
  }

  @Override
  public void setStackNbt(ItemStack stack) {
    super.setStackNbt(stack);
    data.writeNbt(stack.getOrCreateNbt());
  }
}
