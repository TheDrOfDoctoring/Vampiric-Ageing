package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class CelerityAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer>  {

    public static final UUID CELERITY_UUID = UUID.fromString("31c9aa6e-38e9-40ad-868f-b494981605a8");
    public boolean activate(IVampirePlayer vampire, IAction.ActivationContext context) {
        vampire.getRepresentingPlayer().getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(CELERITY_UUID, "SPEED_CELERITY_INCREASE", CommonConfig.celerityActionMultiplier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        return true;
    }

    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return VampiricAgeingCapabilityManager.getAge(vampire.getRepresentingPlayer()).orElse(null).getAge() >= CommonConfig.celerityActionRank.get();
    }

    public int getCooldown() {
        return (Integer) CommonConfig.celerityActionCooldown.get() * 20;
    }

    public int getDuration(int i) {
        return 20 * CommonConfig.celerityActionDuration.get();
    }

    public boolean isEnabled() {
        return CommonConfig.celerityAction.get();
    }

    public void onActivatedClient(IVampirePlayer vampire) {
    }

    public void onDeactivated(IVampirePlayer vampire) {
        VampiricAgeingCapabilityManager.removeModifier(vampire.getRepresentingPlayer().getAttribute(Attributes.MOVEMENT_SPEED), CELERITY_UUID);
    }

    public void onReActivated(IVampirePlayer vampire) {
    }

    public boolean onUpdate(IVampirePlayer vampire) {
        if(!vampire.getRepresentingPlayer().getCommandSenderWorld().isClientSide) {
            PlayerEntity player = vampire.getRepresentingPlayer();
            ModParticles.spawnParticlesServer(player.getCommandSenderWorld(), new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "generic_4"), 5, 0xd3d3d3, 0.1F), player.getX(), player.getY(), player.getZ(), 3, 0,0, 0, 0);
        }
        return false;
    }

}
