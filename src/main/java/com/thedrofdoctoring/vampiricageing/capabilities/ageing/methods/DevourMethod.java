package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import net.neoforged.fml.ModList;

import java.util.Arrays;

public class DevourMethod implements IAgeMethod {
    private static int[] devoured;
    private static final String ID = "DEVOUR";


    @Override
    public int[] getRankProgressions() {
        if(devoured == null) {
            devoured = Arrays.stream(WerewolvesAgeingConfig.devouredForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return devoured;
    }
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded("werewolves") && WerewolvesAgeingConfig.ageingMethod.get().equals(ID);
    }
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IAgeType getValidType() {
        return AgeingReference.WEREWOLF;
    }

    @Override
    public String getRemainingLang() {
        return "text.vampiricageing.progress_devour";
    }
}
