package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class StepAssistHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static final ResourceLocation STEP_HEIGHT = VampiricAgeing.rl("hunter_ageing_step_height");


    public boolean activate(@NotNull IHunterPlayer hunter, IAction.ActivationContext context) {
        hunter.asEntity().getAttribute(Attributes.STEP_HEIGHT).addPermanentModifier(new AttributeModifier(STEP_HEIGHT, 0.5f, AttributeModifier.Operation.ADD_VALUE));
        return true;
    }

    public boolean canBeUsedBy(@NotNull IHunterPlayer hunter) {
        return AgeingManager.getAge(hunter.asEntity()).getAge() >= HunterAgeingConfig.stepAssistAge.get();
    }

    public int getCooldown(IHunterPlayer player) {
        return HunterAgeingConfig.stepAssistCooldown.get();
    }

    public int getDuration(@NotNull IHunterPlayer player) {
        return Mth.clamp(HunterAgeingConfig.stepAssistDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onActivatedClient(IHunterPlayer hunter) {
    }

    public void onDeactivated(@NotNull IHunterPlayer hunter) {
        AgeingManager.removeModifier(hunter.asEntity().getAttribute(Attributes.STEP_HEIGHT), STEP_HEIGHT);
    }

    public void onReActivated(IHunterPlayer hunter) {
    }

    public boolean onUpdate(IHunterPlayer hunter) {
        return false;
    }

    public boolean showHudCooldown(Player player) {
        return false;
    }

    public boolean showHudDuration(Player player) {
        return false;
    }
}
