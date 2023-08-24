package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DrainBloodAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public DrainBloodAction() {
    }

    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {

        return true;
    }

    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return VampiricAgeingCapabilityManager.getAge(vampire.getRepresentingPlayer()).orElse(null).getAge() >= CommonConfig.drainBloodActionRank.get();
    }

    public int getCooldown(IVampirePlayer player) {
        return (Integer) CommonConfig.drainBloodActionCooldown.get() * 20;
    }

    public int getDuration(@NotNull IVampirePlayer player) {
        return 20 * CommonConfig.drainBloodActionDuration.get();
    }

    public boolean isEnabled() {
        return CommonConfig.drainBloodAction.get();
    }

    public void onActivatedClient(IVampirePlayer vampire) {
    }

    public void onDeactivated(@NotNull IVampirePlayer vampire) {

    }

    public void onReActivated(IVampirePlayer vampire) {
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
