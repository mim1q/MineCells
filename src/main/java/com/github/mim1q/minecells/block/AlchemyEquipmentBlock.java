package com.github.mim1q.minecells.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class AlchemyEquipmentBlock extends Block {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final EnumProperty<AlchemyEquipmentType> TYPE = EnumProperty.of("variant", AlchemyEquipmentType.class);

  public AlchemyEquipmentBlock(Settings settings) {
    super(settings);
  }

  @Override
  public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
    return true;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(FACING, TYPE);
  }

  public enum AlchemyEquipmentType implements StringIdentifiable {
    VARIANT_0("0"),
    VARIANT_1("1"),
    VARIANT_2("2");

    private final String name;

    AlchemyEquipmentType(String name) {
      this.name = name;
    }

    @Override
    public String asString() {
      return name;
    }
  }
}
