package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DrainBloodAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {
    public DrainBloodAction() {
    }

    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {

        return true;
    }

    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return AgeingManager.getAge(vampire.asEntity()).getAge() >= CommonConfig.drainBloodActionRank.get();
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
