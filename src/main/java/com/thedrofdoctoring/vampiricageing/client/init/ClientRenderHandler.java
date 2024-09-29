package com.thedrofdoctoring.vampiricageing.client.init;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class ClientRenderHandler {

    private @Nullable Bat entityBat;
    private final Minecraft mc;
    private int screenPercentage = 0;


    public ClientRenderHandler(@NotNull Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        boolean batMode = ((IHunterSpecialAttributes) HunterPlayer.get(player).getSpecialAttributes()).ageing$getBatMode();
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
        if(mc.player == null) return;
        HunterPlayer player = HunterPlayer.get(mc.player);
        IHunterSpecialAttributes specialAttributes = (IHunterSpecialAttributes) player.getSpecialAttributes();
        if (mc.player.isAlive() && player.getSpecialAttributes() != null && specialAttributes.ageing$getBatMode()) {
            event.setCanceled(true);
        }
    }
    //for sun damage, essentially a copy of the sun overlay for vampires
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {

        if (mc.player == null || !mc.player.isAlive()) {
            screenPercentage = 0;
            return;
        }

        if (!HunterAgeingConfig.sunAffectTainted.get() || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        if(FactionPlayerHandler.get(mc.player).getCurrentFactionPlayer().isPresent()) {
            @Nullable IFactionPlayer<?> player = FactionPlayerHandler.get(mc.player).getCurrentFactionPlayer().get();
            if (player instanceof HunterPlayer) {
                handleScreenColour((HunterPlayer) player);
            } else {
                screenPercentage = 0;
            }
        }
    }
    private void handleScreenColour(HunterPlayer player) {
        int ticksInSun = ((IHunterSpecialAttributes) player.getSpecialAttributes()).ageing$getTicksInSun();
        if (( ticksInSun / 100) > 0 && !player.getRepresentingPlayer().hasEffect(ModEffects.SUNSCREEN)) {
            screenPercentage = ticksInSun / 50;
            screenPercentage = Math.min(screenPercentage, VampirismConfig.BALANCE.vpMaxYellowBorderPercentage.get());
        } else {
            screenPercentage = 0;
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGui(RenderGuiEvent.Pre event) {
        if (this.screenPercentage > 0) {
            PoseStack stack = event.getGuiGraphics().pose();
            int color = 0xfffff422;
            int w = (event.getGuiGraphics().guiWidth());
            int h = (event.getGuiGraphics().guiHeight());
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            float a = (screenPercentage / 100f) * (color >> 24 & 255) / 255F;
            Matrix4f matrix = stack.last().pose();
            VertexConsumer buffer = event.getGuiGraphics().bufferSource().getBuffer(RenderType.guiOverlay());
            buffer.addVertex(matrix, 0, h, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, w, h, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, w, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(matrix, 0, 0, 0).setColor(r, g, b, a);
            event.getGuiGraphics().flush();
            stack.popPose();
        }
    }
}
