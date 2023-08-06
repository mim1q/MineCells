package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.fluid.AncientSewageFluid;
import com.github.mim1q.minecells.block.fluid.SewageFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MineCellsFluids {

  public static FlowableFluid STILL_SEWAGE = Registry.register(Registries.FLUID, MineCells.createId("sewage"), new SewageFluid.Still());
  public static FlowableFluid FLOWING_SEWAGE = Registry.register(Registries.FLUID, MineCells.createId("flowing_sewage"), new SewageFluid.Flowing());

  public static FlowableFluid STILL_ANCIENT_SEWAGE = Registry.register(Registries.FLUID, MineCells.createId("ancient_sewage"), new AncientSewageFluid.Still());
  public static FlowableFluid FLOWING_ANCIENT_SEWAGE = Registry.register(Registries.FLUID, MineCells.createId("flowing_ancient_sewage"), new AncientSewageFluid.Flowing());

  public static void init() { }
}
