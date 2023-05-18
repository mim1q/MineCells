package com.github.mim1q.minecells.mixin.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  @Inject(method = "supports", at = @At("HEAD"), cancellable = true)
  private void supports(BlockState state, CallbackInfoReturnable<Boolean> cir) {
    if (BlockEntityType.SIGN.equals(this) && state.isIn(BlockTags.SIGNS)) {
      cir.setReturnValue(true);
    }
  }
}
