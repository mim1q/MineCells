package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.data.spawner_runes.SpawnerRuneController;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
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
      controller.tick(getBlockPos(), world);
      if (world.isClient()) return;

      if (world.getBlockState(getBlockPos()).isAir()) {
        world.setBlockState(getBlockPos(), MineCellsBlocks.SPAWNER_RUNE.getDefaultState());
        this.discard();
        var blockEntity = world.getBlockEntity(getBlockPos(), MineCellsBlockEntities.SPAWNER_RUNE);
        blockEntity.ifPresent(
          it -> {
            it.controller.setDataId(getWorld(), getBlockPos(), controller.getDataId());
            it.controller.setVisible(this.controller.isVisible());
            it.markDirty();
          }
        );
        return;
      } else if (world.getBlockState(getBlockPos()).isOf(MineCellsBlocks.SPAWNER_RUNE)) {
        discard();
      }
    }
  }

  @Override
  protected void initDataTracker() {
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    controller.setDataId(getWorld(), getBlockPos(), Identifier.tryParse(nbt.getString("dataId")));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putString("dataId", controller.getDataId().toString());
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
