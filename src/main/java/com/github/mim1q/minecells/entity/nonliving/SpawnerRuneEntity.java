package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpawnerRuneEntity extends Entity {
  public final SpawnerRuneController controller = new SpawnerRuneController();

  public SpawnerRuneEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public void tick() {
    if (age % 10 == 0) {
      controller.tick(getBlockPos(), getWorld());
      if (getWorld().isClient()) return;

      if (getWorld().getBlockState(getBlockPos()).isAir()) {
        getWorld().setBlockState(getBlockPos(), MineCellsBlocks.SPAWNER_RUNE.getDefaultState());
        this.discard();
        var blockEntity = getWorld().getBlockEntity(getBlockPos(), MineCellsBlockEntities.SPAWNER_RUNE);
        blockEntity.ifPresent(
          it -> {
            it.controller.setDataId(getWorld(), getBlockPos(), controller.getDataId());
            it.controller.setVisible(this.controller.isVisible());
            it.controller.setLastActivationTime(this.controller.getLastActivationTime());
            it.markDirty();
          }
        );
      } else if (getWorld().getBlockState(getBlockPos()).isOf(MineCellsBlocks.SPAWNER_RUNE)) {
        discard();
      }
    }
  }

  @Override
  protected void initDataTracker(DataTracker.Builder builder) {
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    controller.setDataId(getWorld(), getBlockPos(), Identifier.tryParse(nbt.getString("dataId")));
    controller.setLastActivationTime(nbt.getLong("last_activation_time"));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    if (controller.getDataId() != null) {
      nbt.putString("dataId", controller.getDataId().toString());
    }
    nbt.putLong("last_activation_time", controller.getLastActivationTime());
  }
}
