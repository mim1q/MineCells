package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.Identifier;

public class PacketIdentifiers {
  public static final Identifier CRIT = MineCells.createId("crit");
  public static final Identifier EXPLOSION = MineCells.createId("explosion");
  public static final Identifier CONNECT = MineCells.createId("connect");
  public static final Identifier ELEVATOR_DESTROYED = MineCells.createId("elevator_destroyed");

  public static final Identifier USE_TENTACLE = MineCells.createId("use_tentacle_c2s");
}
