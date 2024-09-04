package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.valuecalculators.ModValueCalculators;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FrostBlastItem extends Item implements WeaponWithAbility {
  private static final ValueCalculator ABILITY_DAMAGE_CALCULATOR = ModValueCalculators.of("spells", "frost_blast_damage", 0.0);
  private static final ValueCalculator ABILITY_COOLDOWN_CALCULATOR = ModValueCalculators.of("spells", "frost_blast_cooldown", 0.0);

  public FrostBlastItem(Settings settings) {
    super(settings);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    stack.damage(1, user, e -> e.sendToolBreakStatus(user.getActiveHand()));
    user.playSound(MineCellsSounds.FROST_BLAST, 1.0F, 1.1F);
    if (user.getWorld().isClient()) {
      for (int i = 0; i < 20; i++) {
        Vec3d pos = user.getPos().add(0.0D, 1.25D, 0.0D);
        Vec3d vel = Vec3d.fromPolar(
          user.getPitch() + (user.getRandom().nextFloat() - 0.5F) * 45.0F,
          user.getYaw() + (user.getRandom().nextFloat() - 0.5F) * 45.0F
        );
        ParticleUtils.addParticle(
          (ClientWorld) world,
          ParticleTypes.SNOWFLAKE,
          pos,
          vel.multiply(0.25D + user.getRandom().nextDouble() * 0.25D).add(0.0D, 0.1D, 0.0D)
        );
      }
      return stack;
    }
    if (user.isPlayer()) {
      ((PlayerEntity) user).getItemCooldownManager().set(this, getAbilityCooldown(stack, user));
    }
    Set<LivingEntity> entities = new HashSet<>();
    for (int i = 1; i <= 3; ++i) {
      Vec3d searchPos = user.getPos().add(user.getRotationVector().multiply(i * 1.5F));
      Box searchBox = Box.of(searchPos, 1.0D + 0.75D * i, 1.5D, 1.0D + 0.75D * i);
      entities.addAll(world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != user));
    }
    for (LivingEntity entity : entities) {
      applyFreeze(entity);
      entity.damage(world.getDamageSources().freeze(), getAbilityDamage(stack, user, entity));
    }
    return stack;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 20;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  public static void applyFreeze(LivingEntity entity) {
    int duration = 20 * 5;
    World world = entity.getWorld();
    if (world.getBlockState(entity.getBlockPos()).getFluidState().isIn(FluidTags.WATER)) {
      world.setBlockState(entity.getBlockPos(), Blocks.ICE.getDefaultState());
      duration = 20 * 10;
    }
    entity.playSound(MineCellsSounds.FREEZE, 1.0F, 1.0F);
    entity.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.FROZEN, duration, 0, false, false, true));
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    fillTooltip(tooltip, true, "item.minecells.frost_blast.description", stack);
  }

  @Override
  public ValueCalculator getAbilityDamageCalculator() {
    return ABILITY_DAMAGE_CALCULATOR;
  }

  @Override
  public ValueCalculator getAbilityCooldownCalculator() {
    return ABILITY_COOLDOWN_CALCULATOR;
  }
}
