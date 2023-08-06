package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.recipe.CellForgeRecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MineCellsRecipeTypes {
  public static final RecipeType<CellForgeRecipe> CELL_FORGE_RECIPE_TYPE = RecipeType.register("minecells:cell_forge_recipe");

  public static void init() {
    Registry.register(Registries.RECIPE_SERIALIZER, "minecells:cell_forge_recipe", CellForgeRecipeSerializer.INSTANCE);
  }
}
