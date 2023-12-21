package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mim1q.minecells.util.MathUtils.getClosestMultiplePosition;
import static com.github.mim1q.minecells.util.TextUtils.addDescription;

public class DimensionalRuneItem extends Item {
  private static final String ONLY_USABLE_IN_OVERWORLD_KEY = "item.minecells.dimensional_rune.only_usable_in_overworld";
  private static final String NOT_VISITED_KEY = "item.minecells.dimensional_rune.not_visited";
  private static final String TOOLTIP_KEY = "item.minecells.dimensional_rune.tooltip";

  public final DoorwayPortalBlock portalBlock;

  public DimensionalRuneItem(Settings settings, DoorwayPortalBlock portalBlock) {
    super(settings);
    this.portalBlock = portalBlock;
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    var world = context.getWorld();
    if (context.getPlayer() == null) return ActionResult.PASS;

    var state = world.getBlockState(context.getBlockPos());
    if (!(state.getBlock() instanceof DoorwayPortalBlock)) return ActionResult.PASS;
    if (state.isOf(portalBlock)) return ActionResult.PASS;

    if (!(world.getRegistryKey().getValue().getPath().equals("overworld"))) {
      context.getPlayer().sendMessage(Text.translatable(ONLY_USABLE_IN_OVERWORLD_KEY), true);
      return ActionResult.PASS;
    }

    if (world.isClient) {
      return ActionResult.SUCCESS;
    }

    var doorway = world.getBlockEntity(context.getBlockPos());
    if (!(doorway instanceof DoorwayPortalBlockEntity)) return ActionResult.PASS;

    var posOverride = ((DoorwayPortalBlockEntity) doorway).getPosOverride();
    if (posOverride == null) posOverride = new BlockPos(getClosestMultiplePosition(context.getBlockPos(), 1024));

    var playerData = MineCellsData.getPlayerData(
      (ServerPlayerEntity) context.getPlayer(),
      (ServerWorld) world,
      posOverride
    );

    if (portalBlock.type.dimension != MineCellsDimension.PRISONERS_QUARTERS && !playerData.hasVisitedDimension(portalBlock.type.dimension)) {
      context.getPlayer().sendMessage(Text.translatable(NOT_VISITED_KEY), true);
      return ActionResult.PASS;
    }

    var pos = Vec3d.ofCenter(context.getBlockPos());
    ((ServerWorld) world).spawnParticles(MineCellsParticles.SPECKLE.get(portalBlock.type.color), pos.getX(), pos.getY(), pos.getZ(), 50, 0.5F, 0.1F, 0.5F, 0.1F);
    ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 3, 0.5F, 0.1F, 0.5F, 0.1F);

    world.setBlockState(
      context.getBlockPos(),
      portalBlock.getDefaultState().with(DoorwayPortalBlock.FACING, state.get(DoorwayPortalBlock.FACING))
    );

    var newDoorwayEntity = world.getBlockEntity(context.getBlockPos());
    if (newDoorwayEntity instanceof DoorwayPortalBlockEntity newDoorway) {
      newDoorway.setPosOverride(posOverride);
    }

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

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    addDescription(tooltip, TOOLTIP_KEY);
  }
}
