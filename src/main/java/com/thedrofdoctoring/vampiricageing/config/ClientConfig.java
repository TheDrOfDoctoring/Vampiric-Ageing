package com.thedrofdoctoring.vampiricageing.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.IntValue guiLevelOffsetX;
    public static final ModConfigSpec.IntValue guiLevelOffsetY;
    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        guiLevelOffsetX = CLIENT_BUILDER.comment("X-Offset of the age rank indicator from the center in pixels").defineInRange("ageRankOffsetX", 50, -250, 250);
        guiLevelOffsetY = CLIENT_BUILDER.comment("Y-Offset of the age rank indicator from the bottom in pixels").defineInRange("ageRankOffsetY", 47, 0, 270);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
