package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;

public record CellForgeRecipe(
  Identifier id,
  Map<Item, Integer> ingredients,
  ItemStack output,
  Optional<Identifier> requiredAdvancement,
  int priority,
  Category category
) implements Recipe<PlayerInventoryInput> {

  public static final MapCodec<CellForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    Codec.unboundedMap(Registries.ITEM.getCodec(), Codec.INT).fieldOf("input").forGetter(CellForgeRecipe::ingredients),
    ItemStack.CODEC.fieldOf("output").forGetter(CellForgeRecipe::output),
    Identifier.CODEC.optionalFieldOf("advancement").forGetter(CellForgeRecipe::requiredAdvancement),
    Codec.INT.optionalFieldOf("priority", 0).forGetter(CellForgeRecipe::priority),
    StringIdentifiable.createCodec(Category::values).optionalFieldOf("category", Category.OTHER).forGetter(CellForgeRecipe::category)
  ).apply(instance, CellForgeRecipe::create));

  public static final PacketCodec<RegistryByteBuf, CellForgeRecipe> PACKET_CODEC = PacketCodecs.registryCodec(CODEC.codec());

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private static CellForgeRecipe create(
    Map<Item, Integer> ingredients,
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
  public boolean matches(PlayerInventoryInput input, World world) {
    var inventory = input.inventory();
    for (var ingredient : ingredients.entrySet()) {
      if (inventory.count(ingredient.getKey()) < ingredient.getValue()) return false;
    }
    return true;
  }

  @Override
  public ItemStack craft(PlayerInventoryInput input, RegistryWrapper.WrapperLookup lookup) {
    var inventory = input.inventory();

    for (var ingredient : ingredients.entrySet()) {
      if (inventory.count(ingredient.getKey()) < ingredient.getValue()) return null;
      inventory.remove(stack -> stack.isOf(ingredient.getKey()), ingredient.getValue(), inventory);
    }
    return output.copy();
  }

  @Override
  public boolean fits(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
    return output;
  }

  @Override
  public DefaultedList<Ingredient> getIngredients() {
    return DefaultedList.copyOf(Ingredient.EMPTY,
      ingredients.entrySet().stream().map(entry -> {
        var stack = new ItemStack(entry.getKey());
        stack.setCount(entry.getValue());
        return Ingredient.ofStacks(stack);
      }).toArray(Ingredient[]::new));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CellForgeRecipeSerializer.INSTANCE;
  }

  @Override
  public RecipeType<?> getType() {
    return MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE;
  }

  @Override
  public boolean isIgnoredInRecipeBook() {
    return true;
  }

  public enum Category implements StringIdentifiable {
    GEAR("gear", MineCellsItems.BLOOD_SWORD),
    DECORATION("decoration", MineCellsBlocks.KINGS_CREST_FLAG),
    OTHER("other", MineCellsItems.RESET_RUNE);

    private final String name;
    public final Item displayItem;
    private final String translationKey;

    Category(String name, ItemConvertible displayItem) {
      this.name = name;
      this.displayItem = displayItem.asItem();
      this.translationKey = "block.minecells.cell_crafter.category." + name;
    }

    @Override
    public String asString() {
      return name;
    }

    public Text getName() {
      return Text.translatable(translationKey);
    }
  }
}
