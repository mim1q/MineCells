package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.BiomeBannerBlock;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BiomeBannerItem extends BlockItem {

  private static final String PATTERN_LANG_PREFIX = "item.minecells.biome_banner.";

  public BiomeBannerItem(Settings settings) {
    super(MineCellsBlocks.BIOME_BANNER, settings);
  }

  @Nullable
  @Override
  protected BlockState getPlacementState(ItemPlacementContext context) {
    BlockState state = super.getPlacementState(context);
    if (state == null) {
      return null;
    }
    var nbt = context.getStack().getNbt();

    var pattern = nbt == null
      ? BiomeBannerBlock.BannerPattern.KING_CREST
      : BiomeBannerBlock.BannerPattern.fromString(nbt.getString("pattern"));

    return state.with(BiomeBannerBlock.PATTERN, pattern);
  }

  public ItemStack getOf(BiomeBannerBlock.BannerPattern pattern) {
    ItemStack stack = this.getDefaultStack();
    stack.getOrCreateNbt().putString("pattern", pattern.asString());
    return stack;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    var nbt = stack.getNbt();
    var key = nbt == null
      ? "king_crest"
      : nbt.getString("pattern");
    tooltip.add(Text.translatable(PATTERN_LANG_PREFIX + key).formatted(Formatting.GRAY));
  }
}
