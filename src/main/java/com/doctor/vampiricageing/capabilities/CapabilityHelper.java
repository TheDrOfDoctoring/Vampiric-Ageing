package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    public static int getCumulativeTaintedAge(Player player) {
        if(!Helper.isHunter(player) || !HunterAgeingConfig.taintedBloodAvailable.get()) {
            return 0;
        }
        boolean transformed = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.isTransformed()).orElse(false);
        int tainted = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getTemporaryTaintedAgeBonus()).orElse(0);
        int bonus = transformed ? 6 : tainted;
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age == 0 && !transformed) {
            return 0;
        }
        return age + bonus;
    }
    public static void increasePoints(ServerPlayer player, int points) {
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(age -> {
            age.setHunted(age.getHunted() + points);
            VampiricAgeingCapabilityManager.syncAgeCap(player);
            if(shouldIncreaseRankHunted(player)) {
                VampiricAgeingCapabilityManager.increaseAge(player);
            }

        });
    }
    public static boolean shouldIncreaseRankHunted(Player player) {
        if(Helper.isVampire(player)) {
            return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getHunted() >= CommonConfig.huntedForNextAge.get().get(age.getAge())).orElse(false);
        } else {
            return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getHunted() >= HunterAgeingConfig.huntedForNextAge.get().get(age.getAge())).orElse(false);
        }
    }


}
