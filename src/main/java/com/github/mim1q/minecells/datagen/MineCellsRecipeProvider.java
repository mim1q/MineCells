package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;

import java.util.function.Consumer;

public class MineCellsRecipeProvider extends FabricRecipeProvider {
  public MineCellsRecipeProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator);
  }

  @Override
  protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
    ShapelessRecipeJsonBuilder.create(MineCellsBlocks.PUTRID_PLANKS, 4)
      .input(MineCellsBlocks.PUTRID_LOG)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.PUTRID_LOG))
      .offerTo(exporter);
    ShapelessRecipeJsonBuilder.create(MineCellsBlocks.PUTRID_PLANKS, 4)
      .input(MineCellsBlocks.STRIPPED_PUTRID_LOG)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.STRIPPED_PUTRID_LOG))
      .offerTo(exporter, MineCells.createId("stripped_putrid_log_to_planks"));
    ShapelessRecipeJsonBuilder.create(MineCellsBlocks.PUTRID_PLANKS, 4)
      .input(MineCellsBlocks.PUTRID_WOOD)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.PUTRID_WOOD))
      .offerTo(exporter, MineCells.createId("putrid_wood_to_planks"));
    ShapelessRecipeJsonBuilder.create(MineCellsBlocks.PUTRID_PLANKS, 4)
      .input(MineCellsBlocks.STRIPPED_PUTRID_WOOD)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.STRIPPED_PUTRID_WOOD))
      .offerTo(exporter, MineCells.createId("stripped_putrid_wood_to_planks"));

    ShapedRecipeJsonBuilder.create(MineCellsBlocks.PUTRID_WOOD, 3)
      .pattern("##")
      .pattern("##")
      .input('#', MineCellsBlocks.PUTRID_LOG)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.PUTRID_LOG))
      .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(MineCellsBlocks.STRIPPED_PUTRID_WOOD, 3)
      .pattern("##")
      .pattern("##")
      .input('#', MineCellsBlocks.STRIPPED_PUTRID_LOG)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsBlocks.STRIPPED_PUTRID_LOG))
      .offerTo(exporter);

    generateStairsAndSlab(exporter, MineCellsBlocks.PUTRID_PLANKS, MineCellsBlocks.PUTRID_STAIRS, MineCellsBlocks.PUTRID_SLAB);
  }

  public void generateStairsAndSlab(
    Consumer<RecipeJsonProvider> exporter,
    ItemConvertible base,
    ItemConvertible stairs,
    ItemConvertible slab
  ) {
    ShapedRecipeJsonBuilder.create(stairs, 4)
      .pattern("#  ")
      .pattern("## ")
      .pattern("###")
      .input('#', base)
      .criterion("has_item", RecipeProvider.conditionsFromItem(base))
      .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(stairs, 4)
      .pattern("  #")
      .pattern(" ##")
      .pattern("###")
      .input('#', base)
      .criterion("has_item", RecipeProvider.conditionsFromItem(base))
      .offerTo(exporter, getItemPath(stairs) + "_flipped");

    ShapedRecipeJsonBuilder.create(slab, 6)
      .pattern("###")
      .input('#', base)
      .criterion("has_item", RecipeProvider.conditionsFromItem(base))
      .offerTo(exporter);
  }
}
