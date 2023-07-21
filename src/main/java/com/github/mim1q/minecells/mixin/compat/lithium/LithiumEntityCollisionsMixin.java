package com.github.mim1q.minecells.mixin.compat.lithium;

import com.github.mim1q.minecells.accessor.MineCellsBorderEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Mixin(targets = "me.jellysquid.mods.lithium.common.entity.LithiumEntityCollisions")
public abstract class LithiumEntityCollisionsMixin {
  @Inject(
    method = "getEntityWorldBorderCollisions(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Z)Ljava/util/List;",
    at = @At("TAIL"),
    cancellable = true
  )
  private static void minecells$injectGetEntityWorldBorderCollisions(
    World world,
    Entity entity,
    Box box,
    boolean includeWorldBorder,
    CallbackInfoReturnable<List<VoxelShape>> cir
  ) {
    if (entity != null && MineCellsDimension.isMineCellsDimension(world)) {
      var border = ((MineCellsBorderEntity) entity).getMineCellsBorder();
      var shape = VoxelShapes.cuboid(
        border.getBoundWest(), Double.NEGATIVE_INFINITY, border.getBoundNorth(),
        border.getBoundEast(), Double.POSITIVE_INFINITY, border.getBoundSouth()
      );
      var result = new ArrayList<>(cir.getReturnValue());
      result.add(VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, shape, BooleanBiFunction.ONLY_FIRST));
      cir.setReturnValue(result);
    }
  }
}
