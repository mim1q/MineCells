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

public interface DatagenModelUtils extends DatagenUtils {
  default Identifier getId(Block block) {
    return Registries.BLOCK.getId(block).withPrefixedPath("block/");
  }

  default void addBlock(Block block) {
    getInitializers().blockState().add(it -> {
      it.registerSimpleCubeAll(block);

      it.registerParentedItemModel(block, getId(block));
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

  default void addStairs(StairsBlock block, Block baseBlock) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(baseBlock);
      var model = Models.STAIRS.upload(block, texture, it.modelCollector);
      var innerModel = Models.INNER_STAIRS.upload(block, texture, it.modelCollector);
      var outerModel = Models.OUTER_STAIRS.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createStairsBlockState(block, innerModel, model, outerModel));
      it.registerParentedItemModel(block, model);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createStairsRecipe(block, Ingredient.ofItems(baseBlock)).offerTo(it);
    });
  }

  default void addSlab(SlabBlock block, Block baseBlock) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(baseBlock);

      var modelBottom = Models.SLAB.upload(block, texture, it.modelCollector);
      var modelTop = Models.STAIRS.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createSlabBlockState(block, modelBottom, modelTop, getId(baseBlock)));
      it.registerParentedItemModel(block, modelBottom);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, block, Ingredient.ofItems(baseBlock)).offerTo(it);
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
      FabricRecipeProvider.createPressurePlateRecipe(RecipeCategory.BUILDING_BLOCKS, block, Ingredient.ofItems(base)).offerTo(it);
    });
  }

  default void addDoor(DoorBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var modelLeft = Models.DOOR_BOTTOM_LEFT.upload(block, texture, it.modelCollector);
      var modelRight = Models.DOOR_BOTTOM_RIGHT.upload(block, texture, it.modelCollector);
      var modelLeftTop = Models.DOOR_TOP_LEFT.upload(block, texture, it.modelCollector);
      var modelRightTop = Models.DOOR_TOP_RIGHT.upload(block, texture, it.modelCollector);
      var modelLeftOpen = Models.DOOR_BOTTOM_LEFT_OPEN.upload(block, texture, it.modelCollector);
      var modelRightOpen = Models.DOOR_BOTTOM_RIGHT_OPEN.upload(block, texture, it.modelCollector);
      var modelLeftTopOpen = Models.DOOR_TOP_LEFT_OPEN.upload(block, texture, it.modelCollector);
      var modelRightTopOpen = Models.DOOR_TOP_RIGHT_OPEN.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createDoorBlockState(
        block,
        modelLeft,
        modelRight,
        modelLeftTop,
        modelRightTop,
        modelLeftOpen,
        modelRightOpen,
        modelLeftTopOpen,
        modelRightTopOpen
      ));

    });

    addGeneratedItem(block);

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createDoorRecipe(block, Ingredient.ofItems(base)).offerTo(it);
    });
  }

  default void addTrapdoor(TrapdoorBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);

      var modelBottom = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM.upload(block, texture, it.modelCollector);
      var modelTop = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_TOP.upload(block, texture, it.modelCollector);
      var modelOpen = Models.TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createTrapdoorBlockState(block, modelBottom, modelTop, modelOpen));
      it.registerParentedItemModel(block, modelBottom);
    });

    getInitializers().recipe().add(it -> {
      FabricRecipeProvider.createTrapdoorRecipe(block, Ingredient.ofItems(base)).offerTo(it);
    });
  }

  default void addWall(WallBlock block, Block base) {
    getInitializers().blockState().add(it -> {
      var texture = TextureMap.all(base);
      var modelPost = Models.TEMPLATE_WALL_POST.upload(block, texture, it.modelCollector);
      var modelSide = Models.TEMPLATE_WALL_SIDE.upload(block, texture, it.modelCollector);
      var modelSideTall = Models.TEMPLATE_WALL_SIDE_TALL.upload(block, texture, it.modelCollector);

      it.blockStateCollector.accept(createWallBlockState(block, modelPost, modelSide, modelSideTall));

      it.registerParentedItemModel(block, modelPost);
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
