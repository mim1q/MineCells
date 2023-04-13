package com.github.mim1q.minecells.mixin.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin implements WorldAccess, AutoCloseable {
  @Inject(method = "updateComparators", at = @At("HEAD"), cancellable = true)
  private void minecells$cancelUpdateComparators(BlockPos pos, Block block, CallbackInfo ci) {
    if (!this.isChunkLoaded(pos.getX(), pos.getZ())) {
      ci.cancel();
    }
  }
}
