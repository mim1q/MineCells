package com.github.mim1q.minecells.datagen.util;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ColoredTorchBlock;
import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.block.SkeletonDecorationBlock;
import com.github.mim1q.minecells.datagen.util.specific.DatagenWoodModelUtils;
import com.github.mim1q.minecells.registry.featureset.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.Properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.mim1q.minecells.datagen.util.DatagenModelUtils.createHorizontalRotateableCoordinates;
import static com.github.mim1q.minecells.datagen.util.DatagenModelUtils.createRotateableCoordinates;
import static net.minecraft.data.client.VariantSettings.MODEL;
import static net.minecraft.data.client.VariantSettings.Y;

public interface DatagenBlockSetUtils extends DatagenWoodModelUtils, DatagenTagUtils, DatagenLootTableUtils {
  default void addSimpleSet(SimpleSet set) {
    addBlock(set.block);
    addSlab(set.slab, set.block);
    addStairs(set.stairs, set.block);
  }

  default void addWoodSet(WoodSet set) {
    addBlock(set.planks);

    addPillar(set.log);
    addPillar(set.strippedLog);
    addBlock(set.wood, set.log, "_side");
    addBlock(set.strippedWood, set.strippedLog, "_side");

    addStairs(set.stairs, set.planks);
    addSlab(set.slab, set.planks);
    addFence(set.fence, set.planks);
    addFenceGate(set.fenceGate, set.planks);
    addButton(set.button, set.planks);
    addPressurePlate(set.pressurePlate, set.planks);
    addTrapdoor(set.trapdoor, set.planks);
    addDoor(set.door, set.planks);
    addSign(set.sign, set.wallSign, set.signItem, set.planks);

    //#region Drops
    addSimpleDrop(set.getBlocks().stream().filter(it -> !(it instanceof DoorBlock)).toArray(Block[]::new));
    getInitializers().blockLootTable().add(it -> it.addDrop(set.door, it.doorDrops(set.door)));
    //#endregion

    //#region Tags
    addBlockTag(BlockTags.PLANKS, set.planks);
    addBlockTag(BlockTags.LOGS_THAT_BURN, set.log, set.strippedLog, set.wood, set.strippedWood);
    addBlockTag(BlockTags.WOODEN_SLABS, set.slab);
    addBlockTag(BlockTags.WOODEN_STAIRS, set.stairs);
    addBlockTag(BlockTags.WOODEN_FENCES, set.fence);
    addBlockTag(BlockTags.FENCE_GATES, set.fenceGate);
    addBlockTag(BlockTags.WOODEN_BUTTONS, set.button);
    addBlockTag(BlockTags.WOODEN_PRESSURE_PLATES, set.pressurePlate);
    addBlockTag(BlockTags.WOODEN_TRAPDOORS, set.trapdoor);
    addBlockTag(BlockTags.WOODEN_DOORS, set.door);
    addBlockTag(BlockTags.WALL_SIGNS, set.wallSign);
    addBlockTag(BlockTags.SIGNS, set.sign);

    addBlockTag(BlockTags.AXE_MINEABLE, set.getBlocks());

    addItemTag(ItemTags.WOODEN_SLABS, set.slab);
    addItemTag(ItemTags.WOODEN_STAIRS, set.stairs);
    addItemTag(ItemTags.WOODEN_FENCES, set.fence);
    addItemTag(ItemTags.FENCE_GATES, set.fenceGate);
    addItemTag(ItemTags.WOODEN_BUTTONS, set.button);
    addItemTag(ItemTags.WOODEN_PRESSURE_PLATES, set.pressurePlate);
    addItemTag(ItemTags.WOODEN_TRAPDOORS, set.trapdoor);
    addItemTag(ItemTags.WOODEN_DOORS, set.door);
    addItemTag(ItemTags.SIGNS, tag -> tag.add(set.signItem));
    //#endregion
  }

  default void addStoneSet(StoneSet set) {
    if (set.getClass() != StoneSet.class)
      throw new IllegalArgumentException("Subclasses of StoneSet must use specific methods");

    _addStoneSet(set);
  }

  private void _addStoneSet(StoneSet set) {
    addBlock(set.block);
    addStairs(set.stairs, set.block);
    addSlab(set.slab, set.block);
    addWall(set.wall, set.block);

    //#region Tags
    addBlockTag(BlockTags.PICKAXE_MINEABLE, set.getBlocks());
    //#endregion
  }

  default void addFullStoneSet(FullStoneSet set) {
    _addStoneSet(set);

    addButton(set.button, set.block);
    addPressurePlate(set.pressurePlate, set.block);

    //#region Tags
    addBlockTag(BlockTags.BUTTONS, set.button);
    addBlockTag(BlockTags.PRESSURE_PLATES, set.pressurePlate);

    addBlockTag(BlockTags.PICKAXE_MINEABLE, set.button, set.pressurePlate);

    addItemTag(ItemTags.BUTTONS, set.button);
    addItemTag(ItemTags.STONE_CRAFTING_MATERIALS, set.block);
    addItemTag(ItemTags.STONE_TOOL_MATERIALS, set.block);
    //#endregion
  }

