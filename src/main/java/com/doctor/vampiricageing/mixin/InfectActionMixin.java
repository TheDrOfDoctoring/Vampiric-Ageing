package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.InfectAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InfectAction.class)
public class InfectActionMixin  {
    //Increment the infected count, cant use the stat because infected count has to be reset.
    @Inject(method = "activate(Lde/teamlapen/vampirism/api/entity/player/vampire/IVampirePlayer;Lde/teamlapen/vampirism/api/entity/player/actions/IAction$ActivationContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void activate(IVampirePlayer vampire, IAction.ActivationContext context, CallbackInfoReturnable<Boolean> cir, Player player, Entity creature) {
        if(!player.getCommandSenderWorld().isClientSide) {
            if(VampiricAgeingCapabilityManager.canAge(player) && CommonConfig.biteBasedIncrease.get() ) {
                VampiricAgeingCapabilityManager.incrementInfected((ServerPlayer) player);
            }
            if(creature instanceof Player && CommonConfig.sireingMechanic.get()) {
                VampiricAgeingCapabilityManager.getAge(player).ifPresent(vampireAge -> {
                    if (vampireAge.getAge() > 1) {
                        creature.getPersistentData().remove("AGE");
                        creature.getPersistentData().putInt("AGE", vampireAge.getAge() - 1);
                    }
                });
            }
        }
    }
}
