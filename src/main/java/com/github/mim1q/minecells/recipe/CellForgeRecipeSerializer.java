package com.github.mim1q.minecells.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class CellForgeRecipeSerializer implements RecipeSerializer<CellForgeRecipe> {
  public static final CellForgeRecipeSerializer INSTANCE = new CellForgeRecipeSerializer();

  @Override
  public CellForgeRecipe read(Identifier id, JsonObject json) {
    return CellForgeRecipe.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.out::println).getFirst().withId(id);
  }

  @Override
  @SuppressWarnings("deprecation")
  public CellForgeRecipe read(Identifier id, PacketByteBuf buf) {
    return buf.decode(CellForgeRecipe.CODEC).withId(id);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void write(PacketByteBuf buf, CellForgeRecipe recipe) {
    buf.encode(CellForgeRecipe.CODEC, recipe);
  }
}
