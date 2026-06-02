package com.thedrofdoctoring.vampiricageing.client;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.capabilities.cache.AgeingPlayerCache;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.util.Helper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;

@EventBusSubscriber(modid = VampiricAgeing.MODID, value = {Dist.CLIENT})
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerInput(MovementInputUpdateEvent event) {

        if(event.getInput().jumping && AgeingPlayerCache.get(event.getEntity()).hasBypassInvisibility) {

            if(Helper.isHunter(event.getEntity()) && HunterAgeingConfig.wiseEyeSlowdown.getAsBoolean()) {
                event.getInput().jumping = false;
            }
            if(CapabilityHelper.isWerewolfCheckMod(event.getEntity()) && WerewolvesAgeingConfig.improvedSensesSlowdown.getAsBoolean()) {
                event.getInput().jumping = false;
            }

        }

    }
}