  default void addLeavesSet(LeavesSet set, SaplingBlock sapling) {
    addLeaves(set.leaves, sapling);

    getInitializers().blockState().add(it -> {
      var zeroTextureKey = TextureKey.of("0");
      var baseTextureKey = TextureKey.of("base");
      var detailTextureKey = TextureKey.of("detail");

      var wallModel = new Model(Optional.of(MineCells.createId("block/wall_leaves")), Optional.empty(), baseTextureKey, detailTextureKey)
        .upload(set.wallLeaves, TextureMap.of(baseTextureKey, getBlockId(set.wallLeaves)).put(detailTextureKey, getBlockId(set.wallLeaves).withSuffixedPath("_detail")), it.modelCollector);

      var hangingModel = new Model(Optional.of(MineCells.createId("block/hanging_leaves")), Optional.empty(), zeroTextureKey)
        .upload(set.hangingLeaves, TextureMap.of(zeroTextureKey, getBlockId(set.hangingLeaves)), it.modelCollector);

      it.blockStateCollector.accept(
        VariantsBlockStateSupplier.create(set.wallLeaves)
          .coordinate(createRotateableCoordinates(wallModel))
      );

      it.blockStateCollector.accept(
        VariantsBlockStateSupplier.create(set.hangingLeaves)
          .coordinate(createHorizontalRotateableCoordinates(hangingModel))
      );

      it.registerParentedItemModel(set.wallLeaves.asItem(), wallModel);
      it.registerParentedItemModel(set.hangingLeaves.asItem(), hangingModel);
      it.registerTintableCross(sapling, BlockStateModelGenerator.TintType.NOT_TINTED, TextureMap.of(TextureKey.CROSS, getBlockId(sapling)));
    });
  }

  default void addColoredTorch(ColoredTorchBlock torch, String flameName) {
    getInitializers().blockState().add(it -> {
      var flameTextureKey = TextureKey.of("flame");

      var flameTexture = MineCells.createId("block/colored_torch/" + flameName);
      var baseModel = MineCells.createId("block/template/colored_torch");
      var baseModelStanding = MineCells.createId("block/template/colored_torch_standing");

      var texture = new TextureMap()
        .put(flameTextureKey, flameTexture);

      var model = new Model(Optional.of(baseModel), Optional.empty(), flameTextureKey)
        .upload(torch, texture, it.modelCollector);
      var modelStanding = new Model(Optional.of(baseModelStanding), Optional.empty(), flameTextureKey)
        .upload(torch, "_standing", texture, it.modelCollector);

      it.blockStateCollector.accept(VariantsBlockStateSupplier.create(torch)
        .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, ColoredTorchBlock.STANDING)
          .registerVariants((dir, standing) -> {
            var variant = BlockStateVariant.create()
              .put(MODEL, standing ? modelStanding : model);
            if (!standing) {
              variant.put(Y, DatagenModelUtils.rotationFromDir(dir));
            }
            return List.of(variant);
          })
        ));

      it.registerParentedItemModel(torch, modelStanding);
    });

    addSimpleDrop(torch);
  }

  default void addFlag(FlagBlock flag) {
    addParticleOnly(flag, Blocks.OAK_PLANKS);
    addSimpleDrop(flag);

    getInitializers().blockState().add(it -> {
      var model = new Model(Optional.of(BUILTIN_ENTITY_MODEL), Optional.of("inventory"));
      model.upload(getItemId(flag), new TextureMap(), it.modelCollector, (x, textures) -> {
        var json = model.createJson(getItemId(flag), Map.of());
        json.addProperty("gui_light", "front");
        return json;
      });
    });

    addBlockTag(BlockTags.AXE_MINEABLE, flag);
  }

  default void addCorpse(SkeletonDecorationBlock sitting, SkeletonDecorationBlock hanging, Item drop, Item dropRare) {
    getInitializers().blockState().add(it -> {
      it.registerNorthDefaultHorizontalRotation(sitting);
      it.registerNorthDefaultHorizontalRotation(hanging);
    });

    getInitializers().blockLootTable().add(it -> {
      var dropBuilder = it.dropsWithSilkTouch(sitting)
        .pool(LootPool.builder().conditionally(it.createSilkTouchCondition().invert())
          .rolls(UniformLootNumberProvider.create(1F, 3F)).with(ItemEntry.builder(drop))
          .rolls(ConstantLootNumberProvider.create(1F)).with(ItemEntry.builder(dropRare)).conditionally(RandomChanceLootCondition.builder(0.2f))
        );

      it.addDrop(sitting, dropBuilder);
      it.addDrop(hanging, dropBuilder);
    });
  }
}
