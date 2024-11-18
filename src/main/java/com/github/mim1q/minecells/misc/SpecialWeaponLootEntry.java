package com.github.mim1q.minecells.misc;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;

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
  private final List<Entry> entryList;

  private SpecialWeaponLootEntry(List<Entry> entryList) {
    super(new LootCondition[0]);
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

  public static class Serializer implements JsonSerializer<SpecialWeaponLootEntry> {
    @Override
    public void toJson(JsonObject json, SpecialWeaponLootEntry object, JsonSerializationContext context) {
      var entries = new JsonArray();
      json.add("entries", entries);
      for (var entry : object.entryList) {
        var entryJson = new JsonObject();
        entryJson.addProperty("weight", entry.weight());
        entryJson.addProperty("dimension_level", entry.dimensionLevel());
        entryJson.addProperty("advancement", entry.advancement().map(Identifier::toString).orElse(null));
        entryJson.add("loot", context.serialize(entry.loot()));
        entries.add(entryJson);
      }
    }

    @Override
    public SpecialWeaponLootEntry fromJson(JsonObject json, JsonDeserializationContext context) {
      var entries = json.getAsJsonArray("entries");
      var entryList = entries.asList().stream()
        .map(entry -> {
          var entryJson = entry.getAsJsonObject();
          return new Entry(
            context.deserialize(entryJson.get("loot"), LootPoolEntry.class),
            entryJson.get("weight").getAsInt(),
            entryJson.get("dimension_level").getAsInt(),
            Optional.ofNullable(entryJson.get("advancement")).map(e -> new Identifier(e.getAsString()))
          );
        })
        .toList();
      return new SpecialWeaponLootEntry(entryList);
    }
  }
}
