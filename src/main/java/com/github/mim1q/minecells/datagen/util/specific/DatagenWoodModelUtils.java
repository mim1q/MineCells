package com.github.mim1q.minecells.datagen.util.specific;

import com.github.mim1q.minecells.datagen.util.DatagenModelUtils;
import com.github.mim1q.minecells.datagen.util.DatagenTagUtils;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;

import static net.minecraft.data.client.BlockStateModelGenerator.createFenceGateBlockState;
import static net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem;
import static net.minecraft.data.server.recipe.RecipeProvider.hasItem;

public interface DatagenWoodModelUtils extends DatagenModelUtils, DatagenTagUtils {
  default void addFence(FenceBlock block, Block base) {
//    var texture = TextureMap.all(base);
//    getInitializers().blockState().add(it -> {
//
//      var fencePost = Models.FENCE_POST.upload(block, texture, it.modelCollector);
//      var fenceSide = Models.FENCE_SIDE.upload(block, texture, it.modelCollector);
//
//      it.blockStateCollector.accept(createFenceBlockState(
//        block,
//        fencePost,
//        fenceSide
//      ));
//    });
//
//    getInitializers().itemModel().add(it -> {
//      var fenceInventory = new Model(Optional.of(Identifier.of("block/fence_inventory")), Optional.of("inventory"), TextureKey.ALL);
//      it.register(block.asItem(), fenceInventory);
//    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createFenceRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addFenceGate(FenceGateBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var fenceGateOpen = Models.TEMPLATE_CUSTOM_FENCE_GATE_OPEN.upload(block, texture, it.modelCollector);
      var fenceGate = Models.TEMPLATE_CUSTOM_FENCE_GATE.upload(block, texture, it.modelCollector);
      var fenceGateWallOpen = Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN.upload(block, texture, it.modelCollector);
      var fenceGateWall = Models.TEMPLATE_CUSTOM_FENCE_GATE_WALL.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createFenceGateBlockState(
        block,
        fenceGateOpen,
        fenceGate,
        fenceGateWallOpen,
        fenceGateWall,
        true
      ));
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createFenceGateRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addSign(SignBlock block, WallSignBlock wallSign, Item signItem, Block base) {
    addParticleOnly(block, base);
    addParticleOnly(wallSign, base);

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createSignRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });

    addGeneratedItem(block);
  }

  default void addLeaves(LeavesBlock block, SaplingBlock sapling) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(block);
      var model = Models.LEAVES.upload(block, texture, it.modelCollector);
      it.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)));

      it.registerParentedItemModel(block, model);
    });

    getInitializers().blockLootTable().add(it -> {
      it.addDrop(block, it.leavesDrops(block, sapling, 0.1f));
    });

    addBlockTag(BlockTags.LEAVES, block);
  }
}
