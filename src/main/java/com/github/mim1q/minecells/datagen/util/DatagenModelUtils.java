package com.github.mim1q.minecells.datagen.util;

import com.github.mim1q.minecells.block.ColoredTorchBlock;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.data.client.BlockStateModelGenerator.*;
import static net.minecraft.data.client.VariantSettings.MODEL;
import static net.minecraft.data.client.VariantSettings.Y;
import static net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem;
import static net.minecraft.data.server.recipe.RecipeProvider.hasItem;

public interface DatagenModelUtils extends DatagenUtils {
  default Identifier getBlockId(Block block) {
    return Registries.BLOCK.getId(block).withPrefixedPath("block/");
  }

  default Identifier getItemId(ItemConvertible item) {
    return Registries.ITEM.getId(item.asItem()).withPrefixedPath("item/");
  }

  default void addBlock(Block block) {
    getInitializers().blockState().add(it -> {
      it.registerSimpleCubeAll(block);

      it.registerParentedItemModel(block, getBlockId(block));
    });
  }

  default void addBlock(Block block, Block base, String suffix) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(getBlockId(base).withSuffixedPath(suffix));
      Models.CUBE_ALL.upload(block, texture, it.modelCollector);

      it.registerParentedItemModel(block, getBlockId(block));
    });
  }

  default void addParticleOnly(Block block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.particle(base);
      var model = Models.PARTICLE.upload(block, texture, it.modelCollector);
      it.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)));
    });
  }

  default void addGeneratedItem(ItemConvertible item) {
    getInitializers().itemModel().add(it -> {
      it.register(item.asItem(), Models.GENERATED);
    });
  }

  default void addPillar(Block block) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.sideEnd(block);
      var model = Models.CUBE_COLUMN.upload(block, texture, it.modelCollector);
      it.blockStateCollector.accept(createAxisRotatedBlockState(block, model));

      it.registerParentedItemModel(block, model);
    });
  }

  default void addStairs(StairsBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);
      var model = Models.STAIRS.upload(block, texture, it.modelCollector);
      var innerModel = Models.INNER_STAIRS.upload(block, texture, it.modelCollector);
      var outerModel = Models.OUTER_STAIRS.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createStairsBlockState(block, innerModel, model, outerModel));
      it.registerParentedItemModel(block, model);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createStairsRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addSlab(SlabBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var modelBottom = Models.SLAB.upload(block, texture, it.modelCollector);
      var modelTop = Models.SLAB_TOP.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createSlabBlockState(block, modelBottom, modelTop, getBlockId(base)));
      it.registerParentedItemModel(block, modelBottom);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addButton(ButtonBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var model = Models.BUTTON.upload(block, texture, it.modelCollector);
      var modelPressed = Models.BUTTON_PRESSED.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createButtonBlockState(block, model, modelPressed));
      it.registerParentedItemModel(block, Models.BUTTON_INVENTORY.upload(block, texture, it.modelCollector));
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.offerShapelessRecipe(it, block, base, RecipeCategory.BUILDING_BLOCKS.getName(), 1);
    });
  }

  default void addPressurePlate(AbstractPressurePlateBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var modelUp = Models.PRESSURE_PLATE_UP.upload(block, texture, it.modelCollector);
      var modelDown = Models.PRESSURE_PLATE_DOWN.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createPressurePlateBlockState(block, modelUp, modelDown));
      it.registerParentedItemModel(block, modelUp);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createPressurePlateRecipe(RecipeCategory.BUILDING_BLOCKS, block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addDoor(DoorBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      it.registerDoor(block);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createDoorRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addTrapdoor(TrapdoorBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      it.registerTrapdoor(block);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createTrapdoorRecipe(block, Ingredient.ofItems(base))
        .criterion(hasItem(base), conditionsFromItem(base))
        .offerTo(it);
    });
  }

  default void addWall(WallBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);
      var modelPost = Models.TEMPLATE_WALL_POST.upload(block, texture, it.modelCollector);
      var modelSide = Models.TEMPLATE_WALL_SIDE.upload(block, texture, it.modelCollector);
      var modelSideTall = Models.TEMPLATE_WALL_SIDE_TALL.upload(block, texture, it.modelCollector);
      var modelInventory = Models.WALL_INVENTORY.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createWallBlockState(block, modelPost, modelSide, modelSideTall));

      it.registerParentedItemModel(block, modelInventory);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.offerWallRecipe(it, RecipeCategory.BUILDING_BLOCKS, block, base);
    });
  }

  static BlockStateVariantMap createHorizontalRotateableCoordinates(Identifier model) {
    return BlockStateVariantMap.create(ColoredTorchBlock.FACING)
      .register(Direction.NORTH, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R0))
      .register(Direction.SOUTH, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R180))
      .register(Direction.EAST, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R90))
      .register(Direction.WEST, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R270));
  }
}
