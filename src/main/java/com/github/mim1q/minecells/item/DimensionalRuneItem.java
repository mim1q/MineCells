package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;

public class DimensionalRuneItem extends Item {
  private static final String ONLY_USABLE_IN_OVERWORLD_KEY = "item.minecells.dimensional_rune.only_usable_in_overworld";

  public final DoorwayPortalBlock portalBlock;

  public DimensionalRuneItem(Settings settings, DoorwayPortalBlock portalBlock) {
    super(settings);
    this.portalBlock = portalBlock;
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    var world = context.getWorld();

    var state = world.getBlockState(context.getBlockPos());
    if (!(state.getBlock() instanceof DoorwayPortalBlock)) return ActionResult.PASS;
    if (state.isOf(portalBlock)) return ActionResult.PASS;

    if (!(world.getRegistryKey().getValue().getPath().equals("overworld"))) {
      if (context.getPlayer() != null) {
        context.getPlayer().sendMessage(Text.translatable(ONLY_USABLE_IN_OVERWORLD_KEY), true);
      }
      return ActionResult.PASS;
    }

    if (world.isClient) {
      ParticleUtils.addAura(
        (ClientWorld) world,
        Vec3d.ofCenter(context.getBlockPos()),
        MineCellsParticles.SPECKLE.get(portalBlock.type.color),
        50, 0.5F, 0.1F
      );
      ParticleUtils.addAura(
        (ClientWorld) world,
        Vec3d.ofCenter(context.getBlockPos()),
        ParticleTypes.EXPLOSION,
        3, 0.5F, 0.1F
      );
      return ActionResult.SUCCESS;
    }
    world.setBlockState(
      context.getBlockPos(),
      portalBlock.getDefaultState().with(DoorwayPortalBlock.FACING, state.get(DoorwayPortalBlock.FACING))
    );
    world.playSound(
      null,
      context.getBlockPos(),
      MineCellsSounds.TELEPORT_RELEASE,
      SoundCategory.BLOCKS,
      1.0F, 1.0F
    );
    context.getStack().decrement(1);
    return ActionResult.CONSUME_PARTIAL;
  }
}
