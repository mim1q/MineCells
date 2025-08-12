package com.github.mim1q.minecells.datagen.util;

import com.github.mim1q.minecells.datagen.util.specific.CellCrafterRecipeProvider;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.mim1q.minecells.datagen.util.DatagenModelUtils.getItemId;

public abstract class AllDatagenUtils implements
  DataGeneratorEntrypoint,
  DatagenBlockSetUtils {

  public abstract void initialize();

  private InitializerHolder initializers = InitializerHolder.createEmpty();

  @Override
  public InitializerHolder getInitializers() {
    return initializers;
  }

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    this.initializers = InitializerHolder.createEmpty();
    initialize();

    var pack = fabricDataGenerator.createPack();

    pack.addProvider(Models::new);
    pack.addProvider(Recipe::new);
    pack.addProvider(CellCrafterRecipes::new);

    // Loot tables
    List.of(
      LootContextTypes.EMPTY, LootContextTypes.CHEST, LootContextTypes.COMMAND, LootContextTypes.SELECTOR,
      LootContextTypes.FISHING, LootContextTypes.ENTITY, LootContextTypes.EQUIPMENT, LootContextTypes.ARCHAEOLOGY,
      LootContextTypes.GIFT, LootContextTypes.BARTER, LootContextTypes.VAULT, LootContextTypes.ADVANCEMENT_REWARD,
      LootContextTypes.ADVANCEMENT_ENTITY, LootContextTypes.ADVANCEMENT_LOCATION, LootContextTypes.BLOCK_USE,
      LootContextTypes.GENERIC,
      LootContextTypes.BLOCK, LootContextTypes.SHEARING, LootContextTypes.ENCHANTED_DAMAGE,
      LootContextTypes.ENCHANTED_ITEM,
      LootContextTypes.ENCHANTED_LOCATION, LootContextTypes.ENCHANTED_ENTITY, LootContextTypes.HIT_BLOCK
    ).forEach(it -> pack.addProvider(createOtherLootTable(it)));

    pack.addProvider(BlockLootTable::new);

    // Tags
    pack.addProvider(BlockTags::new);
    pack.addProvider(ItemTags::new);
    pack.addProvider(FluidTags::new);
    pack.addProvider(EntityTags::new);
    pack.addProvider(BlockEntityTags::new);
    pack.addProvider(EnchantmentTags::new);

    // Advancements
    pack.addProvider(Advancement::new);
  }

  private class Models extends FabricModelProvider {
    public Models(FabricDataOutput output) {
      super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
      getInitializers().blockState().forEach(it -> it.accept(blockStateModelGenerator));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
      getInitializers().itemModel().forEach(it -> it.accept(itemModelGenerator));
    }
  }

  private class Recipe extends FabricRecipeProvider {
    public Recipe(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
      getInitializers().recipe().forEach(it -> it.accept(exporter));
    }
  }

  private class BlockLootTable extends FabricBlockLootTableProvider {
    protected BlockLootTable(
      FabricDataOutput dataOutput,
      CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
      super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
      getInitializers().blockLootTable().forEach(it -> it.accept(this));
    }
  }

  private FabricDataGenerator.Pack.RegistryDependentFactory<SimpleFabricLootTableProvider> createOtherLootTable(
    LootContextType type
  ) {
    return (output, registryLookup) -> new OtherLootTable(output, registryLookup, type);
  }

  private class OtherLootTable extends SimpleFabricLootTableProvider {
    public OtherLootTable(
      FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup,
      LootContextType lootContextType
    ) {
      super(output, registryLookup, lootContextType);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<net.minecraft.loot.LootTable>, net.minecraft.loot.LootTable.Builder> lootTableBiConsumer) {
      var list = getInitializers().otherLootTable().get(lootContextType);
      if (list != null) {
        list.forEach(it -> it.accept(lootTableBiConsumer));
      }
    }
  }

  private class Advancement extends FabricAdvancementProvider {
    protected Advancement(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
      super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
      getInitializers().advancements().forEach(consumer);
    }
  }

  private static <T> void configureTags(
    Function<TagKey<T>, FabricTagProvider<T>.FabricTagBuilder> provider,
    Map<TagKey<T>, List<Consumer<FabricTagProvider<T>.FabricTagBuilder>>> map
  ) {
    map.forEach((tag, values) -> {
      var builder = provider.apply(tag);
      values.forEach(it -> it.accept(builder));
    });
  }

  private class BlockTags extends FabricTagProvider.BlockTagProvider {
    public BlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().blockTags);
    }
  }

  private class ItemTags extends FabricTagProvider.ItemTagProvider {
    public ItemTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().itemTags);
    }
  }

  private class BlockEntityTags extends FabricTagProvider.BlockEntityTypeTagProvider {
    public BlockEntityTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().blockEntityTags);
    }
  }

  private class FluidTags extends FabricTagProvider.FluidTagProvider {
    public FluidTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().fluidTags);
    }
  }

  private class EnchantmentTags extends FabricTagProvider.EnchantmentTagProvider {
    public EnchantmentTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().enchantmentTags);
    }
  }

  private class EntityTags extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
      configureTags(this::getOrCreateTagBuilder, getInitializers().tags().entityTags);
    }
  }

  private class CellCrafterRecipes extends CellCrafterRecipeProvider {
    protected CellCrafterRecipes(
      FabricDataOutput dataOutput,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
      super(dataOutput, registriesFuture);
    }

    @Override
    protected void configure(BiConsumer<Identifier, CellForgeRecipe> provider, RegistryWrapper.WrapperLookup lookup) {
      getInitializers().cellCrafterRecipes().forEach(it -> {
        provider.accept(it.id(), it);
      });
    }
  }
}
