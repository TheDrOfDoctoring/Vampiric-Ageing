package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;

public class DrainBloodAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public DrainBloodAction() {
    }

    public boolean activate(IVampirePlayer vampire, IAction.ActivationContext context) {
        return true;
    }

    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return VampiricAgeingCapabilityManager.getAge(vampire.getRepresentingPlayer()).orElse(null).getAge() >= CommonConfig.drainBloodActionRank.get();
    }
    public int getCooldown() {
        return (Integer) CommonConfig.drainBloodActionCooldown.get() * 20;
    }

    public int getDuration(int i) {
        return 20 * CommonConfig.drainBloodActionDuration.get();
    }

    public boolean isEnabled() {
        return CommonConfig.drainBloodAction.get();
    }

    public void onActivatedClient(IVampirePlayer vampire) {
    }

    public void onDeactivated(IVampirePlayer vampire) {

    }

    public void onReActivated(IVampirePlayer vampire) {
    }

    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }

}
