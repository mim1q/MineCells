package com.github.mim1q.minecells.mixin.item;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
  @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
  public void canPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (context.getPlayer() != null && ((LivingEntityAccessor)context.getPlayer()).getMineCellsFlag(MineCellsEffectFlags.DISARMED)) {
      cir.setReturnValue(false);
    }
  }
}
