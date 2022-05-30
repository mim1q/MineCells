package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfig implements Config {

    public Rendering rendering = new Rendering();

    @Override
    public String getName() {
        return "minecells-client";
    }

    public static class Rendering {
        @Comment("Default: true")
        public boolean shockerGlow = true;

        @Comment("Default: true")
        public boolean grenadierGlow = true;

        @Comment("Default: true")
        public boolean leapingZombieGlow = true;

        @Comment("Default: true")
        public boolean disgustingWormGlow = true;

        @Comment("Default: true")
        public boolean protectorGlow = true;
    }
}
