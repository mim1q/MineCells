package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class EliteObeliskEntity extends ObeliskEntity {
  private Identifier spawnerRuneDataId = null;

  public EliteObeliskEntity(EntityType<?> type, World world) {
    super(type, world);
  }

  @Override
  public Item getActivationItem() {
    return null;
  }

  @Override
  public Identifier getSpawnerRuneDataId() {
    return spawnerRuneDataId;
  }

  @Override
  public Box getBox() {
    return Box.of(getPos(), 32.0, 32.0, 32.0);
  }

  @Override
  protected void postProcessEntity(Entity entity) {
    var pos = this.getPos().add(getRotationVector().multiply(2.0));
    entity.setPos(pos.getX(), pos.getY(), pos.getZ());
  }

  @Override
  public ActionResult interact(PlayerEntity user, Hand hand) {
    if (!getWorld().isClient) {
      var server = getServer();
      if (server != null) {
        var advancement = server.getAdvancementLoader().get(MineCells.createId("elite"));
        ((ServerPlayerEntity) user).getAdvancementTracker().grantCriterion(advancement, "obelisk_activated");
      }
    }
    return super.interact(user, hand);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    if (nbt.contains("spawnerRuneDataId")) {
      spawnerRuneDataId = Identifier.tryParse(nbt.getString("spawnerRuneDataId"));
    }
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    if (spawnerRuneDataId != null) {
      nbt.putString("spawnerRuneDataId", spawnerRuneDataId.toString());
    }
  }
}
