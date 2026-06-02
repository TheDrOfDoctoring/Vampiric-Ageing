package com.thedrofdoctoring.vampiricageing.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.werewolves.entities.player.ModPlayerEventHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModPlayerEventHandler.class)
public class WWModPlayerEventHandlerMixin {

    @ModifyExpressionValue(
            method = "onJump",
            at = @At(value = "INVOKE", target = "Lde/teamlapen/werewolves/api/entities/werewolf/WerewolfForm;getLeapModifier()F")
    )
    private float getModifiedLeapFactor(float original, LivingEvent.LivingJumpEvent event) {
        if(event.getEntity() instanceof Player player) {
            int age = AgeingManager.getAge(player).getAge();
            double factor = WerewolvesAgeingConfig.leapStrengthMultiplier.get().get(age);
            return (float) (original * factor);
        }
        return original;
    }
}
