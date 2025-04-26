package com.github.mim1q.minecells.item.weapon.melee;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.valuecalculators.ModValueCalculators;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL_MATERIAL;
import static net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE;
import static net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE;
import static net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_SPEED;

public class CustomMeleeWeapon extends SwordItem implements CrittingWeapon {
  private static final Set<CustomMeleeWeapon> ALL_MELEE_WEAPONS = new HashSet<>();
  private AttributeModifiersComponent attributeModifiers = AttributeModifiersComponent.DEFAULT;

  private static final ValueCalculator GLOBAL_DAMAGE_MULTIPLIER = ModValueCalculators.of("melee/global", "global_damage_multiplier", 1.0);
  private static final ValueCalculator GLOBAL_SPEED_MULTIPLIER = ModValueCalculators.of("melee/global", "global_speed_multiplier", 1.0);

  private double damage = 0.0;
  private double speed = 0.0;

  private final ValueCalculator damageCalculator;
  private final ValueCalculator extraDamageCalculator;
  private final ValueCalculator critDamageCalculator;
  private final ValueCalculator speedCalculator;

  public CustomMeleeWeapon(String valueCalculatorName, Settings settings) {
    super(CELL_INFUSED_STEEL_MATERIAL, settings);
    ALL_MELEE_WEAPONS.add(this);
    this.damageCalculator = ModValueCalculators.of("melee/" + valueCalculatorName, "damage", 0.0);
    this.speedCalculator = ModValueCalculators.of("melee/" + valueCalculatorName, "speed", 0.0);
    this.extraDamageCalculator = ModValueCalculators.of("melee/" + valueCalculatorName, "extra_damage", 0.0);
    this.critDamageCalculator = ModValueCalculators.of("melee/" + valueCalculatorName, "crit_damage", 0.0);
  }


  @Override
  public AttributeModifiersComponent getAttributeModifiers() {
    return this.attributeModifiers;
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
      it.damage = (it.damageCalculator.calculate() - 1.0) * GLOBAL_DAMAGE_MULTIPLIER.calculate();
      it.speed = (it.speedCalculator.calculate() - 4.0) * GLOBAL_SPEED_MULTIPLIER.calculate();
      it.attributeModifiers = AttributeModifiersComponent.builder()
        .add(
          GENERIC_ATTACK_DAMAGE,
          new EntityAttributeModifier(MineCells.createId("attack_damage"), it.damage, ADD_VALUE),
          AttributeModifierSlot.HAND
        )
        .add(
          GENERIC_ATTACK_SPEED,
          new EntityAttributeModifier(MineCells.createId("attack_speed"), it.speed, ADD_VALUE),
          AttributeModifierSlot.HAND
        )
        .build();
    });
  }

  public static Set<Item> getAllMeleeWeapons() {
    return Set.copyOf(ALL_MELEE_WEAPONS);
  }
}
