package com.github.mim1q.minecells.item.weapon;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HattorisKatanaItem extends SwordItem {
  private static final String TOOLTIP_KEY = "item.minecells.hattoris_katana.tooltip";
  public HattorisKatanaItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    if (user.isPlayer()) {
      PlayerEntity player = (PlayerEntity) user;
      player.getItemCooldownManager().set(this, 20 * 30);
    }
    return super.finishUsing(stack, world, user);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 60;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP_KEY).formatted(Formatting.GRAY));
  }
}
