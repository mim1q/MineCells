package com.github.mim1q.minecells.mixin.world;

import com.github.mim1q.minecells.world.densityfunction.CliffDensityFunction;
import com.github.mim1q.minecells.world.densityfunction.RingDensityFunction;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunctionTypes.class)
public abstract class DensityFunctionTypesMixin {
  @Shadow
  private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> registry, String id, CodecHolder<? extends DensityFunction> codecHolder) {
    throw new Error("Not implemented");
  }

  @Inject(
    method = "registerAndGetDefault",
    at = @At("RETURN")
  )
  private static void minecells$registerDensityFunctions(
    Registry<Codec<? extends DensityFunction>> registry,
    CallbackInfoReturnable<Codec<? extends DensityFunction>> cir
  ) {
    register(registry, "minecells:ring", RingDensityFunction.CODEC_HOLDER);
    register(registry, "minecells:cliff", CliffDensityFunction.CODEC_HOLDER);
  }
}
