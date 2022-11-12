package com.github.mim1q.minecells.block.inventory;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import com.github.mim1q.minecells.network.SyncCellForgeRecipeS2CPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CellForgeBlueprintInventory implements Inventory {
  private final DefaultedList<ItemStack> stacks;
  private final CellForgeScreenHandler handler;
  private final List<CellForgeRecipe> recipes = new ArrayList<>();
  private CellForgeRecipe selectedRecipe = null;
  private final PlayerEntity player;
  private int selectedRecipeIndex = -1;

  public CellForgeBlueprintInventory(PlayerEntity player, CellForgeScreenHandler handler) {
    var recipes = new ArrayList<>(player.getWorld().getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE));
    var size = Math.max(27, MathHelper.ceil(recipes.size() / 9.0F) * 9);
    this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    this.player = player;
    this.handler = handler;

    if (!player.getWorld().isClient()) {
      Comparator<CellForgeRecipe> comparator = Comparator.comparingInt(CellForgeRecipe::getPriorityInt).reversed();
      Comparator<CellForgeRecipe> comparator2 = comparator.thenComparing(recipe -> recipe.getId().toString());
      recipes.sort(comparator2);
      int i = 0;
      for (CellForgeRecipe recipe : recipes) {
        if (canAddRecipe((ServerPlayerEntity) player, recipe)) {
          stacks.set(i, recipe.getOutput());
          this.recipes.add(recipe);
          i++;
        }
      }
    }
  }

  private static boolean canAddRecipe(ServerPlayerEntity player, CellForgeRecipe recipe) {
    if (recipe.getRequiredAdvancement().isPresent()) {
      Identifier advancementId = recipe.getRequiredAdvancement().get();
      MinecraftServer server = player.getServer();
      if (server == null) {
        MineCells.LOGGER.warn("Failed to check advancement for recipe " + recipe.getId());
        return false;
      }
      Advancement advancement = server.getAdvancementLoader().get(advancementId);
      return advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();
    }
    return true;
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
    if (this.player.getWorld().isClient()) {
      return ItemStack.EMPTY;
    }
    if (getStack(slot).isEmpty() || recipes.size() <= slot) {
      return ItemStack.EMPTY;
    }
    int lastIndex = this.selectedRecipeIndex;
    this.selectedRecipe = recipes.get(slot);
    this.selectedRecipeIndex = slot;
    if (lastIndex != this.selectedRecipeIndex) {
      this.handler.clearCraftingInventory();
      ServerPlayNetworking.send(
        (ServerPlayerEntity) this.player,
        SyncCellForgeRecipeS2CPacket.ID,
        new SyncCellForgeRecipeS2CPacket(selectedRecipe, slot)
      );
    }
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
    return true;
  }

  @Override
  public void clear() {

  }

  public CellForgeRecipe getSelectedRecipe() {
    return selectedRecipe;
  }

  public int getSelectedRecipeIndex() {
    return selectedRecipeIndex;
  }

  public void setSelectedRecipe(CellForgeRecipe selectedRecipe, int slot) {
    this.selectedRecipe = selectedRecipe;
    this.selectedRecipeIndex = slot;
  }

  public int getRows() {
    return MathHelper.ceil(stacks.size() / 9.0);
  }
}
