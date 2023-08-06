package com.github.mim1q.minecells.mixin.world;

import com.github.mim1q.minecells.world.placement.InsideGridPlacement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
  @Inject(
    method = "locateStructure(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/registry/entry/RegistryEntryList;Lnet/minecraft/util/math/BlockPos;IZ)Lcom/mojang/datafixers/util/Pair;",
    at = @At("RETURN"),
    cancellable = true)
  private void minecells$injectLocateStructure(ServerWorld world, RegistryEntryList<Structure> structures, BlockPos center, int radius, boolean skipReferencedStructures, CallbackInfoReturnable<@Nullable Pair<BlockPos, RegistryEntry<Structure>>> cir) {
    var returnValue = cir.getReturnValue();
    if (returnValue == null) {
      structures.stream().forEach(entry -> {
        var placements = world.getChunkManager().getStructurePlacementCalculator().getPlacements(entry);
        placements.forEach(
          placement -> {
            if (placement instanceof InsideGridPlacement gridPlacement) {
              var closest = gridPlacement.getClosestPosition(new ChunkPos(center), world.getSeed());
              if (closest != null) {
                cir.setReturnValue(Pair.of(closest, entry));
              }
            }
          }
        );
      });
    }
  }
}
