package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.IAgeingCapability;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
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
        int age = VampiricAgeingCapabilityManager.getAge(player).map(IAgeingCapability::getAge).orElse(0);
        float multiplier = CommonConfig.ageHealingMultiplier.get().get(age).floatValue();
        return orig * multiplier;
    }
}
