package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.werewolves.entities.player.werewolf.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.player.werewolf.actions.WerewolfFormAction;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WerewolfFormAction.class)
public class WerewolfFormActionMixin {

    @Inject(method = "getTimeModifier", at = @At("RETURN"), cancellable = true, remap = false)
    private void getTimeModifier(IWerewolfPlayer werewolf, CallbackInfoReturnable<Integer> cir) {
        PlayerEntity player = werewolf.getRepresentingPlayer();
        if(WerewolvesAgeingConfig.werewolfAgeing.get()) {
            int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            cir.setReturnValue(Math.round((float)cir.getReturnValue() * WerewolvesAgeingConfig.formTimeMultiplier.get().get(age)));
        }
    }
}
