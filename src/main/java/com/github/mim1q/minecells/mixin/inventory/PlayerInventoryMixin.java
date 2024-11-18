package com.github.mim1q.minecells.mixin.inventory;

import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory, Nameable {
  @Shadow @Final public DefaultedList<ItemStack> main;

  @Inject(
    method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$onInsertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    if (stack.isOf(MineCellsItems.MONSTER_CELL)) {
      for (ItemStack slotStack : main) {
        if (slotStack.isOf(MineCellsItems.CELL_HOLDER)) {
          CellHolderItem.setCellCount(slotStack, CellHolderItem.getCellCount(slotStack) + stack.getCount());
          slotStack.setBobbingAnimationTime(5);
          stack.setCount(0);
          cir.setReturnValue(false);
          return;
        }
      }
    }
  }
}
