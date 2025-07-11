package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

public class HunterHuntingMethod extends HuntingMethod {

    private static final String ID = "HUNTING";

    private static int[] points;

    @Override
    public void onAgedKill(LivingEntity target, Player sourceKiller) {
        int pointWorth = 0;
        if(target.getType().is(EntityTypeTagProvider.pettyHunt)) {
            pointWorth = HunterAgeingConfig.pettyHuntWorth.get();
        } else if(target.getType().is(EntityTypeTagProvider.commonHunt)) {
            pointWorth = HunterAgeingConfig.commonHuntWorth.get();
        } else if(target.getType().is(EntityTypeTagProvider.greaterHunt)) {
            pointWorth = HunterAgeingConfig.greaterHuntWorth.get();
        }
        AgeingManager.getAge(sourceKiller).increaseRankPoints(pointWorth);

    }


    @Override
    public int[] getRankProgressions() {
        if(points == null) {
            points = Arrays.stream(HunterAgeingConfig.huntedForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return points;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.ageingMethod.get().equals(getId());
    }

    @Override
    public IAgeType getValidType() {
        return AgeingReference.HUNTER;
    }

    @Override
    public void displayLevelRequirements(Player player, int points, int age) {
        int pointsForNextAge = getRankProgressions()[age] - points;
        player.displayClientMessage(Component.translatable("text.vampiricageing.progress_hunted", pointsForNextAge).withStyle(ChatFormatting.DARK_RED), true);
    }
}
