package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import com.github.mim1q.minecells.network.c2s.UseTentacleWeaponC2SPacket;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.valuecalculators.ModValueCalculators;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Objects;

public class TentacleItem extends CustomMeleeWeapon implements WeaponWithAbility {
  public HitResult hitResult = null;

  private static final ValueCalculator ABILITY_DAMAGE_CALCULATOR = ModValueCalculators.of("melee/ability_damage", "conjunctivius_tentacle", 0.0);
  private static final ValueCalculator ABILITY_COOLDOWN_CALCULATOR = ModValueCalculators.of("melee/ability_cooldown", "conjunctivius_tentacle", 0.0);

  public TentacleItem(Settings settings) {
    super("conjunctivius_tentacle", settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient && hand == Hand.MAIN_HAND) {
      if (this.hitResult != null && this.hitResult.getType() != HitResult.Type.MISS) {
        var pos = this.hitResult.getPos();

        if (this.hitResult.getType() == HitResult.Type.ENTITY) {
          var targetEntity = ((EntityHitResult) this.hitResult).getEntity();
          pos = targetEntity.getPos().add(0.0D, targetEntity.getHeight() / 2.0D, 0.0D);
        }

        if (this.hitResult.getType() == HitResult.Type.BLOCK) {
          pos = ((BlockHitResult) hitResult).getBlockPos().toCenterPos();
        }

        world.playSound(user.getX(), user.getY(), user.getZ(), MineCellsSounds.TENTACLE_CHARGE, user.getSoundCategory(), 1.0F, 1.0F, false);
        var packet = new UseTentacleWeaponC2SPacket(pos);
        packet.send();

        return TypedActionResult.success(user.getStackInHand(hand));
      }
    }
    return super.use(world, user, hand);
  }

  @Override
  public boolean canCrit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    return attacker.getPassengerList().stream().anyMatch(it -> it.getType() == MineCellsEntities.TENTACLE_WEAPON);
  }

  @Override
  public ValueCalculator getAbilityDamageCalculator() {
    return ABILITY_DAMAGE_CALCULATOR;
  }

  @Override
  public ValueCalculator getAbilityCooldownCalculator() {
    return ABILITY_COOLDOWN_CALCULATOR;
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (entity instanceof PlayerEntity player) {
      if (!player.isMainPlayer() || player.getItemCooldownManager().isCoolingDown(this)) {
        return;
      }
    } else {
      return;
    }

    var maxDistance = MineCells.COMMON_CONFIG.baseTentacleMaxDistance;
    var minDistance = 3;
    var targetPos = entity.getEyePos().add(entity.getRotationVec(0.5F).multiply(maxDistance));
    var entityRaycast = ProjectileUtil.raycast(
      entity,
      entity.getEyePos(),
      targetPos,
      Box.of(entity.getEyePos(), maxDistance * 2, maxDistance * 2, maxDistance * 2),
      Objects::nonNull,
      maxDistance * maxDistance
    );
    if (
      entityRaycast != null
        && entityRaycast.getType() != HitResult.Type.MISS
        && entityRaycast.getPos().squaredDistanceTo(entity.getEyePos()) >= minDistance * minDistance
    ) {
      this.hitResult = entityRaycast;
      return;
    }
    this.hitResult = world.raycast(new RaycastContext(
      entity.getEyePos(),
      targetPos,
      RaycastContext.ShapeType.COLLIDER,
      RaycastContext.FluidHandling.NONE,
      entity
    ));

    if (
      this.hitResult != null
        && BlockPos.ofFloored(this.hitResult.getPos()).getSquaredDistance(entity.getPos()) <= minDistance * minDistance
    ) {
      this.hitResult = null;
    }
  }
}
