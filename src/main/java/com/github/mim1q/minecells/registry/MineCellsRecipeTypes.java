package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.recipe.CellForgeRecipeSerializer;
import com.github.mim1q.minecells.recipe.ClearDoorwayRecipe;
import com.github.mim1q.minecells.recipe.CloneDoorwayRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MineCellsRecipeTypes {
  public static final RecipeType<CellForgeRecipe> CELL_FORGE_RECIPE_TYPE = RecipeType.register("minecells:cell_forge_recipe");

  public static final RecipeSerializer<ClearDoorwayRecipe> CLEAR_DOORWAY_RECIPE_SERIALIZER = register(
    "clear_doorway_recipe",
    new SpecialRecipeSerializer<>(ClearDoorwayRecipe::new)
  );

  public static final RecipeSerializer<CloneDoorwayRecipe> CLONE_DOORWAY_RECIPE_SERIALIZER = register(
    "clone_doorway_recipe",
    new SpecialRecipeSerializer<>(CloneDoorwayRecipe::new)
  );

  public static void init() {
    Registry.register(Registries.RECIPE_SERIALIZER, "minecells:cell_forge_recipe", CellForgeRecipeSerializer.INSTANCE);
  }

  private static <T extends Recipe<?>> RecipeSerializer<T> register(String id, RecipeSerializer<T> serializer) {
    return Registry.register(Registries.RECIPE_SERIALIZER, MineCells.createId(id), serializer);
  }
}
