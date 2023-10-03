package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class CapabilityHelper {

    public static boolean isWerewolfCheckMod(Entity entity) {
        if(!ModList.get().isLoaded(VampiricAgeing.WEREWOLVES_MODID)) {
            return false;
        } else {
            return Helper.isWerewolf(entity);
        }
    }
    public static int getCumulativeTaintedAge(Player player) {
        if(!Helper.isHunter(player) || !HunterAgeingConfig.taintedBloodAvailable.get()) {
            return 0;
        }
        int tainted = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getTemporaryTaintedAgeBonus()).orElse(0);
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        return age == 0 ? 0 : tainted + age;
    }
}