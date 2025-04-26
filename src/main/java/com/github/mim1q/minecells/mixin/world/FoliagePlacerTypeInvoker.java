package com.github.mim1q.minecells.mixin.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FoliagePlacerType.class)
public interface FoliagePlacerTypeInvoker {
  @Invoker("register")
  static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, MapCodec<P> codec) {
    throw new AssertionError();
  }
}
