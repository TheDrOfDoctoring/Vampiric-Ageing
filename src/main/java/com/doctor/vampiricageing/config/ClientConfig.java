package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.IntValue guiLevelOffsetX;
    public static final ForgeConfigSpec.IntValue guiLevelOffsetY;
    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        guiLevelOffsetX = CLIENT_BUILDER.comment("X-Offset of the age indicator from the center in pixels").defineInRange("levelOffsetX", 50, 0, 100);
        guiLevelOffsetY = CLIENT_BUILDER.comment("Y-Offset of the age indicator from the bottom in pixels").defineInRange("levelOffsetY", 45, 0, 270);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
