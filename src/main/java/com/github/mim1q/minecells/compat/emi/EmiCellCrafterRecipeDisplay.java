package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiCellCrafterRecipeDisplay implements EmiRecipe {
  private final CellForgeRecipe recipe;

  public EmiCellCrafterRecipeDisplay(CellForgeRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public EmiRecipeCategory getCategory() {
    return MineCellsEmiPlugin.CELL_CRAFTER_CATEGORY;
  }

  @Override
  @Nullable
  public Identifier getId() {
    return recipe.id();
  }

  @Override
  public List<EmiIngredient> getInputs() {
    return recipe.getIngredients().stream().map(EmiIngredient::of).toList();
  }

  @Override
  public List<EmiStack> getOutputs() {
    return List.of(EmiStack.of(recipe.getOutput(null)));
  }

  @Override
  public int getDisplayWidth() {
    return getInputs().size() * 20;
  }

  @Override
  public int getDisplayHeight() {
    return 58;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    var x = getDisplayWidth() / 2 - (getInputs().size() * 10) + 2;
    for (var input : getInputs()) {
      widgets.addSlot(input, x, 0);
      x += 20;
    }

    widgets.addSlot(getOutputs().get(0), getDisplayWidth() / 2 - 8, 40);
  }
}
