package com.github.mim1q.minecells.compat.rei;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.github.mim1q.minecells.compat.emi.EmiCellCrafterRecipeDisplay.ARROW_TEXTURE;

public class MineCellsReiPlugin implements REIClientPlugin {
  private static final CategoryIdentifier<CellCrafterDisplay> CELL_CRAFTER_CATEGORY_ID
    = CategoryIdentifier.of(MineCells.createId("cell_crafter"));

  private static final DisplayCategory<CellCrafterDisplay> CELL_CRAFTER_CATEGORY = new DisplayCategory<>() {

    @Override
    public CategoryIdentifier<? extends CellCrafterDisplay> getCategoryIdentifier() {
      return CELL_CRAFTER_CATEGORY_ID;
    }

    @Override
    public Text getTitle() {
      return Text.translatable("emi.category.minecells.cell_crafter");
    }

    @Override
    public Renderer getIcon() {
      return EntryStacks.of(MineCellsBlocks.CELL_CRAFTER);
    }

    @Override
    public List<Widget> setupDisplay(CellCrafterDisplay display, Rectangle bounds) {
      var widgets = new ArrayList<Widget>();

      var widgetX = bounds.width / 2 - (display.getInputEntries().size() * 10) + 2;
      for (var input : display.getInputEntries()) {
        widgets.add(Widgets.createSlot(new Point(widgetX, 0)).entries(input));
        widgetX += 20;
      }

      var hasAdvancement = display.recipe.requiredAdvancement().isPresent();

      var arrow =
        Widgets.createTexturedWidget(
          ARROW_TEXTURE, bounds.width / 2 - 8, 19,
          hasAdvancement ? 24 : 16, 16,
          0, 0,
          hasAdvancement ? 24 : 16, 16,
          32, 32
        );
      widgets.add(arrow);

      if (hasAdvancement) {
        var advancementKey = Util.createTranslationKey("advancements", display.recipe.requiredAdvancement().orElseThrow()) + ".description";
        widgets.add(Widgets.createTooltip(
          new Rectangle(bounds.width / 2 - 8, 19, 16, 16),
          Text.translatable("block.minecells.cell_crafter.requirement").formatted(Formatting.RED),
          Text.translatable(advancementKey).formatted(Formatting.GRAY)
        ));
      }
      widgets.add(Widgets.createSlot(
          new Point(bounds.width / 2 - 8, 34))
        .entry(EntryStacks.of(display.recipe.getOutput(null)))
      );

      return widgets;
    }
  };

  @Override
  public void registerCategories(CategoryRegistry registry) {
    registry.add(CELL_CRAFTER_CATEGORY);
    registry.addWorkstations(CELL_CRAFTER_CATEGORY_ID, EntryStacks.of(MineCellsBlocks.CELL_CRAFTER));
  }

  @Override
  public void registerDisplays(DisplayRegistry registry) {
    var recipes = registry.getRecipeManager()
      .listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE)
      .stream()
      .sorted(Comparator
        .comparingInt((CellForgeRecipe a) -> a.category().ordinal())
        .thenComparing((a, b) -> b.priority() - a.priority()))
      .toList();

    for (var recipe : recipes) {
      registry.add(new CellCrafterDisplay(recipe));
    }
  }

  private static class CellCrafterDisplay extends BasicDisplay {
    public final CellForgeRecipe recipe;

    public CellCrafterDisplay(CellForgeRecipe recipe) {
      super(
        mapStacksToIngredient(recipe.getIngredients()),
        List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null))))
      );

      this.recipe = recipe;
    }

    private static List<EntryIngredient> mapStacksToIngredient(List<Ingredient> ingredients) {
      var result = new ArrayList<EntryIngredient>();
      for (var ingredient : ingredients) {
        var stacks = Arrays.stream(ingredient.getMatchingStacks()).map(EntryStacks::of).toList();
        result.add(EntryIngredient.of(stacks));
      }
      return result;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
      return CELL_CRAFTER_CATEGORY_ID;
    }
  }
}
