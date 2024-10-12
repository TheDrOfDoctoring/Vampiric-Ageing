package com.thedrofdoctoring.vampiricageing.client.overlay;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.gui.overlay.TextureOverlay;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

/**
 * This is essentially a duplicate of <a href="https://github.com/TeamLapen/Vampirism/blob/1.21/src/main/java/de/teamlapen/vampirism/client/gui/overlay/SunOverlay.java">...</a>
 */
public class TaintedSunOverlay extends TextureOverlay {


    public static final ResourceLocation SUN_TEXTURE = VResourceLocation.mod("textures/misc/sun.png");

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (this.mc.player != null && VampirismConfig.CLIENT.enableSunOverlayRendering.get()) {
            LocalPlayer player = this.mc.player;
            MobEffectInstance effect = player.getEffect(ModEffects.SUNSCREEN);
            if(AgeingManager.getAge(player).getTypeState() instanceof HunterAgeingType.HunterState state) {
                float progress = Math.clamp(state.getTicksInSun() / 1500f, 0f, 1f);
                if (progress > 0 && (effect == null || effect.getAmplifier() < 5)) {
                    if (player.getAbilities().instabuild || (effect != null && effect.getAmplifier() >= 3)) {
                        progress = Math.min(0.5f, progress);
                    }
                    graphics.pose().pushPose();
                    scaleBy(progress, 1 / 5f, 2F, 1.0F, graphics);
                    renderTextureOverlay(graphics, SUN_TEXTURE, 1.0F);
                    graphics.pose().popPose();
                }
            }

        }
    }
}
