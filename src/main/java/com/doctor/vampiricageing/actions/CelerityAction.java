package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CelerityAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer>  {

    public static final UUID CELERITY_UUID = UUID.fromString("31c9aa6e-38e9-40ad-868f-b494981605a8");
    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {
        vampire.getRepresentingPlayer().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(CELERITY_UUID, "SPEED_CELERITY_INCREASE", CommonConfig.celerityActionMultiplier.get(), AttributeModifier.Operation.MULTIPLY_BASE));
        return true;
    }

    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return VampiricAgeingCapabilityManager.getAge(vampire.getRepresentingPlayer()).orElse(null).getAge() >= CommonConfig.celerityActionRank.get();
    }

    public int getCooldown(IVampirePlayer player) {
        return (Integer) CommonConfig.celerityActionCooldown.get() * 20;
    }

    public int getDuration(@NotNull IVampirePlayer player) {
        return 20 * CommonConfig.celerityActionDuration.get();
    }

    public boolean isEnabled() {
        return CommonConfig.celerityAction.get();
    }

    public void onActivatedClient(IVampirePlayer vampire) {
    }

    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        VampiricAgeingCapabilityManager.removeModifier(vampire.getRepresentingPlayer().getAttribute(Attributes.MOVEMENT_SPEED), CELERITY_UUID);
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
