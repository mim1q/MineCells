package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TentacleItem extends AbstractCritWeaponItem implements WeaponWithAbility {
  public TentacleItem(float attackDamage, float critAttackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, critAttackDamage, attackSpeed, settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    world.playSound(user.getX(), user.getY(), user.getZ(), MineCellsSounds.TENTACLE_CHARGE, user.getSoundCategory(), 1.0F, 1.0F, false);
    if (!world.isClient) {
      Entity entity = TentacleWeaponEntity.create(world, user);
      if (entity != null) {
        world.spawnEntity(entity);
      }
      stack.damage(1, user, (p) -> p.sendToolBreakStatus(hand));
      user.getItemCooldownManager().set(this, getAbilityCooldown(stack));
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
    super.appendTooltip(stack, world, tooltip, context);
    fillTooltip(tooltip, false, "item.minecells.tentacle.description", stack);
  }

  @Override
  public float getBaseAbilityDamage(ItemStack stack) {
    return 0.0F;
  }

  @Override
  public int getBaseAbilityCooldown(ItemStack stack) {
    return 40;
  }
}
