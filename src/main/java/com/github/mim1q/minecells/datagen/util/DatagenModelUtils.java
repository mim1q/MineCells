package com.github.mim1q.minecells.datagen.util;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.item.weapon.bow.CustomCrossbowItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.*;
import net.minecraft.data.client.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.data.client.BlockStateModelGenerator.*;
import static net.minecraft.data.client.VariantSettings.MODEL;
import static net.minecraft.data.client.VariantSettings.Y;
import static net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem;
import static net.minecraft.data.server.recipe.RecipeProvider.hasItem;

public interface DatagenModelUtils extends DatagenUtils {
  Identifier BUILTIN_ENTITY_MODEL = Identifier.of("builtin/entity");

  default Identifier getBlockId(Block block) {
    return Registries.BLOCK.getId(block).withPrefixedPath("block/");
  }

  default Identifier getItemId(ItemConvertible item) {
    return Registries.ITEM.getId(item.asItem()).withPrefixedPath("item/");
  }

  default Identifier getItemId(ItemConvertible item, String prefix) {
    return Registries.ITEM.getId(item.asItem()).withPrefixedPath("item/" + prefix);
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

  default void addHandheldItem(ItemConvertible item) {
    getInitializers().itemModel().add(it -> {
      it.register(item.asItem(), Models.HANDHELD);
    });
  }

  default void addGeneratedItem(ItemConvertible item, Identifier texture) {
    getInitializers().itemModel().add(it -> {
      Models.GENERATED.upload(ModelIds.getItemModelId(item.asItem()), TextureMap.layer0(texture), it.writer);
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

  default void addBow(CustomBowItem bow) {
    getInitializers().itemModel().add(it -> {
      var model = new Model(Optional.of(MineCells.createId("item/base_bow")), Optional.of("inventory"), TextureKey.LAYER0);

      var modelPulling0 = model.upload(
        getItemId(bow).withSuffixedPath("_pulling_0"),
        TextureMap.layer0(getItemId(bow, "bow/").withSuffixedPath("_pulling_0")),
        it.writer
      );

      var modelPulling1 = model.upload(
        getItemId(bow).withSuffixedPath("_pulling_1"),
        TextureMap.layer0(getItemId(bow, "bow/").withSuffixedPath("_pulling_1")),
        it.writer
      );

      var modelPulling2 = model.upload(
        getItemId(bow).withSuffixedPath("_pulling_2"),
        TextureMap.layer0(getItemId(bow, "bow/").withSuffixedPath("_pulling_2")),
        it.writer
      );

      var layer0Texture = getItemId(bow, "bow/");
      model.upload(
        getItemId(bow),
        TextureMap.layer0(layer0Texture),
        it.writer,
        (id, textures) -> {
          var json = model.createJson(id, Map.of(TextureKey.LAYER0, layer0Texture));
          addPredicates(
            json,
            new Pair<>(Map.of("minecells:pulling", 1f), modelPulling0),
            new Pair<>(Map.of("minecells:pulling", 1f, "minecells:pull", 0.5f), modelPulling1),
            new Pair<>(Map.of("minecells:pulling", 1f, "minecells:pull", 1f), modelPulling2)
          );
          return json;
        }
      );
    });
  }

  default void addCrossbow(CustomCrossbowItem crossbow) {
    getInitializers().itemModel().add(it -> {
      var model = new Model(Optional.of(MineCells.createId("item/base_crossbow")), Optional.of("inventory"), TextureKey.LAYER0);
      var modelPulling0 = model.upload(
        getItemId(crossbow).withSuffixedPath("_pulling_0"),
        TextureMap.layer0(getItemId(crossbow, "bow/").withSuffixedPath("_pulling_0")),
        it.writer
      );

      var modelPulling1 = model.upload(
        getItemId(crossbow).withSuffixedPath("_pulling_1"),
        TextureMap.layer0(getItemId(crossbow, "bow/").withSuffixedPath("_pulling_1")),
        it.writer
      );

      var modelPulling2 = model.upload(
        getItemId(crossbow).withSuffixedPath("_pulling_2"),
        TextureMap.layer0(getItemId(crossbow, "bow/").withSuffixedPath("_pulling_2")),
        it.writer
      );

      var modelCharged = model.upload(
        getItemId(crossbow).withSuffixedPath("_charged"),
        TextureMap.layer0(getItemId(crossbow, "bow/").withSuffixedPath("_pulling_2")),
        it.writer
      );

      model.upload(
        getItemId(crossbow),
        TextureMap.layer0(getItemId(crossbow, "bow/")),
        it.writer,
        (id, textures) -> {
          var json = model.createJson(id, Map.of(TextureKey.LAYER0, getItemId(crossbow, "bow/")));
          addPredicates(
            json,
            new Pair<>(Map.of("minecells:pulling", 1f), modelPulling0),
            new Pair<>(Map.of("minecells:pulling", 1f, "minecells:pull", 0.5f), modelPulling1),
            new Pair<>(Map.of("minecells:pulling", 1f, "minecells:pull", 1f), modelPulling2),
            new Pair<>(Map.of("minecells:charged", 1f), modelCharged)
          );
          return json;
        }
      );
    });
  }

  default void addGrass(Block grass, Block base, Identifier overlay) {
    getInitializers().blockState().add(it -> {
      var overlayKey = TextureKey.of("overlay");
      var model = new Model(
        Optional.of(Identifier.of("block/grass_block")),
        Optional.empty(),
        TextureKey.PARTICLE,
        TextureKey.BOTTOM,
        TextureKey.SIDE,
        overlayKey
      );

      it.registerSingleton(
        grass,
        TextureMap
          .of(TextureKey.PARTICLE, getBlockId(base))
          .put(TextureKey.BOTTOM, getBlockId(base))
          .put(TextureKey.SIDE, getBlockId(grass))
          .put(overlayKey, overlay),
        model
      );

      it.registerParentedItemModel(grass, getBlockId(grass));
    });

    getInitializers().blockLootTable().add(it -> {
      it.addDrop(grass, it.drops(grass, base));
    });
  }

  @SafeVarargs
  static void addPredicates(JsonObject json, Pair<Map<String, Float>, Identifier>... predicates) {
    var overrides = new JsonArray();
    for (var predicate : predicates) {
      var map = predicate.getLeft();
      var model = predicate.getRight();
      var overrideJson = new JsonObject();
      var predicateJson = new JsonObject();
      map.forEach(predicateJson::addProperty);
      overrideJson.addProperty("model", model.toString());
      overrideJson.add("predicate", predicateJson);
      overrides.add(overrideJson);
    }

    json.add("overrides", overrides);
  }

  static BlockStateVariantMap createHorizontalRotateableCoordinates(Identifier model) {
    return BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
      .register(Direction.NORTH, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R0))
      .register(Direction.SOUTH, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R180))
      .register(Direction.EAST, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R90))
      .register(Direction.WEST, BlockStateVariant.create()
        .put(MODEL, model).put(Y, VariantSettings.Rotation.R270));
  }

  static VariantSettings.Rotation rotationFromDir(Direction direction) {
    return switch (direction) {
      case EAST -> VariantSettings.Rotation.R90;
      case SOUTH -> VariantSettings.Rotation.R180;
      case WEST -> VariantSettings.Rotation.R270;
      default -> VariantSettings.Rotation.R0;
    };
  }

  static BlockStateVariantMap createRotateableCoordinates(Identifier model) {
    return BlockStateVariantMap.create(Properties.FACING)
      .register(Direction.DOWN, BlockStateVariant.create()
        .put(MODEL, model)
        .put(VariantSettings.X, VariantSettings.Rotation.R90))
      .register(Direction.UP, BlockStateVariant.create()
        .put(MODEL, model)
        .put(VariantSettings.X, VariantSettings.Rotation.R270))
      .register(Direction.NORTH, BlockStateVariant.create()
        .put(MODEL, model))
      .register(Direction.SOUTH, BlockStateVariant.create()
        .put(MODEL, model)
        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
      .register(Direction.WEST, BlockStateVariant.create()
        .put(MODEL, model)
        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
      .register(Direction.EAST, BlockStateVariant.create()
        .put(MODEL, model)
        .put(VariantSettings.Y, VariantSettings.Rotation.R90));
  }
}
