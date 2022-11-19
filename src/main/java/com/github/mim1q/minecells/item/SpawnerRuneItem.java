package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.spawnerrune.EntryList;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneData;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpawnerRuneItem extends BlockItem {
  public SpawnerRuneItem(Settings settings) {
    super(MineCellsBlocks.SPAWNER_RUNE, settings);
  }

  public static ItemStack withData(String name, EntryList entryList, int maxCooldown, int minRolls, int maxRolls, float spawnRadius, float playerRange) {
    ItemStack stack = new ItemStack(MineCellsBlocks.SPAWNER_RUNE);
    SpawnerRuneData data = new SpawnerRuneData(name, entryList, maxCooldown, minRolls, maxRolls, spawnRadius, playerRange);
    data.writeNbt(stack.getOrCreateNbt());
    data.writeNbt(stack.getOrCreateSubNbt("BlockEntityTag"));
    return stack;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    SpawnerRuneData data = SpawnerRuneData.fromNbt(stack.getOrCreateNbt());
    tooltip.add(Text.literal("Cooldown: " + data.maxCooldown / 20.0F + "s").formatted(Formatting.DARK_GRAY));
    if (data.minRolls == data.maxRolls) {
      tooltip.add(Text.literal("Rolls: " + data.minRolls).formatted(Formatting.DARK_GRAY));
    } else {
      tooltip.add(Text.literal("Rolls: " + data.minRolls + "-" + data.maxRolls).formatted(Formatting.DARK_GRAY));
    }
    tooltip.add(Text.literal("Entities:").formatted(Formatting.DARK_GRAY));
    for (EntryList.Entry entry : data.entryList.entries) {
      tooltip.add(Text.literal("  [" + entry.weight + "] ").append(Text.translatable(entry.entityType.getTranslationKey())).formatted(Formatting.DARK_GRAY));
    }
  }

  @Override
  public Text getName(ItemStack stack) {
    return Text.literal("Spawner Rune").formatted(Formatting.GOLD).append(Text.literal(" (" + SpawnerRuneData.fromNbt(stack.getOrCreateNbt()).name + ")").formatted(Formatting.GRAY));
  }
}
