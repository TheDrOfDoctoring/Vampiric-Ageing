package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.cache.AgeingPlayerCache;
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
public class HunterEyeAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public static final ResourceLocation SLOWNESS = VampiricAgeing.rl("hunter_eye_slowness");


    public boolean activate(@NotNull IHunterPlayer hunter, IAction.ActivationContext context) {
        activate(hunter);
        if(HunterAgeingConfig.wiseEyeSlowdown.getAsBoolean()) {
            hunter.asEntity().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SLOWNESS, -0.95f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        return true;
    }

    private void activate(IHunterPlayer hunterPlayer) {
        AgeingPlayerCache.get(hunterPlayer.asEntity()).hasBypassInvisibility = true;
    }

    public boolean canBeUsedBy(@NotNull IHunterPlayer hunter) {
        return AgeingManager.getAge(hunter.asEntity()).getAge() >= HunterAgeingConfig.wiseEyeAge.get();
    }

    public int getCooldown(IHunterPlayer player) {
        return HunterAgeingConfig.wiseEyeCooldown.get();
    }

    public int getDuration(@NotNull IHunterPlayer player) {
        return Mth.clamp(HunterAgeingConfig.stepAssistDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    public boolean isEnabled() {
        return true;
    }

    public void onActivatedClient(IHunterPlayer hunter) {
        activate(hunter);
    }

    public void onDeactivated(@NotNull IHunterPlayer hunter) {
        AgeingPlayerCache.get(hunter.asEntity()).hasBypassInvisibility = false;
        AgeingManager.removeModifier(hunter.asEntity().getAttribute(Attributes.MOVEMENT_SPEED), SLOWNESS);

    }

    public void onReActivated(IHunterPlayer hunter) {
        activate(hunter);
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
