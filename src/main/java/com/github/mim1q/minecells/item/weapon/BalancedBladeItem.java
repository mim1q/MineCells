package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class BalancedBladeItem extends SwordItem implements ICritWeapon {
  private static final ImmutableList<Multimap<EntityAttribute, EntityAttributeModifier>> modifiers;
  static {
    var listBuilder = ImmutableList.<Multimap<EntityAttribute, EntityAttributeModifier>>builder();
    listBuilder.addAll(IntStream.range(0, 10).<Multimap<EntityAttribute, EntityAttributeModifier>>mapToObj(
      num -> ImmutableMultimap.of(
        EntityAttributes.GENERIC_ATTACK_DAMAGE,
        new EntityAttributeModifier("Tool modifier", num * 0.75F + 2.0F, EntityAttributeModifier.Operation.ADDITION)
      )
    ).iterator());
    modifiers = listBuilder.build();
  }

  public BalancedBladeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
    super(toolMaterial, attackDamage, attackSpeed, settings);
  }

  @Override
  public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
    Entity holder = stack.getHolder();
    if (holder instanceof PlayerEntityAccessor player) {
      return modifiers.get(player.getBalancedBladeStacks());
    }
    return super.getAttributeModifiers(stack, slot);
  }

  @Override
  public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof PlayerEntityAccessor player) {
      player.addBalancedBladeStack();
    }
    return super.postHit(stack, target, attacker);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (attacker instanceof PlayerEntityAccessor player) {
      return player.getBalancedBladeStacks() >= 9;
    }
    return false;
  }

  @Override
  public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return 1.0F;
  }
}
