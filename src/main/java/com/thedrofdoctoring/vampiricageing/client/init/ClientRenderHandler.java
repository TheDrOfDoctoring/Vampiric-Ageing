package com.thedrofdoctoring.vampiricageing.client.init;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttachments;
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

    private final Minecraft mc;


    public ClientRenderHandler(@NotNull Minecraft mc) {
        this.mc = mc;
    }
    //Copied from Vampirism's Bat Rendering.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderPlayerPreHigh(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        boolean batMode = ((IHunterSpecialAttributes) HunterPlayer.get(player).getSpecialAttributes()).ageing$getBatMode();
        if (batMode) {
            event.setCanceled(true);
            var bat = player.getData(ModAttachments.VAMPIRE_BAT);

            float partialTicks = event.getPartialTick();


            // Copy values
            bat.yBodyRotO = player.yBodyRotO;
            bat.yBodyRot = player.yBodyRot;
            bat.tickCount = player.tickCount;
            bat.setXRot(player.getXRot());
            bat.setYRot(player.getYRot());
            bat.yHeadRot = player.yHeadRot;
            bat.yRotO = player.yRotO;
            bat.xRotO = player.xRotO;
            bat.yHeadRotO = player.yHeadRotO;
            bat.setInvisible(player.isInvisible());

            // Calculate render parameter
            double d0 = Mth.lerp(partialTicks, bat.xOld, bat.getX());
            double d1 = Mth.lerp(partialTicks, bat.yOld, bat.getY());
            double d2 = Mth.lerp(partialTicks, bat.zOld, bat.getZ());
            float f = Mth.lerp(partialTicks, bat.yRotO, bat.getYRot());
            mc.getEntityRenderDispatcher().render(bat, d0, d1, d2, f, partialTicks, event.getPoseStack(), mc.renderBuffers().bufferSource(), mc.getEntityRenderDispatcher().getPackedLightCoords(bat, partialTicks));
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


}
