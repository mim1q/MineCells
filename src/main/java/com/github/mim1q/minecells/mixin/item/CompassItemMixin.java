package com.github.mim1q.minecells.mixin.item;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.CompassItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItem.class)
public class CompassItemMixin {
  @Inject(
    method = "createSpawnPos",
    at = @At(value = "HEAD"),
    cancellable = true
  )
  private static void minecells$createSpawnPos(World world, CallbackInfoReturnable<GlobalPos> cir) {
    if (world.isClient && MineCellsDimension.isMineCellsDimension(world)) {
      var dimension = MineCellsDimension.of(world);
      var player = MinecraftClient.getInstance().player;
      if (player == null) return;
      var point = GridBasedStructureUtils.getSpecialPoint((ServerWorld) world, player.getBlockPos(), dimension, SpecialPointIds.ENTRANCE);
      if (point.isEmpty()) return;
      var pos = MathUtils.getClosestMultiplePosition(player.getBlockPos(), 1024);
      cir.setReturnValue(GlobalPos.create(dimension.key, new BlockPos(point.get().offset().add(pos))));
    }
  }
}
