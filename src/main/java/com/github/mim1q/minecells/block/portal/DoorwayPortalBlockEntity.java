package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class DoorwayPortalBlockEntity extends BlockEntity {
  public DoorwayPortalBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.DOORWAY, pos, state);
  }

  public Identifier getTexture() {
    return ((DoorwayPortalBlock)getCachedState().getBlock()).type.texture;
  }

  public Identifier getDimension() {
    return ((DoorwayPortalBlock)getCachedState().getBlock()).type.dimension;
  }

  public MutableText getLabel() {
    return Text.translatable(getDimension().toTranslationKey("dimension"));
  }

  public float getRotation() {
    return getCachedState().get(DoorwayPortalBlock.FACING).asRotation();
  }
}
