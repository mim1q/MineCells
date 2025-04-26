package com.github.mim1q.minecells.recipe;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record PlayerInventoryInput(
  PlayerInventory inventory
) implements RecipeInput {
  @Override
  public ItemStack getStackInSlot(int slot) {
    return inventory.getStack(slot);
  }

  @Override
  public int getSize() {
    return inventory.size();
  }
}
