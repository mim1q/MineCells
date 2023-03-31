package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.util.TextUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class NutcrackerItem extends Item implements CrittingWeapon {
  private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

  public NutcrackerItem(float attackDamage, float attackSpeed, Settings settings) {
    super(settings);
    this.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
      .put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", attackDamage, EntityAttributeModifier.Operation.ADDITION))
      .put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", attackSpeed, EntityAttributeModifier.Operation.ADDITION))
      .build();
  }

  public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
    return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    stack.damage(1, attacker, (user) -> user.sendToolBreakStatus(user.getActiveHand()));
    return super.postHit(stack, target, attacker);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    return Stream.of(MineCellsEffectFlags.FROZEN, MineCellsEffectFlags.STUNNED).anyMatch(
      flag -> ((LivingEntityAccessor) target).getMineCellsFlag(flag)
    );
  }

  @Override
  public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 6.0F;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, "item.minecells.nutcracker.description", getAdditionalCritDamage(stack, null, null));
  }
}
