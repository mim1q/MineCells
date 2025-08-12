package com.github.mim1q.minecells.datagen.util.specific;

import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public abstract class CellCrafterRecipeProvider extends FabricCodecDataProvider<CellForgeRecipe> {
  protected CellCrafterRecipeProvider(
    FabricDataOutput dataOutput,
    CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
  ) {
    super(
      dataOutput,
      registriesFuture,
      DataOutput.OutputType.DATA_PACK,
      "recipe/cell_crafter",
      Codec.STRING.dispatch(
        e -> "minecells:cell_forge_recipe", // forced type key
        e -> CellForgeRecipe.CODEC
      )
    );
  }

  @Override
  public String getName() {
    return "minecells:cell_crafter_recipes";
  }
}
