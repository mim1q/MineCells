package com.github.mim1q.minecells.mixin;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.generator.ImageDensityFunction;
import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunctionTypes.class)
public class DensityFunctionTypesMixin {
    @Inject(method = "registerAndGetDefault(Lnet/minecraft/util/registry/Registry;)Lcom/mojang/serialization/Codec;", at = @At("TAIL"))
    private static void registerAndGetDefault(Registry<Codec<? extends DensityFunction>> registry, CallbackInfoReturnable<Codec<? extends DensityFunction>> cir) {
        Registry.register(registry, MineCells.createId("image_map"), ImageDensityFunction.CODEC.codec());
    }
}
