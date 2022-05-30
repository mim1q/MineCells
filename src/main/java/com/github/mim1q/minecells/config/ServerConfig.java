package com.github.mim1q.minecells.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;

public class ServerConfig implements Config {

    @Override
    public String getName() {
        return "minecells-server";
    }

    Elevator elevator = new Elevator();

    public void correctValues() {

    }

    private static class Elevator {
        @Comment(" default: 256, min: 1, max: 320")
        public int maxHeight = 256;
        @Comment(" default: 1, min: 1, max: 10")
        public int minHeight = 1;
        @Comment(" default: 1.0, min: 0.1, max: 2.5")
        public float speed = 1.0F;
        @Comment(" default: 0.01, min: 0.001, max: 0.1")
        public float acceleration = 0.01F;
    }
}
