package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.player.werewolf.actions.WerewolfFormAction;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WerewolfFormAction.class)
public class WerewolfFormActionMixin {

    @Inject(method = "getTimeModifier", at = @At("RETURN"), cancellable = true, remap = false)
    private void getTimeModifier(IWerewolfPlayer werewolf, CallbackInfoReturnable<Integer> cir) {
        Player player = werewolf.asEntity();
        if(WerewolvesAgeingConfig.werewolfAgeing.get()) {
            int age = AgeingManager.getAge(player).getAge();
            cir.setReturnValue(Math.round((float)cir.getReturnValue() * WerewolvesAgeingConfig.formTimeMultiplier.get().get(age).floatValue()));
        }
    }
}
