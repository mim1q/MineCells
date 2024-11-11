package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.MineCellsClientConfig;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowShooter;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.LightningBoltItem;
import com.github.mim1q.minecells.item.weapon.interfaces.CritIndicator;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.item.weapon.melee.CustomMeleeWeapon;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.network.ClientPacketHandler;
import com.github.mim1q.minecells.registry.MineCellsItemGroups;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import com.google.common.collect.Streams;
import dev.mim1q.gimm1q.client.highlight.HighlightDrawerCallback;
import dev.mim1q.gimm1q.client.highlight.crosshair.CrosshairTipDrawerCallback;
import dev.mim1q.gimm1q.client.item.handheld.HandheldItemModelRegistry;
import dev.mim1q.gimm1q.client.tooltip.TooltipResolverRegistry;
import dev.mim1q.gimm1q.screenshake.ScreenShakeModifiers;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Locale;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class MineCellsClient implements ClientModInitializer {
  private static final Identifier CRIT_CROSSHAIR = MineCells.createId("textures/gui/crosshair/crit_indicator.png");

  public static final MineCellsClientConfig CLIENT_CONFIG = MineCellsClientConfig.createAndLoad();

  @Override
  public void onInitializeClient() {
    CLIENT_CONFIG.save();
    MineCellsRenderers.init();
    MineCellsRenderers.initBlocks();
    MineCellsItemGroups.init();
    ClientPacketHandler.init();
    MineCellsParticles.initClient();

    if (CLIENT_CONFIG.keepOriginalGuiModels()) setupAllHandheldModels();
    setupShieldHandheldModels();
    if (CLIENT_CONFIG.showCritIndicator()) setupCritIndicator();
    loadArrowModels();
    loadMiscCustomModels();
    setupTentacleWeaponHighlighting();
    setupLightningBoltHighlighting();
    setupTooltips();

    ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
      setupScreenShakeModifiers(CLIENT_CONFIG.screenShake().global);
    });
  }

  private void setupTooltips() {
    // Melee weapons
    TooltipResolverRegistry.getInstance().register((ctx, helper) -> {
        helper.defaultStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        var item = ctx.item();
        var tooltip = item.getItem().getTranslationKey(item) + ".description";
        var description = Text.translatableWithFallback(tooltip, "");
        if (!description.getString().isBlank()) {
          helper.maxLineWidth(40);
          helper.addLine(description);
        }
        var specialStyle = Style.EMPTY.withColor(Formatting.GOLD);
        var doesCrit = false;
        if (item.getItem() instanceof CrittingWeapon crittingWeapon) {
          var critDamage = crittingWeapon.getAdditionalCritDamage(item, null, ctx.player());
          if (critDamage > 0) {
            doesCrit = true;
            helper.addLine(Text.translatable("item.minecells.crit_damage", critDamage).setStyle(specialStyle));
          }
        }

        if (item.getItem() instanceof WeaponWithAbility weaponWithAbility) {
          if (doesCrit) {
            helper.addLine(Text.empty());
          }
          var damage = weaponWithAbility.getAbilityDamage(item, ctx.player(), null);
          var cooldown = weaponWithAbility.getAbilityCooldown(item, ctx.player()) / 20.0;
          helper.addLine(Text.translatable("item.minecells.special_ability_hold", damage, cooldown).setStyle(specialStyle));
        }
      },
      Streams.concat(
        CustomMeleeWeapon.getAllMeleeWeapons().stream(),
        Stream.of(
          MineCellsItems.FROST_BLAST,
          MineCellsItems.LIGHTNING_BOLT,
          MineCellsItems.ELECTRIC_WHIP,
          MineCellsItems.PHASER
        )
      ).toArray(Item[]::new)
    );

    TooltipResolverRegistry.getInstance().register((ctx, helper) -> {
        helper.defaultStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        var item = ctx.item();
        var tooltip = item.getItem().getTranslationKey(item) + ".description";
        var description = Text.translatableWithFallback(tooltip, "");
        if (!description.getString().isBlank()) {
          helper.maxLineWidth(40);
          helper.addLine(description);
        }
        var specialStyle = Style.EMPTY.withColor(Formatting.GOLD);
        if (item.getItem() instanceof CustomArrowShooter arrowShooter) {
          var type = arrowShooter.getArrowType();
          var context = ValueCalculatorContext.create()
            .with(ValueCalculatorParameter.HOLDER, ctx.player())
            .with(ValueCalculatorParameter.HOLDER_STACK, ctx.item());

          var drawTime = type.getDrawTime(context) / 20.0;
          var cooldown = type.getCooldown(context) / 20.0;
          var damage = type.getDamage(context);
          var critDamage = type.getAdditionalCritDamage(context);

          if (damage > 0) {
            helper.addLine(Text.translatable("item.minecells.bow.damage", damage).setStyle(specialStyle));
          }
          if (critDamage > 0) {
            helper.addLine(Text.translatable("item.minecells.bow.crit_damage", critDamage).setStyle(specialStyle));
          }
          if (drawTime > 0) {
            helper.addLine(Text.translatable("item.minecells.bow.draw_time", drawTime).setStyle(specialStyle));
          }
          if (cooldown > 0) {
            helper.addLine(Text.translatable("item.minecells.bow.cooldown", cooldown).setStyle(specialStyle));
          }
        }
      },
      Streams.concat(
        MineCellsItems.BOWS.stream(),
        MineCellsItems.CROSSBOWS.stream(),
        Stream.of(
          MineCellsItems.FIREBRANDS,
          MineCellsItems.THROWING_KNIFE
        )
      ).toArray(Item[]::new)
    );

    TooltipResolverRegistry.getInstance().register((ctx, helper) -> {
        helper.defaultStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        var item = ctx.item();
        var tooltip = item.getItem().getTranslationKey(item) + ".description";
        var description = Text.translatableWithFallback(tooltip, "");
        if (!description.getString().isBlank()) {
          helper.maxLineWidth(40);
          helper.addLine(description);
        }
        var specialStyle = Style.EMPTY.withColor(Formatting.GOLD);

        if (item.getItem() instanceof CustomShieldItem shieldItem) {
          var context = ValueCalculatorContext.create()
            .with(ValueCalculatorParameter.HOLDER, ctx.player())
            .with(ValueCalculatorParameter.HOLDER_STACK, ctx.item());

          var type = shieldItem.shieldType;

          var parryDamage = type.getParryDamage(context);
          var damageReduction = type.getBlockDamageReduction(context);
          var cooldown = type.getCooldown(context, false) / 20.0;

          if (parryDamage > 0) {
            helper.addLine(Text.translatable("item.minecells.shield.parry_damage", parryDamage).setStyle(specialStyle));
          }
          if (damageReduction > 0) {
            helper.addLine(Text.translatable("item.minecells.shield.damage_reduction", String.format(Locale.ROOT, "%.1f", damageReduction * 100.0)).setStyle(specialStyle));
          }
          if (cooldown > 0) {
            helper.addLine(Text.translatable("item.minecells.shield.cooldown", cooldown).setStyle(specialStyle));
          }
        }
      },
      MineCellsItems.SHIELDS.toArray(Item[]::new)
    );
  }

  private void loadMiscCustomModels() {
    ModelLoadingPlugin.register(context -> {
      var miscModels = Stream.of(
        "conjunctivius/chain",
        "conjunctivius/dash_chain"
      ).map(it -> MineCells.createId("misc/" + it)).toList();
      context.addModels(miscModels);
    });
  }

  private void setupTentacleWeaponHighlighting() {
    HighlightDrawerCallback.EVENT.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.isOf(MineCellsItems.TENTACLE)) {
        var hitResult = MineCellsItems.TENTACLE.hitResult;
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
          return;
        }
        if (hitResult.getType() == HitResult.Type.ENTITY) {
          drawer.highlightEntity(((EntityHitResult) hitResult).getEntity(), 0x00000000, 0xFFAA25EB);
          return;
        }

        drawer.highlightBlock(((BlockHitResult) hitResult).getBlockPos(), 0x00000000, 0xFFAA25EB);
      }
    });
  }

  private void setupLightningBoltHighlighting() {
    HighlightDrawerCallback.EVENT.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.isOf(MineCellsItems.LIGHTNING_BOLT)) {
        var entity = LightningBoltItem.getTargetedEntity(stack, ctx.player().getWorld());
        if (entity != null) {
          var ticks = ctx.player().getItemUseTime();
          var color = 0xFF000000 | LightningBoltItem.getLightningColor(ticks);
          drawer.highlightEntity(entity, 0x00000000, color);
        }
      }
    });
  }

  private void setupCritIndicator() {
    CrosshairTipDrawerCallback.register((drawer, ctx) -> {
      var stack = ctx.player().getMainHandStack();
      if (stack.getItem() instanceof CritIndicator item) {
        var hitResult = MinecraftClient.getInstance().crosshairTarget;
        LivingEntity entity = null;
        if (
          hitResult instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() instanceof LivingEntity livingEntity
        ) {
          entity = livingEntity;
        }
        if (item.shouldShowCritIndicator(ctx.player(), entity, stack)) {
          drawer.drawCrosshairTip(12, 0, 16, CRIT_CROSSHAIR);
        }
      }
    });
  }

  public static void setupScreenShakeModifiers(float d) {
    // Weapons
    ScreenShakeModifiers.setModifier("minecells:weapon_flint", d * CLIENT_CONFIG.screenShake().weaponFlint);
    ScreenShakeModifiers.setModifier("minecells:weapon_lightning_bolt", d * CLIENT_CONFIG.screenShake().weaponLightningBolt);

    // Shields
    ScreenShakeModifiers.setModifier("minecells:shield_block", d * CLIENT_CONFIG.screenShake().shieldBlock);
    ScreenShakeModifiers.setModifier("minecells:shield_parry", d * CLIENT_CONFIG.screenShake().shieldParry);

    // Conjunctivius
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_smash", d * CLIENT_CONFIG.screenShake().conjunctiviusSmash);
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_roar", d * CLIENT_CONFIG.screenShake().conjunctiviusRoar);
    ScreenShakeModifiers.setModifier("minecells:conjunctivius_death", d * CLIENT_CONFIG.screenShake().conjunctiviusDeath);

    // Concierge
    ScreenShakeModifiers.setModifier("minecells:concierge_leap", d * CLIENT_CONFIG.screenShake().conciergeLeap);
    ScreenShakeModifiers.setModifier("minecells:concierge_step", d * CLIENT_CONFIG.screenShake().conciergeStep);
    ScreenShakeModifiers.setModifier("minecells:concierge_roar", d * CLIENT_CONFIG.screenShake().conciergeRoar);
    ScreenShakeModifiers.setModifier("minecells:concierge_death", d * CLIENT_CONFIG.screenShake().conciergeDeath);

    // Explosions
    ScreenShakeModifiers.setModifier("minecells:explosion", d * CLIENT_CONFIG.screenShake().explosion);
  }

  private void setupAllHandheldModels() {
    setupHandheldModel(MineCellsItems.ASSASSINS_DAGGER);
    setupHandheldModel(MineCellsItems.BROADSWORD);
    setupHandheldModel(MineCellsItems.BALANCED_BLADE);
    setupHandheldModel(MineCellsItems.BLOOD_SWORD);
    setupHandheldModel(MineCellsItems.CROWBAR);
    setupHandheldModel(MineCellsItems.FLINT);
    setupHandheldModel(MineCellsItems.HATTORIS_KATANA);
    setupHandheldModel(MineCellsItems.SPITE_SWORD);
    setupHandheldModel(MineCellsItems.CURSED_SWORD);
    setupHandheldModel(MineCellsItems.PHASER);
    setupHandheldModel(MineCellsItems.FROST_BLAST);
    setupHandheldModel(MineCellsItems.NUTCRACKER);
    setupHandheldModel(MineCellsItems.TENTACLE);
  }

  private void setupHandheldModel(Item weapon) {
    var name = Registries.ITEM.getId(weapon).getPath();
    HandheldItemModelRegistry.getInstance().register(
      weapon,
      MineCells.createId("weapon/" + name),
      MineCells.createId(name)
    );
  }

  private void setupShieldHandheldModels() {
    for (var shield : MineCellsItems.SHIELDS) {
      var name = Registries.ITEM.getId(shield).getPath();
      HandheldItemModelRegistry.getInstance().register(
        shield,
        MineCells.createId(name),
        MineCells.createId("shield_3d/" + name)
      );
    }
  }

  private void loadArrowModels() {
    ModelLoadingPlugin.register(context -> {
      context.addModels(
        CustomArrowType
          .getAllNames()
          .stream()
          .map(it -> MineCells.createId("arrow/" + it))
          .toList()
      );
    });
  }
}
