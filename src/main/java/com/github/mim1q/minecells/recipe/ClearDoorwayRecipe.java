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

import java.util.List;

public class ClearDoorwayRecipe extends SpecialCraftingRecipe {
  public ClearDoorwayRecipe(Identifier id, CraftingRecipeCategory category) {
    super(id, category);
  }

  @Override
  public boolean matches(RecipeInputInventory inventory, World world) {
    var stacks = getNonEmptyStacks(inventory);
    if (stacks.size() != 1) return false;
    var stack = stacks.get(0);
    return stack.getItem() instanceof DoorwayItem && hasBlockEntityTag(stack);
  }

  @Override
  public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
    var stacks = getNonEmptyStacks(inventory);
    var stack = stacks.get(0).copy();
    stack.removeSubNbt("BlockEntityTag");
    return stack;
  }

  @Override
  public boolean fits(int width, int height) {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return MineCellsRecipeTypes.CLEAR_DOORWAY_RECIPE_SERIALIZER;
  }

  public static List<ItemStack> getNonEmptyStacks(RecipeInputInventory inventory) {
    return inventory.getInputStacks().stream().filter(stack -> !stack.isEmpty()).toList();
  }

  @SuppressWarnings("DataFlowIssue")
  public static boolean hasBlockEntityTag(ItemStack stack) {
    return stack.getSubNbt("BlockEntityTag") != null && !stack.getSubNbt("BlockEntityTag").isEmpty();
  }
}
