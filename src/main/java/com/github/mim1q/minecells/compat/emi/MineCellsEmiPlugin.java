package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Comparator;

public class MineCellsEmiPlugin implements EmiPlugin {
  public static final Identifier CELL_CRAFTER_ID = MineCells.createId("cell_crafter");

  public static final EmiRecipeCategory CELL_CRAFTER_CATEGORY = new EmiRecipeCategory(
    CELL_CRAFTER_ID, EmiStack.of(MineCellsBlocks.CELL_CRAFTER)
  );

  @Override
  public void register(EmiRegistry registry) {
    registry.addCategory(CELL_CRAFTER_CATEGORY);
    registry.addWorkstation(
      CELL_CRAFTER_CATEGORY,
      EmiIngredient.of(Ingredient.ofItems(MineCellsBlocks.CELL_CRAFTER))
    );

    var recipes = registry.getRecipeManager()
      .listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE)
      .stream()
      .sorted(Comparator
        .comparingInt((CellForgeRecipe a) -> a.category().ordinal())
        .thenComparing((a, b) -> b.priority() - a.priority()))
      .toList();

    for (var recipe : recipes) {
      registry.addRecipe(
        new EmiCellCrafterRecipeDisplay(recipe)
      );
    }
  }
}
