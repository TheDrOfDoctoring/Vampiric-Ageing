package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.InfectAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InfectAction.class)
public class InfectActionMixin  {
    //Increment the infected count, cant use the stat because infected count has to be reset.
    @Inject(method = "activate(Lde/teamlapen/vampirism/api/entity/player/vampire/IVampirePlayer;Lde/teamlapen/vampirism/api/entity/player/actions/IAction$ActivationContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;awardStat(Lnet/minecraft/util/ResourceLocation;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void activate(IVampirePlayer vampire, IAction.ActivationContext context, CallbackInfoReturnable<Boolean> cir, PlayerEntity player, Entity creature) {
        if(!player.getCommandSenderWorld().isClientSide) {
            if(VampiricAgeingCapabilityManager.canAge(player) && CommonConfig.biteBasedIncrease.get() && !creature.getType().is(EntityTypeTagProvider.infectedBlackList)) {
                VampiricAgeingCapabilityManager.incrementInfected((ServerPlayerEntity) player);
            }
            if(creature instanceof PlayerEntity && CommonConfig.sireingMechanic.get()) {
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
