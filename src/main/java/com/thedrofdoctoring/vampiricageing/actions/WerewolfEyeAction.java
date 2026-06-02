package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.cache.AgeingPlayerCache;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.player.werewolf.actions.DefaultWerewolfAction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class WerewolfEyeAction extends DefaultWerewolfAction implements ILastingAction<IWerewolfPlayer> {
    public static final ResourceLocation SLOWNESS = VampiricAgeing.rl("werewolf_eye_slowness");


    public boolean activate(@NotNull IWerewolfPlayer werewolf, IAction.ActivationContext context) {
        activate(werewolf);
        if(WerewolvesAgeingConfig.improvedSensesSlowdown.getAsBoolean()) {
            werewolf.asEntity().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SLOWNESS, -0.95f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        return true;
    }

    private void activate(IWerewolfPlayer werewolfPlayer) {
        AgeingPlayerCache.get(werewolfPlayer.asEntity()).hasBypassInvisibility = true;
    }

    public boolean canBeUsedBy(@NotNull IWerewolfPlayer werewolf) {
        return AgeingManager.getAge(werewolf.asEntity()).getAge() >= WerewolvesAgeingConfig.improvedSensesAge.get();
    }

    public int getCooldown(IWerewolfPlayer player) {
        return WerewolvesAgeingConfig.improvedSensesCooldown.get();
    }

    public int getDuration(@NotNull IWerewolfPlayer player) {
        return Mth.clamp(WerewolvesAgeingConfig.improvedSensesDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onActivatedClient(IWerewolfPlayer werewolf) {
        activate(werewolf);
    }

    public void onDeactivated(@NotNull IWerewolfPlayer werewolf) {
        AgeingPlayerCache.get(werewolf.asEntity()).hasBypassInvisibility = false;
        AgeingManager.removeModifier(werewolf.asEntity().getAttribute(Attributes.MOVEMENT_SPEED), SLOWNESS);

    }

    public void onReActivated(IWerewolfPlayer werewolf) {
        activate(werewolf);
    }

    public boolean onUpdate(IWerewolfPlayer werewolf) {
        return false;
    }

    public boolean showHudCooldown(Player player) {
        return false;
    }

    public boolean showHudDuration(Player player) {
        return false;
    }
}
