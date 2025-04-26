package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnerRuneBlockEntity extends MineCellsBlockEntity {
  public final SpawnerRuneController controller = new SpawnerRuneController();

  public SpawnerRuneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.SPAWNER_RUNE, pos, state);
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    if (world.getTime() % 10 == 0) {
      controller.tick(pos, world);
      markDirty();
    }
  }

  @Override
  public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
    super.readNbt(nbt, lookup);
    controller.setDataId(getWorld(), getPos(), Identifier.tryParse(nbt.getString("dataId")));
    controller.setLastActivationTime(nbt.getLong("last_activation_time"));
  }



  @Override
  protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
    super.writeNbt(nbt, lookup);
    if (controller.getDataId() != null) {
      nbt.putString("dataId", controller.getDataId().toString());
    }
    nbt.putLong("last_activation_time", controller.getLastActivationTime());
  }
}
