package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

public class BitingMethod implements IAgeMethod {

    private static int[] infected;
    private static final String ID = "BITING";


    @Override
    public int[] getRankProgressions() {
        if(infected == null) {
            infected = Arrays.stream(CommonConfig.infectedForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return infected;
    }
    @Override
    public boolean isEnabled() {
        return CommonConfig.ageingMethod.get().equals(ID);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IAgeType getValidType() {
        return AgeingReference.VAMP;
    }

    @Override
    public void displayLevelRequirements(Player player, int points, int age) {
        int pointsForNextAge = getRankProgressions()[age] - points;
        player.displayClientMessage(Component.translatable("text.vampiricageing.progress_infected", pointsForNextAge).withStyle(ChatFormatting.DARK_RED), true);
    }

}
