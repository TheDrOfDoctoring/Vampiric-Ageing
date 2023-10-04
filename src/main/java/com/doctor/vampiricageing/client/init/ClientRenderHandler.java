package com.doctor.vampiricageing.client.init;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.networking.ClientProxy;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import de.teamlapen.lib.lib.client.gui.ExtendedGui;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientRenderHandler extends ExtendedGui {

    private @Nullable Bat entityBat;
    private final Minecraft mc;
    private int screenPercentage = 0;


    public ClientRenderHandler(@NotNull Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        boolean batMode = VampiricAgeingCapabilityManager.getAge(player).map(hunter -> hunter.getBatMode()).orElse(false);
        if (batMode) {
            event.setCanceled(true);
            if (entityBat == null) {
                entityBat = EntityType.BAT.create(event.getEntity().getCommandSenderWorld());
                entityBat.setResting(false);
            }
            float partialTicks = event.getPartialTick();

            // Copy values
            entityBat.yBodyRotO = player.yBodyRotO;
            entityBat.yBodyRot = player.yBodyRot;
            entityBat.tickCount = player.tickCount;
            entityBat.setXRot(player.getXRot());
            entityBat.setYRot(player.getYRot());
            entityBat.yHeadRot = player.yHeadRot;
            entityBat.yRotO = player.yRotO;
            entityBat.xRotO = player.xRotO;
            entityBat.yHeadRotO = player.yHeadRotO;
            entityBat.setInvisible(player.isInvisible());

            // Calculate render parameter
            double d0 = Mth.lerp(partialTicks, entityBat.xOld, entityBat.getX());
            double d1 = Mth.lerp(partialTicks, entityBat.yOld, entityBat.getY());
            double d2 = Mth.lerp(partialTicks, entityBat.zOld, entityBat.getZ());
            float f = Mth.lerp(partialTicks, entityBat.yRotO, entityBat.getYRot());
            mc.getEntityRenderDispatcher().render(entityBat, d0, d1, d2, f, partialTicks, event.getPoseStack(), mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getPackedLightCoords(entityBat, partialTicks));
        }
    }
    @SubscribeEvent
    public void onRenderHand(@NotNull RenderHandEvent event) {
        if (mc.player != null && mc.player.isAlive() && VampiricAgeingCapabilityManager.getAge(mc.player).map(hunter -> hunter.getBatMode()).orElse(false)) {
            event.setCanceled(true);
        }
    }
    //for sun damage, essentially a copy of the sun overlay for vampires
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mc.player == null || !mc.player.isAlive()) {
            screenPercentage = 0;
            return;
        }
        if (event.phase == TickEvent.Phase.END || !HunterAgeingConfig.sunAffectTainted.get() || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        @Nullable IFactionPlayer<?> player = FactionPlayerHandler.getOpt(mc.player).resolve().flatMap(FactionPlayerHandler::getCurrentFactionPlayer).orElse(null);
        if (player instanceof HunterPlayer) {
            handleScreenColour((HunterPlayer) player);
        } else {
            screenPercentage = 0;
        }
    }
    private void handleScreenColour(HunterPlayer player) {
        VampiricAgeingCapabilityManager.getAge(player.getRepresentingPlayer()).ifPresent(hunter -> {
            if ((hunter.getTicksInSun() / 100) > 0 && !player.getRepresentingPlayer().hasEffect(ModEffects.SUNSCREEN.get())) {
                screenPercentage = hunter.getTicksInSun() / 50;
                screenPercentage = Math.min(screenPercentage, VampirismConfig.BALANCE.vpMaxYellowBorderPercentage.get());
            } else {
                screenPercentage = 0;
            }

        });
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLast(RenderGuiEvent.Pre event) {
        int percentages = 0;
        int color = 0;
        if (this.screenPercentage > 0) {
            percentages = this.screenPercentage;
            color = 0xfffff422;
        }

        if (percentages > 0 && VampirismConfig.CLIENT.renderScreenOverlay.get()) {
            PoseStack stack = event.getGuiGraphics().pose();
            stack.pushPose();
            int w = (this.mc.getWindow().getGuiScaledWidth());
            int h = (this.mc.getWindow().getGuiScaledHeight());

            int bh = Math.round(h / (float) 4 * percentages / 100);
            int bw = Math.round(w / (float) 8 * percentages / 100);

            this.fillGradient2(stack, 0, 0, w, bh, color, 0x000);
            if (!OptifineHandler.isShaders()) {
                this.fillGradient2(stack, 0, h - bh, w, h, 0x00000000, color);
            }
            this.fillGradient2(stack, 0, 0, bw, h, 0x000000, color);
            this.fillGradient2(stack, w - bw, 0, w, h, color, 0x00);

            stack.popPose();
        }
    }
}
