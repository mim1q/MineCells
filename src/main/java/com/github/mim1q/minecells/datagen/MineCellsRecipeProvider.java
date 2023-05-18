package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;

import java.util.function.Consumer;

public class MineCellsRecipeProvider extends FabricRecipeProvider {
  public MineCellsRecipeProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator);
  }

  @Override
  protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(MineCellsBlocks.BIG_CHAIN, 2)
      .pattern("#")
      .pattern(".")
      .pattern("#")
      .input('#', Items.IRON_INGOT)
      .input('.', Items.IRON_NUGGET)
      .criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
      .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(MineCellsItems.ELEVATOR_MECHANISM, 1)
      .pattern(" # ")
      .pattern("RWR")
      .pattern(" C ")
      .input('#', Items.CHAIN)
      .input('R', Items.REDSTONE)
      .input('W', ItemTags.LOGS)
      .input('C', Items.COMPARATOR)
      .criterion("has_item", RecipeProvider.conditionsFromItem(Items.COMPARATOR))
      .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(MineCellsBlocks.ELEVATOR_ASSEMBLER, 2)
      .pattern("# #")
      .pattern("MPM")
      .pattern("# #")
      .input('#', Items.CHAIN)
      .input('M', MineCellsItems.ELEVATOR_MECHANISM)
      .input('P', ItemTags.PLANKS)
      .criterion("has_item", RecipeProvider.conditionsFromItem(MineCellsItems.ELEVATOR_MECHANISM))
      .offerTo(exporter);

    ShapedRecipeJsonBuilder.create(MineCellsBlocks.CAGE, 1)
      .pattern("TTT")
      .pattern("###")
      .pattern("TTT")
      .input('T', Items.IRON_TRAPDOOR)
      .input('#', Items.IRON_BARS)
      .criterion("has_item", RecipeProvider.conditionsFromItem(Items.IRON_BARS))
      .offerTo(exporter);
  }
}
