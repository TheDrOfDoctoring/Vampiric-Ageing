package com.thedrofdoctoring.vampiricageing.client.overlay;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.config.ClientConfig;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import org.jetbrains.annotations.NotNull;


public class AgeRankOverlay implements LayeredDraw.Layer {
    //This is basically just a copy of the FactionLevelOverlay
    private final Minecraft mc = Minecraft.getInstance();
    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker partialTicks) {
        if (this.mc.player != null && this.mc.player.isAlive() && !this.mc.player.isUnderWater() && this.mc.player.getVehicle() == null && !this.mc.options.hideGui) {
            AgeingManager age = AgeingManager.getAge(mc.player);
            int rank = age.getAge();

            boolean transformed = false;
            if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
                transformed = state.isTransformed();
            }
            IAgeType type = age.getAgeType();
            if (type != null && this.mc.gameMode != null && this.mc.gameMode.hasExperience() && (rank > 0 || transformed)) {
                String text = type.getAgeTitle(rank);
                int width = this.mc.font.width(text);
                int x = (this.mc.getWindow().getGuiScaledWidth() - width) / 2 + ClientConfig.guiLevelOffsetX.get();
                int y = this.mc.getWindow().getGuiScaledHeight() - ClientConfig.guiLevelOffsetY.get();
                if(!text.isEmpty()) {
                    graphics.drawString(this.mc.font, text, x + 1, y, 0, false);
                    graphics.drawString(this.mc.font, text, x - 1, y, 0, false);
                    graphics.drawString(this.mc.font, text, x, y + 1, 0, false);
                    graphics.drawString(this.mc.font, text, x, y - 1, 0, false);
                    graphics.drawString(this.mc.font, text, x, y, 0x8B0000, false);
                }
                int tempTainted = 0;
                if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
                    tempTainted = state.getTemporaryTaintedAgeBonus();
                }
                if(Helper.isHunter(this.mc.player) && ClientConfig.showTaintedAgeRank.get() && HunterAgeingConfig.taintedBloodAvailable.get() && (tempTainted > 0 || transformed)) {
                    int displayBonus = transformed  ? 6 : tempTainted;
                    String taintedTextValue = " (" + (rank + displayBonus) + ")";
                    int x2 = x + 1 + width;
                    graphics.drawString(this.mc.font, taintedTextValue, x2 + 1, y, 0, false);
                    graphics.drawString(this.mc.font, taintedTextValue, x2 - 1, y, 0, false);
                    graphics.drawString(this.mc.font, taintedTextValue, x2, y + 1, 0, false);
                    graphics.drawString(this.mc.font, taintedTextValue, x2, y - 1, 0, false);
                    graphics.drawString(this.mc.font, taintedTextValue, x2, y, Color.MAGENTA_DARK.getRGB(), false);
                }
            }
        }
    }

}
