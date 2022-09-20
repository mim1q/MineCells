package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.block.blockentity.SetupBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockEntities {
  public static final BlockEntityType<KingdomPortalCoreBlockEntity> KINGDOM_PORTAL_CORE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("kingdom_portal_core"),
    FabricBlockEntityTypeBuilder
      .create(KingdomPortalCoreBlockEntity::new)
      .addBlock(MineCellsBlocks.KINGDOM_PORTAL_CORE)
      .build()
  );

  public static final BlockEntityType<SetupBlockEntity> SETUP_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("setup_block_entity"),
    FabricBlockEntityTypeBuilder
      .create(SetupBlockEntity::new)
      .addBlocks(
        MineCellsBlocks.ELEVATOR_ASSEMBLER,
        MineCellsBlocks.PRISON_BOX,
        MineCellsBlocks.CONJUNCTIVIUS_BOX,
        MineCellsBlocks.SHOCKER_BOX)
      .build()
  );

  public static void init() {}
}
