package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.entity.data.TrackedDataHandlerRegistry.STRING;

public class CustomArrowEntity extends PersistentProjectileEntity {
  private CustomArrowType arrowType = CustomArrowType.DEFAULT;

  public static final TrackedData<String> ARROW_TYPE = DataTracker.registerData(CustomArrowEntity.class, STRING);

  public CustomArrowEntity(EntityType<? extends CustomArrowEntity> entityType, World world) {
    super(entityType, world);
  }

  public CustomArrowEntity(PlayerEntity owner, World world, CustomArrowType arrowType) {
    super(MineCellsEntities.CUSTOM_ARROW, world);
    this.dataTracker.set(ARROW_TYPE, arrowType.getName());
    setOwner(owner);
    setPosition(owner.getEyePos().subtract(0.0, 0.1, 0.0));
  }

  public CustomArrowType getArrowType() {
    return arrowType;
  }

  @Override
  protected ItemStack asItemStack() {
    return new ItemStack(Items.ARROW);
  }

  @Override
  public void onTrackedDataSet(TrackedData<?> data) {
    if (data == ARROW_TYPE) {
      this.arrowType = CustomArrowType.get(this.dataTracker.get(ARROW_TYPE));
    }

    super.onTrackedDataSet(data);
  }

  @Override
  public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> dataEntries) {
    super.onDataTrackerUpdate(dataEntries);
    var arrowType = dataEntries.stream().filter(it -> it.id() == ARROW_TYPE.getId()).findAny();
    arrowType.ifPresent(it -> this.arrowType = CustomArrowType.get((String) it.value()));
  }
}
