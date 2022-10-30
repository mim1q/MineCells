package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TentacleItem extends AbstractCritWeaponItem {

  public static final String TOOLTIP1_KEY = "item.minecells.tentacle.tooltip1";
  public static final String TOOLTIP2_KEY = "item.minecells.tentacle.tooltip2";

  public TentacleItem(float attackDamage, float critAttackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, critAttackDamage, attackSpeed, settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (!world.isClient) {
      Entity entity = TentacleWeaponEntity.create(world, user);
      if (entity != null) {
        world.spawnEntity(entity);
      }
      stack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
      user.getItemCooldownManager().set(this, 40);
      return TypedActionResult.success(stack, true);
    }
    return super.use(world, user, hand);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    return false;
  }

  @Override
  public DamageSource getDamageSource(ItemStack stack, LivingEntity attacker, LivingEntity target) {
    return null;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    tooltip.add(Text.translatable(TOOLTIP1_KEY).formatted(Formatting.GRAY));
    tooltip.add(Text.translatable(TOOLTIP2_KEY).formatted(Formatting.DARK_GRAY));
  }
}
