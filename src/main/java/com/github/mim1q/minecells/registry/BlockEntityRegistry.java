package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntityRegistry {

  public static final Block KINGDOM_PORTAL_CORE = BlockRegistry.registerBlock(
    new KingdomPortalCoreBlock(FabricBlockSettings.copyOf(Blocks.STONE)),
    "kingdom_portal_core"
  );

  public static final BlockEntityType<KingdomPortalCoreBlockEntity> KINGDOM_PORTAL_CORE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("kingdom_portal_core"),
    FabricBlockEntityTypeBuilder
      .create(KingdomPortalCoreBlockEntity::new)
      .addBlock(KINGDOM_PORTAL_CORE)
      .build()
  );

  public static final Block KINGDOM_PORTAL_FILLER = BlockRegistry.registerBlock(
    new KingdomPortalCoreBlock.Filler(FabricBlockSettings.copyOf(Blocks.STONE)),
    "kingdom_portal_filler"
  );

  public static void register() {}
}
