package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiCellCrafterRecipeDisplay implements EmiRecipe {
  public static final Identifier ARROW_TEXTURE = MineCells.createId("textures/gui/cell_crafter/emi_arrow.png");

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
    return 52;
  }

  @Override
  public void addWidgets(WidgetHolder widgets) {
    var widgetX = getDisplayWidth() / 2 - (getInputs().size() * 10) + 2;
    for (var input : getInputs()) {
      widgets.addSlot(input, widgetX, 0);
      widgetX += 20;
    }

    var hasAdvancement = recipe.requiredAdvancement().isPresent();

    var arrow = widgets.addTexture(
      ARROW_TEXTURE, getDisplayWidth() / 2 - 8, 19,
      hasAdvancement ? 24 : 16, 16,
      0, 0,
      hasAdvancement ? 24 : 16, 16,
      32, 32
    );

    if (hasAdvancement) {
      arrow.tooltip((x, y) -> {
        var advancementKey = Util.createTranslationKey("advancements", recipe.requiredAdvancement().orElseThrow()) + ".description";

        return List.of(
          TooltipComponent.of(Text.translatable("block.minecells.cell_crafter.requirement").formatted(Formatting.RED).asOrderedText()),
          TooltipComponent.of(Text.translatable(advancementKey).formatted(Formatting.GRAY).asOrderedText())
        );
      });
    }
    widgets.addSlot(getOutputs().get(0), getDisplayWidth() / 2 - 8, 34);
  }
}
