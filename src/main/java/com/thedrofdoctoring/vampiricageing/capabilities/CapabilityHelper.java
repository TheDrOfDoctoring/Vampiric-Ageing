package com.thedrofdoctoring.vampiricageing.capabilities;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.AgeingRegistry;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.werewolves.api.WReference;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

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
        AgeingManager age = AgeingManager.getAge(player);
        if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
            boolean transformed = state.isTransformed();
            int tainted = state.getTemporaryTaintedAgeBonus();
            int bonus = transformed ? 6 : tainted;
            int rank = age.getAge();
            if(rank == 0 && !transformed) {
                return 0;
            }
            return rank + bonus;
        }
        return 0;

    }
    public static void setDefaultAgeTypeAndMethod(Player player) {
        AgeingManager manager = AgeingManager.getAge(player);
        for(IAgeType type: AgeingRegistry.getAgeingTypes()) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
                if(type.isEnabled() && handler.getCurrentLevel() >= type.minFactionRank() && type.faction() == handler.getCurrentFaction()) {
                    manager.setType(type);
                }
        }
        for(IAgeMethod method : AgeingRegistry.getAgeingMethods()) {
            if(method.isEnabled() && method.getValidType() == manager.getType()) {
                manager.setMethod(method);
            }
        }
        manager.sync(false);
    }


}
