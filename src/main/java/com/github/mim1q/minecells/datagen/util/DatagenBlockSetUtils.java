package com.github.mim1q.minecells.datagen.util;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.ColoredTorchBlock;
import com.github.mim1q.minecells.datagen.util.specific.DatagenWoodModelUtils;
import com.github.mim1q.minecells.registry.featureset.FullStoneSet;
import com.github.mim1q.minecells.registry.featureset.LeavesSet;
import com.github.mim1q.minecells.registry.featureset.StoneSet;
import com.github.mim1q.minecells.registry.featureset.WoodSet;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.data.client.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

import static net.minecraft.data.client.VariantSettings.MODEL;

public interface DatagenBlockSetUtils extends DatagenWoodModelUtils, DatagenTagUtils, DatagenLootTableUtils {
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
        .upload(torch, texture, it.modelCollector);

      it.blockStateCollector.accept(VariantsBlockStateSupplier.create(torch)
        .coordinate(BlockStateVariantMap.create(ColoredTorchBlock.STANDING)
          .register(true, BlockStateVariant.create().put(MODEL, modelStanding)))
        .coordinate(DatagenModelUtils.createHorizontalRotateableCoordinates(model))
      );
    });

    addSimpleDrop(torch);
  }
}
