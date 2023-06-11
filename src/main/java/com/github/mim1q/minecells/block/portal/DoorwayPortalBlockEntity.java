package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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

  public MutableText getLabel() {
    return Text.translatable(((DoorwayPortalBlock)getCachedState().getBlock()).type.dimension.translationKey);
  }

  public boolean canPlayerEnter(PlayerEntity player) {
    if (player == null || world == null) return false;
    return ((PlayerEntityAccessor)player).getMineCellsData().get(this.pos).getPortalData(
      MineCellsDimension.getFrom(world),
      ((DoorwayPortalBlock)getCachedState().getBlock()).type.dimension
    ).isPresent();
  }

  public float getRotation() {
    return getCachedState().get(DoorwayPortalBlock.FACING).asRotation();
  }
}
