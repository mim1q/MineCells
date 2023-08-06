package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CellForgeRecipe implements Recipe<CellForgeInventory> {

  public static final Codec<CellForgeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.list(ItemStack.CODEC).fieldOf("input").forGetter(CellForgeRecipe::getInput),
    Codec.INT.fieldOf("cells").forGetter(CellForgeRecipe::getCells),
    ItemStack.CODEC.fieldOf("output").forGetter(CellForgeRecipe::getOutput),
    Identifier.CODEC.optionalFieldOf("advancement").forGetter(CellForgeRecipe::getRequiredAdvancement),
    Codec.INT.optionalFieldOf("priority", 0).forGetter(CellForgeRecipe::getPriority)
  ).apply(instance, CellForgeRecipe::new));

  private Identifier id = null;
  private final List<ItemStack> ingredients;
  private final int cells;
  private final ItemStack output;
  private final Optional<Identifier> requiredAdvancement;
  private final int priority;

  public CellForgeRecipe(List<ItemStack> ingredients, int cells, ItemStack output, Optional<Identifier> requiredAdvancement, int priority) {
    this.ingredients = ingredients;
    this.cells = cells;
    this.output = output;
    this.requiredAdvancement = requiredAdvancement;
    this.priority = priority;
  }

  public CellForgeRecipe withId(Identifier id) {
    this.id = id;
    return this;
  }

  @Override
  public boolean matches(CellForgeInventory inventory, World world) {
    for (int i = 0; i < ingredients.size(); i++) {
      ItemStack ingredient = ingredients.get(i);
      ItemStack stack = inventory.getStack(i);
      if (!(ingredient.isOf(stack.getItem()) && ingredient.getCount() == stack.getCount())) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack craft(CellForgeInventory inventory, DynamicRegistryManager registryManager) {
    return null;
  }

  @Override
  public boolean fits(int width, int height) {
    return false;
  }

  public ItemStack getOutput() {
    return output;
  }

  @Override
  public ItemStack getOutput(DynamicRegistryManager registryManager) {
    return output;
  }

  public List<ItemStack> getInput() {
    return ingredients;
  }

  public int getCells() {
    return cells;
  }

  public Optional<Identifier> getRequiredAdvancement() {
    return requiredAdvancement;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public Identifier getId() {
    return this.id;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CellForgeRecipeSerializer.INSTANCE;
  }

  @Override
  public RecipeType<?> getType() {
    return MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE;
  }
}
