package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.werewolves.util.WReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.ModList;

public class CapabilityHelper {

    public static boolean isWerewolfCheckMod(Entity entity) {
        if(!ModList.get().isLoaded(VampiricAgeing.WEREWOLVES_MODID)) {
            return false;
        } else {
            return de.teamlapen.werewolves.util.Helper.isWerewolf(entity);
        }
    }
    public static boolean isWerewolfCheckMod(Entity entity, IPlayableFaction<?> faction) {
        if(!ModList.get().isLoaded(VampiricAgeing.WEREWOLVES_MODID)) {
            return false;
        } else {
            return de.teamlapen.werewolves.util.Helper.isWerewolf(entity) || faction == WReference.WEREWOLF_FACTION;
        }
    }
    public static int getCumulativeTaintedAge(PlayerEntity player) {
        if(!Helper.isHunter(player) || !HunterAgeingConfig.taintedBloodAvailable.get()) {
            return 0;
        }
        int tainted = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getTemporaryTaintedAgeBonus()).orElse(0);
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        return age == 0 ? 0 : tainted + age;
    }
    public static boolean shouldIncreaseRankHunted(PlayerEntity player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getHunted() >= HunterAgeingConfig.huntedForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static void increasePoints(ServerPlayerEntity player, int points) {
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(age -> {
            age.setHunted(age.getHunted() + points);
            VampiricAgeingCapabilityManager.syncAgeCap(player);
            if (shouldIncreaseRankHunted(player)) {
                VampiricAgeingCapabilityManager.increaseAge(player);
            }
        });
    }


}
