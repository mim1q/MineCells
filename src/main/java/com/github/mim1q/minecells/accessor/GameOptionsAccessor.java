package com.github.mim1q.minecells.accessor;

import net.minecraft.client.option.SimpleOption;

public interface GameOptionsAccessor {
  SimpleOption<?> minecells$getMinecellsScreenshake();
  float minecells$getMinecellsScreenshakeValue();
}
