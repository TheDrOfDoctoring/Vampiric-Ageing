package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BloodStats.class)
public class BloodStatsMixin {

    @Shadow @Final private Player player;

    @ModifyArg(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V"))
    private float modifyAgeingHeal(float orig) {
        int age = AgeingManager.getAge(player).getAge();
        float multiplier = CommonConfig.ageHealingMultiplier.get().get(age).floatValue();
        return orig * multiplier;
    }
}
