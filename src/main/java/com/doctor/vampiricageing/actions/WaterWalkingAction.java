package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.ISpecialAttributes;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class WaterWalkingAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {
        activate(vampire);
        return true;
    }

    protected void activate(IVampirePlayer player) {
        ISpecialAttributes specialAttributes = (ISpecialAttributes) ((VampirePlayer) player).getSpecialAttributes();
        specialAttributes.ageing$setWaterWalking(true);
    }

    public int getCooldown(IVampirePlayer player) {
        return CommonConfig.waterWalkingCooldown.get() * 20;
    }

    public int getDuration(@NotNull IVampirePlayer player) {
        return Mth.clamp(CommonConfig.waterWalkingDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    public boolean isEnabled() {
        return CommonConfig.ageWaterWalking.get();
    }

    public void onActivatedClient(IVampirePlayer vampire) {
        activate(vampire);
    }

    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        ISpecialAttributes specialAttributes = (ISpecialAttributes) ((VampirePlayer) vampire).getSpecialAttributes();
        specialAttributes.ageing$setWaterWalking(false);
    }

    public void onReActivated(IVampirePlayer vampire) {
        activate(vampire);
    }

    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }

    public boolean showHudCooldown(Player player) {
        return true;
    }

    public boolean showHudDuration(Player player) {
        return true;
    }
}
