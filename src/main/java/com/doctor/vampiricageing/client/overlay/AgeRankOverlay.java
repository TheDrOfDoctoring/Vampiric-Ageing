package com.doctor.vampiricageing.client.overlay;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AgeRankOverlay extends GuiComponent implements IGuiOverlay {
    //This is basically just a copy of the FactionLevelOverlay
    private final Minecraft mc = Minecraft.getInstance();
    @Override
    public void render(@NotNull ForgeGui gui, @NotNull PoseStack mStack, float partialTicks, int width, int height) {
        if (this.mc.player != null && this.mc.player.isAlive() && this.mc.player.getVehicle() == null && !this.mc.options.hideGui) {
            gui.setupOverlayRenderState(true, false);
            VampiricAgeingCapabilityManager.getAge(this.mc.player).ifPresent(age -> {
                if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && age.getAge() > 0) {
                    String text = String.valueOf(age.getAge());
                    int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + ClientConfig.guiLevelOffsetX.get();
                    int y = this.mc.getWindow().getGuiScaledHeight() - (ClientConfig.guiLevelOffsetY.get() - 47) - gui.rightHeight;
                    this.mc.font.draw(mStack, text, x + 1, y, 0);
                    this.mc.font.draw(mStack, text, x - 1, y, 0);
                    this.mc.font.draw(mStack, text, x, y + 1, 0);
                    this.mc.font.draw(mStack, text, x, y - 1, 0);
                    this.mc.font.draw(mStack, text, x, y, 0x8B0000);
                }
            });
        }
    }
}
