package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.InfectAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InfectAction.class)
public class InfectActionMixin  {
    //Increment the infected count, cant use the stat because infected count has to be reset.
    @Inject(method = "activate(Lde/teamlapen/vampirism/api/entity/player/vampire/IVampirePlayer;Lde/teamlapen/vampirism/api/entity/player/actions/IAction$ActivationContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"))
    private void activate(IVampirePlayer vampire, IAction.ActivationContext context, CallbackInfoReturnable<Boolean> cir) {
        Player player = vampire.getRepresentingPlayer();
        if(VampiricAgeingCapabilityManager.canAge(player) && CommonConfig.biteBasedIncrease.get() && !player.getCommandSenderWorld().isClientSide) {
            VampiricAgeingCapabilityManager.incrementInfected((ServerPlayer) player);

        }
    }
}
