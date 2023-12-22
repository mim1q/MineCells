package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.block.ReturnStoneBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReturnStoneBlockEntity extends MineCellsBlockEntity {
  private Identifier structure = null;
  private int windup = 0;
  private ServerPlayerEntity player = null;

  public ReturnStoneBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RETURN_STONE, pos, state);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    var structureKey = nbt.getString("structure");
    if (!structureKey.isBlank()) {
      structure = new Identifier(structureKey);
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    if (structure != null) {
      nbt.putString("structure", structure.toString());
    }
  }

  public void teleportPlayer(ServerWorld world, BlockPos pos, PlayerEntity player) {
    if (structure != null && !structure.getPath().isBlank()) {
      var key = RegistryKey.of(RegistryKeys.STRUCTURE, structure);
      var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);
      var found = world.getChunkManager().getChunkGenerator().locateStructure(
        world,
        RegistryEntryList.of(structureRegistry.entryOf(key)),
        pos,
        128,
        false
      );
      if (found != null) {
        var structurePos = found.getFirst();
        world.getChunk(structurePos);
        var y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, structurePos.getX(), structurePos.getZ());
        player.teleport(structurePos.getX() + 0.5, y, structurePos.getZ() + 0.5);
        world.playSound(null, player.getBlockPos(), MineCellsSounds.TELEPORT_RELEASE, SoundCategory.BLOCKS, 1F, 1F);
        world.spawnParticles(ReturnStoneBlock.PARTICLE, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 1.0, 0.5, 0.025);
      }
      return;
    }

    BlockPos targetPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
    for (var offset : BlockPos.iterateOutwards(pos, 30, 0, 30)) {
      var topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, offset);
      if (world.getBlockState(topPos.down()).isIn(MineCellsBlockTags.RETURN_STONE_TARGETS)) {
        targetPos = topPos;
        break;
      }
    }
    if (targetPos == null) {
      return;
    }
    Vec3d tpPos = Vec3d.ofBottomCenter(targetPos);
    player.teleport(tpPos.x, tpPos.y, tpPos.z);
    world.playSound(null, player.getBlockPos(), MineCellsSounds.TELEPORT_RELEASE, SoundCategory.BLOCKS, 1F, 1F);
    world.spawnParticles(ReturnStoneBlock.PARTICLE, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 1.0, 0.5, 0.025);
  }

  public ActionResult setPlayer(ServerPlayerEntity player) {
    if (this.player != null || this.windup > 0 || world == null) {
      return ActionResult.FAIL;
    }
    this.player = player;
    this.windup = 25;
    world.playSound(null, pos, MineCellsSounds.TELEPORT_CHARGE, SoundCategory.BLOCKS, 1F, 1F);
    return ActionResult.SUCCESS;
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    if (world.isClient) {
      return;
    }
    if (windup > 0) {
      windup--;
      ((ServerWorld) world).spawnParticles(
        ReturnStoneBlock.PARTICLE,
        pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5,
        5,
        0.25, 0.25, 0.25,
        0.01
      );
      ((ServerWorld) world).spawnParticles(
        ReturnStoneBlock.PARTICLE,
        player.getX(),
        player.getY() + 1.0,
        player.getZ(), 5,
        0.5, 1.0, 0.5,
        0.01
      );
    }
    if (windup == 0 && player != null) {
      teleportPlayer((ServerWorld) world, pos, player);
      player = null;
    }
  }
}
