package com.github.mim1q.minecells.block.inventory;

import com.github.mim1q.minecells.network.SyncCellForgeRecipeS2CPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class CellForgeBlueprintInventory implements Inventory {
  private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
  private final List<CellForgeRecipe> recipes = new ArrayList<>();
  private CellForgeRecipe selectedRecipe = null;
  private final PlayerEntity player;

  public CellForgeBlueprintInventory(PlayerEntity player) {
    this.player = player;
    if (!player.getWorld().isClient()) {
      this.recipes.addAll(player.getWorld().getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE));
      int i = 0;
      for (CellForgeRecipe recipe : recipes) {
        stacks.set(i, recipe.getOutput());
        i++;
      }
    }
  }

  @Override
  public int size() {
    return 3 * 9;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public ItemStack getStack(int slot) {
    return stacks.get(slot);
  }

  @Override
  public ItemStack removeStack(int slot, int amount) {
    if (getStack(slot).isEmpty() || recipes.size() <= slot) {
      return ItemStack.EMPTY;
    }
    this.selectedRecipe = recipes.get(slot);
    ServerPlayNetworking.send(
      (ServerPlayerEntity) this.player,
      SyncCellForgeRecipeS2CPacket.ID,
      new SyncCellForgeRecipeS2CPacket(selectedRecipe)
    );
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStack(int slot) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setStack(int slot, ItemStack stack) {
    stacks.set(slot, stack);
  }

  @Override
  public void markDirty() {

  }

  @Override
  public boolean canPlayerUse(PlayerEntity player) {
    return false;
  }

  @Override
  public void clear() {

  }
}
