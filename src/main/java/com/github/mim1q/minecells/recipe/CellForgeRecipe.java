package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
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
    Identifier.CODEC.optionalFieldOf("blueprint").forGetter(CellForgeRecipe::getBlueprint)
  ).apply(instance, CellForgeRecipe::new));

  public static final Identifier ID = MineCells.createId("cell_forge_recipe");

  private final List<ItemStack> ingredients;
  private final int cells;
  private final ItemStack output;
  private final Optional<Identifier> requiredBlueprint;

  public CellForgeRecipe(List<ItemStack> ingredients, int cells, ItemStack output, Optional<Identifier> requiredBlueprint) {
    this.ingredients = ingredients;
    this.cells = cells;
    this.output = output;
    this.requiredBlueprint = requiredBlueprint;
  }

  @Override
  public boolean matches(CellForgeInventory inventory, World world) {
    return false;
  }

  @Override
  public ItemStack craft(CellForgeInventory inventory) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean fits(int width, int height) {
    return false;
  }

  public List<ItemStack> getInput() {
    return ingredients;
  }

  public int getCells() {
    return cells;
  }

  @Override
  public ItemStack getOutput() {
    return this.output;
  }

  public Optional<Identifier> getBlueprint() {
    return requiredBlueprint;
  }

  @Override
  public Identifier getId() {
    return ID;
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
