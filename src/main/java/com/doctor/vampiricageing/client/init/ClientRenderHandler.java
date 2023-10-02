package com.doctor.vampiricageing.client.init;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.networking.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientRenderHandler {

    private @Nullable Bat entityBat;
    private final Minecraft mc;

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
}
