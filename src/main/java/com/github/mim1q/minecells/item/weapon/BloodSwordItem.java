package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.effect.BleedingStatusEffect;
import com.github.mim1q.minecells.util.TextUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodSwordItem extends SwordItem {

  public static final float COOLDOWN = 2.5F;

  private static final String TOOLTIP1_KEY = "item.minecells.blood_sword.tooltip1";
  private static final String TOOLTIP2_KEY = "item.minecells.blood_sword.tooltip2";
  private static final String EFFECT_KEY = "effect.minecells.bleeding";

  public BloodSwordItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof ServerPlayerEntity player) {
      if (!player.getItemCooldownManager().isCoolingDown(this)) {
        player.getItemCooldownManager().set(this, (int)(20  * COOLDOWN));
        BleedingStatusEffect.apply(target, 20 * 6);
      }
    }
    return super.postHit(stack, target, attacker);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.blood_sword.description");
  }
}
