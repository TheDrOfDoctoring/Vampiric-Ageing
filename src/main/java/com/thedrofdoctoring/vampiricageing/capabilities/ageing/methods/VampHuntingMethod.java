package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;

public class VampHuntingMethod extends HuntingMethod {

    private static final String ID = "V_HUNTING";

    private static int[] points;

    @Override
    public void onAgedKill(LivingEntity target, Player sourceKiller) {
        int pointWorth = 0;
        if(target.getType().is(EntityTypeTagProvider.pettyHuntVampire)) {
            pointWorth = CommonConfig.pettyHuntWorth.get();
        } else if(target.getType().is(EntityTypeTagProvider.commonHuntVampire)) {
            pointWorth = CommonConfig.commonHuntWorth.get();
        } else if(target.getType().is(EntityTypeTagProvider.greaterHuntVampire)) {
            pointWorth = CommonConfig.greaterHuntWorth.get();
        }
        AgeingManager.getAge(sourceKiller).increaseRankPoints(pointWorth);

    }
    @Override
    public boolean isEnabled() {
        return CommonConfig.ageingMethod.get().equals(getId());
    }


    @Override
    public int[] getRankProgressions() {
        if(points == null) {
            points = Arrays.stream(CommonConfig.huntedForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return points;
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
    public String getRemainingLang() {
        return "text.vampiricageing.progress_hunted_vampire";
    }
}
