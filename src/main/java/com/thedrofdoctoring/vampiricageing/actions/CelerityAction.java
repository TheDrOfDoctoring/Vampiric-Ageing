package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CelerityAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer>  {

    public boolean activate(@NotNull IVampirePlayer vampire, IAction.ActivationContext context) {
        vampire.getRepresentingPlayer().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(VampiricAgeing.rl("celerity_speed_increase"), CommonConfig.celerityActionMultiplier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return true;
    }

    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return AgeingManager.getAge(vampire.asEntity()).getAge() >= CommonConfig.celerityActionRank.get();
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
        AgeingManager.removeModifier(vampire.asEntity().getAttribute(Attributes.MOVEMENT_SPEED), VampiricAgeing.rl("celerity_speed_increase"));
    }

    public void onReActivated(IVampirePlayer vampire) {
    }

    public boolean onUpdate(IVampirePlayer vampire) {
        if(!vampire.asEntity().getCommandSenderWorld().isClientSide) {
            Player player = vampire.asEntity();
            ModParticles.spawnParticlesServer(player.getCommandSenderWorld(), new GenericParticleOptions(ResourceLocation.fromNamespaceAndPath("minecraft", "generic_4"), 5, 0xd3d3d3, 0.1F) , player.getX(), player.getY(), player.getZ(), 3, 0,0, 0, 0);
        }
        return false;
    }

    public boolean showHudCooldown(Player player) {
        return true;
    }

    public boolean showHudDuration(Player player) {
        return true;
    }
}
