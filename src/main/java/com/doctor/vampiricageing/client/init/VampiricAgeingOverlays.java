package com.doctor.vampiricageing.client.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.client.overlay.AgeRankOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class VampiricAgeingOverlays {

    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "agerankoverlay", new AgeRankOverlay());
    }
}
