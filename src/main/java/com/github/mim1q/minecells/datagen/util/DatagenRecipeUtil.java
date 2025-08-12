package com.github.mim1q.minecells.datagen.util;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;

public interface DatagenRecipeUtil extends DatagenUtils {
  default void addStonecutterRecipe(ItemConvertible from, ItemConvertible to, int count) {
    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.offerStonecuttingRecipe(it, RecipeCategory.BUILDING_BLOCKS, to, from, count);
    });
  }
}
