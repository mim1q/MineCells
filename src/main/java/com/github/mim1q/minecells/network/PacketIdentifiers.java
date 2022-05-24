package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.util.Identifier;

public class PacketIdentifiers {
    public static final Identifier CRIT = new Identifier(MineCells.MOD_ID, "crit");
    public static final Identifier EXPLOSION = new Identifier(MineCells.MOD_ID, "explosion");
    public static final Identifier CONNECT = new Identifier(MineCells.MOD_ID, "connect");
    public static final Identifier ELEVATOR_DESTROYED = new Identifier(MineCells.MOD_ID, "elevator_destroyed");
}
