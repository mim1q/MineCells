package com.github.mim1q.minecells.item.weapon.melee;

import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.valuecalculators.ModValueCalculators;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL_MATERIAL;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADDITION;
import static net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE;
import static net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_SPEED;

public class CustomMeleeWeapon extends SwordItem implements CrittingWeapon {
  private static final Set<CustomMeleeWeapon> ALL_MELEE_WEAPONS = new HashSet<>();
  private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = ImmutableMultimap.of();

  private double damage = 0.0;
  private double speed = 0.0;

  private final ValueCalculator damageCalculator;
  private final ValueCalculator extraDamageCalculator;
  private final ValueCalculator critDamageCalculator;
  private final ValueCalculator speedCalculator;

  public CustomMeleeWeapon(String valueCalculatorName, Settings settings) {
    super(CELL_INFUSED_STEEL_MATERIAL, 0, 0f, settings);
    ALL_MELEE_WEAPONS.add(this);
    this.damageCalculator = ModValueCalculators.of("melee/base_stats", valueCalculatorName + "_damage", 0.0);
    this.speedCalculator = ModValueCalculators.of("melee/base_stats", valueCalculatorName + "_speed", 0.0);
    this.extraDamageCalculator = ModValueCalculators.of("melee/extra_damage", valueCalculatorName + "_crit_damage", 0.0);
    this.critDamageCalculator = ModValueCalculators.of("melee/extra_damage", valueCalculatorName + "_extra_damage", 0.0);
  }

  @Override
  public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
    return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
  }

  @Override
  public float getAttackDamage() {
    return (float) this.damage;
  }

  @Override
  public float getExtraDamage(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
    return (float) this.extraDamageCalculator.calculate(
      ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, attacker)
        .with(ValueCalculatorParameter.TARGET, target)
        .with(ValueCalculatorParameter.HOLDER_STACK, stack)
    );
  }

  @Override
  public boolean canCrit(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
    return false;
  }

  @Override
  public float getAdditionalCritDamage(ItemStack stack, @Nullable LivingEntity target, @Nullable LivingEntity attacker) {
    return (float) this.critDamageCalculator.calculate(
      ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, attacker)
        .with(ValueCalculatorParameter.TARGET, target)
        .with(ValueCalculatorParameter.HOLDER_STACK, stack)
    );
  }

  public static void updateAttributes() {
    ALL_MELEE_WEAPONS.forEach(it -> {
      it.damage = it.damageCalculator.calculate();
      it.speed = it.speedCalculator.calculate();
      it.attributeModifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
        .put(
          GENERIC_ATTACK_DAMAGE,
          new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", it.damage, ADDITION)
        )
        .put(
          GENERIC_ATTACK_SPEED,
          new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", it.speed, ADDITION)
        )
        .build();
    });
  }
}
