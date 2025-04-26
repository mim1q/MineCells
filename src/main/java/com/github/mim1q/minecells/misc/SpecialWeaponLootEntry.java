package com.github.mim1q.minecells.misc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A loot entry that only generates certain loot if the player has completed the specified advancement.
 * Entries can be restricted to a certain "dimension level", which is higher the deeper the player ventures into
 * the world of Mine Cells.
 */
public class SpecialWeaponLootEntry extends LootPoolEntry {
  public static final MapCodec<SpecialWeaponLootEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    Entry.CODEC.codec().listOf().fieldOf("entries").forGetter(it -> it.entryList)
  ).apply(instance, SpecialWeaponLootEntry::new));

  private final List<Entry> entryList;

  private SpecialWeaponLootEntry(List<Entry> entryList) {
    super(List.of());
    this.entryList = entryList;
  }

  @Override
  public LootPoolEntryType getType() {
    return MineCells.SPECIAL_WEAPON_LOOT_ENTRY;
  }

  @Override
  public boolean expand(LootContext context, Consumer<LootChoice> choiceConsumer) {
    choiceConsumer.accept(this.new Choice());
    return true;
  }

  private class Choice implements LootChoice {
    @Override
    public int getWeight(float luck) {
      return 1;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
      var entity = context.get(LootContextParameters.THIS_ENTITY);
      if (!(entity instanceof ServerPlayerEntity player)) {
        return;
      }

      var map = new HashMap<Entry, Integer>(); // Entries with their weights adjusted by the other parameters
      var totalWeight = 0;

      for (var entry : entryList) {
        if (!shouldUseEntry(entry.advancement(), player)) continue;

        var weight = entry.weight();
        var dimensionLevel = entry.dimensionLevel();
        var dimension = MineCellsDimension.of(player.getWorld());

        if (dimension == null) break;

        var currentDimensionLevel = dimension.getDimensionLevel();
        if (dimensionLevel > currentDimensionLevel) continue;
        if (dimensionLevel == currentDimensionLevel) weight *= 2;

        map.put(entry, weight);
        totalWeight += weight;
      }

      var randomWeight = player.getRandom().nextInt(totalWeight);
      for (var entry : map.keySet()) {
        randomWeight -= map.get(entry);
        if (randomWeight < 0) {
          entry.generateLoot(lootConsumer, context);
          return;
        }
      }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean shouldUseEntry(Optional<Identifier> advancementId, ServerPlayerEntity player) {
      if (advancementId.isEmpty()) {
        return true;
      }
      var advancement = player.server.getAdvancementLoader().get(advancementId.get());
      return advancement == null || player.getAdvancementTracker().getProgress(advancement).isDone();
    }
  }

  private record Entry(
    LootPoolEntry loot,
    int weight,
    int dimensionLevel,
    Optional<Identifier> advancement
  ) implements LootChoice {
    public static final MapCodec<Entry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      LootPoolEntryTypes.CODEC.fieldOf("loot").forGetter(Entry::loot),
      Codec.INT.fieldOf("weight").orElse(1).forGetter(Entry::weight),
      Codec.INT.fieldOf("dimension_level").orElse(0).forGetter(Entry::dimensionLevel),
      Identifier.CODEC.optionalFieldOf("advancement").orElse(null).forGetter(Entry::advancement)
    ).apply(instance, Entry::new));

    @Override
    public int getWeight(float luck) {
      return weight;
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
      List<LootChoice> choices = new ArrayList<>();
      loot.expand(context, choices::add);
      choices.forEach(choice -> choice.generateLoot(lootConsumer, context));
    }
  }
}
