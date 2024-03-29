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
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public record CellForgeRecipe(
  Identifier id,
  List<ItemStack> ingredients,
  ItemStack output,
  Optional<Identifier> requiredAdvancement,
  int priority,
  Category category
) implements Recipe<CellForgeInventory> {

  public static final Codec<CellForgeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.list(ItemStack.CODEC).fieldOf("input").forGetter(CellForgeRecipe::ingredients),
    ItemStack.CODEC.fieldOf("output").forGetter(CellForgeRecipe::output),
    Identifier.CODEC.optionalFieldOf("advancement").forGetter(CellForgeRecipe::requiredAdvancement),
    Codec.INT.optionalFieldOf("priority", 0).forGetter(CellForgeRecipe::priority),
    StringIdentifiable.createCodec(Category::values).optionalFieldOf("category", Category.OTHER).forGetter(CellForgeRecipe::category)
  ).apply(instance, CellForgeRecipe::create));

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private static CellForgeRecipe create(
    List<ItemStack> ingredients,
    ItemStack output,
    Optional<Identifier> requiredAdvancement,
    int priority,
    Category category
  ) {
    return new CellForgeRecipe(null, ingredients, output, requiredAdvancement, priority, category);
  }

  public CellForgeRecipe withId(Identifier newId) {
    return new CellForgeRecipe(newId, ingredients, output, requiredAdvancement, priority, category);
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

  @Override
  public ItemStack getOutput(DynamicRegistryManager registryManager) {
    return output;
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

  public enum Category implements StringIdentifiable {
    GEAR("gear"),
    DECORATION("decoration"),
    OTHER("other");

    private final String name;

    Category(String name) {
      this.name = name;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
