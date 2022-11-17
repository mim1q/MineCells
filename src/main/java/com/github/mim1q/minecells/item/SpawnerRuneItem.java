package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.spawnerrune.EntryList;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnerRuneItem extends BlockItem {
  public SpawnerRuneItem(Settings settings) {
    super(MineCellsBlocks.SPAWNER_RUNE, settings);
  }

  public static ItemStack withEntryList(EntryList entryList) {
    ItemStack stack = new ItemStack(MineCellsBlocks.SPAWNER_RUNE);
    stack.getOrCreateNbt().put("entryList", entryList.toNbt());
    stack.getOrCreateSubNbt("BlockEntityTag").put("entryList", entryList.toNbt());
    return stack;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    NbtList entryList = stack.getOrCreateNbt().getList("entryList", 10);
    if (entryList == null) {
      return;
    }
    for (int i = 0; i < entryList.size(); i++) {
      NbtCompound entry = entryList.getCompound(i);
      tooltip.add(Text.literal("[" + entry.getInt("weight") + "] " + entry.getString("entityId")).formatted(Formatting.DARK_GRAY));
    }
  }
}
