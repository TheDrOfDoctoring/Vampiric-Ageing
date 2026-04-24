package com.thedrofdoctoring.vampiricageing.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModPlayerEventHandler.class)
public class ModPlayerEventHandlerMixin {

    @ModifyExpressionValue(
            method = "canStartSleeping",
            at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/util/Helper;isVampire(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean isTaintedBloodStartSleep(boolean original, @Local(argsOnly = true) CanPlayerSleepEvent event) {
        int taintedAge = CapabilityHelper.getCumulativeTaintedAge(event.getEntity());
        if(taintedAge >= HunterAgeingConfig.taintedAgeForCoffinUse.get()) {
            return true;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "canContinueToSleep",
            at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/util/Helper;isVampire(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private boolean isTaintedBloodContinueSleep(boolean original, @Local(argsOnly = true) CanContinueSleepingEvent event) {
        if(!(event.getEntity() instanceof Player player)) return original;
        int taintedAge = CapabilityHelper.getCumulativeTaintedAge(player);
        if(taintedAge >= HunterAgeingConfig.taintedAgeForCoffinUse.get()) {
            return true;
        }
        return original;
    }
}
