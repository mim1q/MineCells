package com.github.mim1q.minecells.recipe;

import com.github.mim1q.minecells.MineCells;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class CellForgeRecipeSerializer implements RecipeSerializer<CellForgeRecipe> {
  public static final CellForgeRecipeSerializer INSTANCE = new CellForgeRecipeSerializer();

  @Override
  public MapCodec<CellForgeRecipe> codec() {
    return CellForgeRecipe.CODEC;
  }

  @Override
  public PacketCodec<RegistryByteBuf, CellForgeRecipe> packetCodec() {
    return CellForgeRecipe.PACKET_CODEC;
  }
}
