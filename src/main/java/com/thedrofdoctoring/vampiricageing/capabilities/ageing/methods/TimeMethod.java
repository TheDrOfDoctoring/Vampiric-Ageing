package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;

import java.util.Arrays;

public class TimeMethod implements IAgeMethod {

    private static int[] ticks;
    private static final String ID = "TIME";


    @Override
    public int[] getRankProgressions() {
        if(ticks == null) {
            ticks = Arrays.stream(CommonConfig.ticksForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return ticks;
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
    public boolean isEnabled() {
        return CommonConfig.ageingMethod.get().equals(ID);
    }

    @Override
    public String getRemainingLang() {
        return "text.vampiricageing.progress_ticks";
    }
}
