package com.github.mim1q.minecells.mixin.block;

import com.github.mim1q.minecells.block.BarrierRuneBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
  @Inject(
    method = "cannotConnect",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void minecells$cannotConnect(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (state.getBlock() instanceof BarrierRuneBlock) {
      cir.setReturnValue(true);
    }
  }
}
