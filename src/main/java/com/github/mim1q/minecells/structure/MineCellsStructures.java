package com.github.mim1q.minecells.structure;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.structure.grid.GridBasedStructures;
import com.github.mim1q.minecells.structure.grid.GridPiece;
import com.mojang.serialization.Codec;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Locale;

public class MineCellsStructures {
  // Structures
  public static final StructureType<GridBasedStructures.Prison> PRISON = registerStructure(
    "prison",
    GridBasedStructures.Prison.CODEC
  );
  // Structure Pieces
  public static final StructurePieceType GRID_PIECE = registerPiece(GridPiece::new, "grid_generator_piece");

  private static <S extends Structure> StructureType<S> registerStructure(String id, Codec<S> codec) {
    return Registry.register(Registry.STRUCTURE_TYPE, MineCells.createId(id), () -> codec);
  }

  private static StructurePieceType registerPiece(StructurePieceType type, String id) {
    return Registry.register(Registry.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
  }

  public static void init() {

  }
}
