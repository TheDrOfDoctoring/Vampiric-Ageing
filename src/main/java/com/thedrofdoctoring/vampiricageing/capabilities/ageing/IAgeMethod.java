package com.thedrofdoctoring.vampiricageing.capabilities.ageing;

import net.minecraft.world.entity.player.Player;

public interface IAgeMethod {

    int[] getRankProgressions();
    String getId();
    IAgeType getValidType();
    boolean isEnabled();

    void displayLevelRequirements(Player player, int points, int age);
}
