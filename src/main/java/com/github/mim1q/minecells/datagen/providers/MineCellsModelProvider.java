package com.github.mim1q.minecells.datagen.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class MineCellsModelProvider extends FabricModelProvider {
  public MineCellsModelProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

  }

  @Override
  public void generateItemModels(ItemModelGenerator itemModelGenerator) {

  }
}
