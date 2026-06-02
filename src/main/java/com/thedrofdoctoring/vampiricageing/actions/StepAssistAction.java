package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class StepAssistAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public static final ResourceLocation STEP_HEIGHT = VampiricAgeing.rl("vamp_ageing_step_height");

    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {
        vampire.asEntity().getAttribute(Attributes.STEP_HEIGHT).addPermanentModifier(new AttributeModifier(STEP_HEIGHT, 0.5f, AttributeModifier.Operation.ADD_VALUE));
        return true;
    }

    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return AgeingManager.getAge(vampire.asEntity()).getAge() >= CommonConfig.stepAssistBonus.get();
    }

    public int getCooldown(IVampirePlayer player) {
        return CommonConfig.stepAssistCooldown.get();
    }

    public int getDuration(@NotNull IVampirePlayer player) {
        return Mth.clamp(CommonConfig.stepAssistDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onActivatedClient(IVampirePlayer vampire) {
    }

    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        AgeingManager.removeModifier(vampire.asEntity().getAttribute(Attributes.STEP_HEIGHT), STEP_HEIGHT);
    }

    public void onReActivated(IVampirePlayer vampire) {
    }

    public boolean onUpdate(IVampirePlayer vampire) {
        return false;
    }

    public boolean showHudCooldown(Player player) {
        return false;
    }

    public boolean showHudDuration(Player player) {
        return false;
    }
}
