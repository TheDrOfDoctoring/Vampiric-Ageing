package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;

public class CapabilityHelper {

    public static boolean isWerewolfCheckMod(Entity entity) {
        if(!ModList.get().isLoaded(VampiricAgeing.WEREWOLVES_MODID)) {
            return false;
        } else {
            return Helper.isWerewolf(entity);
        }
    }
}
