package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfig implements Config {

    @Override
    public String getName() {
        return "minecells-client";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    public Rendering rendering = new Rendering();

    @Override
    public void save() {
        Config.super.save();
    }

    public static class Rendering {
        @Comment(" default: true")
        public boolean shockerGlow = true;

        @Comment(" default: true")
        public boolean grenadierGlow = true;

        @Comment(" default: true")
        public boolean leapingZombieGlow = true;

        @Comment(" default: true")
        public boolean disgustingWormGlow = true;

        @Comment(" default: true")
        public boolean protectorGlow = true;
    }
}
