package com.doctor.vampiricageing.client.overlay;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.ClientConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class AgeRankOverlay {
    //This is basically just a copy of the FactionLevelOverlay
    private final Minecraft mc = Minecraft.getInstance();
    @SubscribeEvent
    public void onRenderFoodBar(RenderGameOverlayEvent.Post event) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && this.mc.player != null && this.mc.player.isAlive() && this.mc.player.getVehicle() == null && !this.mc.options.hideGui && !this.mc.player.isInWater()) {
            MatrixStack mStack = event.getMatrixStack();
            VampiricAgeingCapabilityManager.getAge(this.mc.player).ifPresent(age -> {
                if (this.mc.gameMode != null && this.mc.gameMode.hasExperience() && age.getAge() > 0) {
                    String text = String.valueOf(age.getAge());
                    int x = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + (Integer) ClientConfig.guiLevelOffsetX.get();
                    int y = this.mc.getWindow().getGuiScaledHeight() - (Integer)ClientConfig.guiLevelOffsetY.get();
                    this.mc.font.draw(mStack, text, (float)(x + 1), (float)y, 0);
                    this.mc.font.draw(mStack, text, (float)(x - 1), (float)y, 0);
                    this.mc.font.draw(mStack, text, (float)x, (float)(y + 1), 0);
                    this.mc.font.draw(mStack, text, (float)x, (float)(y - 1), 0);
                    this.mc.font.draw(mStack, text, (float)x, (float)y, 0x8B0000);
                    if(Helper.isHunter(this.mc.player) && HunterAgeingConfig.taintedBloodAvailable.get() && age.getTemporaryTaintedAgeBonus() > 0) {
                        String taintedTextValue = " (" + (age.getAge() + age.getTemporaryTaintedAgeBonus()) + ")";
                        int x2 = (this.mc.getWindow().getGuiScaledWidth() - this.mc.font.width(text)) / 2 + ClientConfig.guiLevelOffsetX.get() + 5;
                        int y2 = this.mc.getWindow().getGuiScaledHeight() - (Integer)ClientConfig.guiLevelOffsetY.get();
                        this.mc.font.draw(mStack, taintedTextValue, x2 + 1, y2, 0);
                        this.mc.font.draw(mStack, taintedTextValue, x2 - 1, y2, 0);
                        this.mc.font.draw(mStack, taintedTextValue, x2, y2 + 1, 0);
                        this.mc.font.draw(mStack, taintedTextValue, x2, y2 - 1, 0);
                        this.mc.font.draw(mStack, taintedTextValue, x2, y2, Color.MAGENTA.darker().darker().getRGB());
                    }
                }
            });
        }
    }
}
