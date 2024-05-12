package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.CritIndicator;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.TextUtils;
import dev.mim1q.gimm1q.screenshake.ScreenShakeUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightningBoltItem extends Item implements CritIndicator {
  private static final int MAX_USE_TIME = 60 * 60 * 20;
  private static final double SELECT_DISTANCE = 6.0;
  private static final double MAX_DISTANCE = 12.0;

  public LightningBoltItem(Settings settings) {
    super(settings);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (world.isClient || !user.isPlayer()) return;

    var entity = getTargetedEntity(stack, world);
    if (entity == null) {
      user.stopUsingItem();
      return;
    }

    var ticks = getMaxUseTime(stack) - remainingUseTicks;


    var damage = 2.0f;
    var intensity = 0;

    if (ticks > 20) {
      damage = 2.5f;
      intensity = 1;
    }

    if (ticks > 40) {
      damage = 3.0f;
      intensity = 2;
    }

    if (ticks > 60) {
      damage = 4.0f;
      intensity = 3;
    }

    if (ticks % 5 == 0) {
      ScreenShakeUtils.applyShake(
        (ServerPlayerEntity) user,
        0.33f * intensity,
        20,
        "minecells:weapon_lightning_bolt"
      );
    }


    if (ticks % 10 == 0) {
      if (ticks > 40) {
        world.playSound(
          null,
          user.getBlockPos(),
          MineCellsSounds.CRIT,
          user.getSoundCategory(),
          0.4f,
          1.0f
        );
      }

      entity.damage(
        MineCellsDamageSource.ELECTRICITY.get(world, user),
        damage
      );

      if (intensity > 0) {
        entity.addStatusEffect(
          new StatusEffectInstance(
            MineCellsStatusEffects.ELECTRIFIED,
            20 + 20 * intensity,
            intensity,
            false,
            false,
            true
          )
        );
      }

      if (ticks > 60) {
        user.damage(
          world.getDamageSources().magic(),
          1.0f
        );
      }

      world.playSound(
        null,
        user.getBlockPos(),
        MineCellsSounds.SHOCKER_RELEASE,
        user.getSoundCategory(),
        0.2f,
        1.0f
      );
    }

    if (ticks % 3 == 0) {

      var color = getLightningColor(ticks);

      var userPos = user.getPos().add(0, user.getHeight() / 2.0, 0);
      var direction = entity.getPos().add(0, entity.getHeight() / 2.0, 0).subtract(userPos);
      ((ServerWorld)world).spawnParticles(
        MineCellsParticles.ELECTRICITY.get(direction, (int) direction.length(), color, 1.0f),
        userPos.getX(),
        userPos.getY(),
        userPos.getZ(),
        1,
        0.0,
        0.0,
        0.0,
        0.0
      );
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    super.onStoppedUsing(stack, world, user, remainingUseTicks);
    if (world.isClient) return;

    setTargetedEntity(stack, null);
    ((PlayerEntity) user).getItemCooldownManager().set(this, 20);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (getTargetedEntity(user.getStackInHand(hand), world) == null) {
      return TypedActionResult.fail(user.getStackInHand(hand));
    }

    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    super.inventoryTick(stack, world, entity, slot, selected);

    if (world.isClient || !entity.isPlayer()) return;

    var player = (PlayerEntity) entity;

    if (!selected || player.getItemCooldownManager().isCoolingDown(this)) {
      setTargetedEntity(stack, null);
      return;
    }
    var startPos = entity.getPos().add(0, entity.getHeight() / 2.0, 0);
    var direction = entity.getRotationVector();
    var length = SELECT_DISTANCE;
    var endPos = startPos.add(direction.multiply(length));

    var blockRaycast = world.raycast(new RaycastContext(
      startPos,
      endPos,
      RaycastContext.ShapeType.COLLIDER,
      RaycastContext.FluidHandling.NONE,
      entity
    ));

    if (blockRaycast.getType() != Type.MISS) {
      length = blockRaycast.getPos().distanceTo(startPos);
    }

    LivingEntity selectedEntity = null;
    for (var delta = 1.0; delta <= length; delta += 0.5) {
      var pos = startPos.add(direction.multiply(delta));
      var box = Box.of(pos, 1.0, 1.0, 1.0);
      var entities = world.getEntitiesByClass(LivingEntity.class, box, it -> it != entity);
      if (!entities.isEmpty()) {
        selectedEntity = entities.get(0);
        break;
      }
    }

    if (selectedEntity == null) {
      var currentTarget = getTargetedEntity(stack, world);
      if (currentTarget == null) return;

      if (!player.isUsingItem() || currentTarget.distanceTo(player) > MAX_DISTANCE) {
        setTargetedEntity(stack, null);
      }
      return;
    }

    setTargetedEntity(stack, selectedEntity);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return MAX_USE_TIME;
  }

  private static void setTargetedEntity(ItemStack stack, @Nullable LivingEntity entity) {
    stack.getOrCreateNbt().putInt("targetId", entity == null ? -1 : entity.getId());
  }

  public static LivingEntity getTargetedEntity(ItemStack stack, World world) {
    var id = stack.getOrCreateNbt().getInt("targetId");
    return id == -1
      ? null
      : (LivingEntity) world.getEntityById(stack.getOrCreateNbt().getInt("targetId"));
  }

  @Override
  public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
    return false;
  }

  public static int getLightningColor(int ticks) {
    if (ticks > 60) return 0xff998c;
    if (ticks > 40) return 0xffc8a7;
    if (ticks > 20) return 0xf5ffbb;
    return 0xbec7ff;
  }

  @Override
  public boolean shouldShowCritIndicator(@Nullable PlayerEntity player, @Nullable LivingEntity target, ItemStack stack) {
    return player != null && player.getItemUseTime() > 40;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    TextUtils.addDescription(tooltip, this.getTranslationKey() + ".description");
  }
}
