package com.doctor.vampiricageing.client.overlay;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;


public class AgeRankOverlay implements IGuiOverlay {
    //This is basically just a copy of the FactionLevelOverlay
    private final Minecraft mc = Minecraft.getInstance();


    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        if (this.mc.player != null && this.mc.player.isAlive() && this.mc.player.getVehicle() == null && !this.mc.options.hideGui && !this.mc.player.isInWater()) {
            gui.setupOverlayRenderState(true, false);
            VampiricAgeingCapabilityManager.getAge(this.mc.player).ifPresent(age -> {
                if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && age.getAge() > 0) {
                    String text = String.valueOf(age.getAge());
                    int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + ClientConfig.guiLevelOffsetX.get();
                    int y = this.mc.getWindow().getGuiScaledHeight() - (ClientConfig.guiLevelOffsetY.get() - 47) - gui.rightHeight;
                    graphics.drawString(this.mc.font, text, x + 1, y, 0, false);
                    graphics.drawString(this.mc.font, text, x - 1, y, 0, false);
                    graphics.drawString(this.mc.font, text, x, y + 1, 0, false);
                    graphics.drawString(this.mc.font, text, x, y - 1, 0, false);
                    graphics.drawString(this.mc.font, text, x, y, 0x8B0000, false);
                }
            });
        }
    }
}
