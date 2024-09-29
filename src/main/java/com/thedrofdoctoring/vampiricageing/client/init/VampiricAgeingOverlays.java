package com.thedrofdoctoring.vampiricageing.client.init;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.client.overlay.AgeRankOverlay;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class VampiricAgeingOverlays {

    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, VampiricAgeing.rl("age_overlay"), new AgeRankOverlay());
    }
}
