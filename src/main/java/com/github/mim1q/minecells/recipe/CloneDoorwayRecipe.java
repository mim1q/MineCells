package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.item.DoorwayItem;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.recipe.ClearDoorwayRecipe.getNonEmptyStacks;

public class CloneDoorwayRecipe extends SpecialCraftingRecipe {
  public CloneDoorwayRecipe(Identifier id, CraftingRecipeCategory category) {
    super(id, category);
  }

  @Override
  public boolean matches(RecipeInputInventory inventory, World world) {
    var stacks = getNonEmptyStacks(inventory);
    if (stacks.size() != 2) return false;
    var stack1 = stacks.get(0);
    var stack2 = stacks.get(1);

    return stack1.getItem() instanceof DoorwayItem && stack2.isOf(stack1.getItem()) && (
      (ClearDoorwayRecipe.hasBlockEntityTag(stack1) && !ClearDoorwayRecipe.hasBlockEntityTag(stack2))
        || (!ClearDoorwayRecipe.hasBlockEntityTag(stack1) && ClearDoorwayRecipe.hasBlockEntityTag(stack2))
    );
  }

  @Override
  public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
    var stackWithNbt = getNonEmptyStacks(inventory)
      .stream()
      .filter(ClearDoorwayRecipe::hasBlockEntityTag)
      .findFirst()
      .orElse(null);
    if (stackWithNbt == null) return ItemStack.EMPTY;
    return stackWithNbt.copyWithCount(2);
  }

  @Override
  public boolean fits(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MineCellsRecipeTypes.CLONE_DOORWAY_RECIPE_SERIALIZER;
  }
}
