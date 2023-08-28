package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BloodStats.class)
public class BloodStatsMixin {

    @Shadow @Final private Player player;

    //Forcing vampirism to take into account the Age based multiplier for blood exhaustion.

    @Inject(method = "addExhaustion(FZ)V", at = @At(value = "HEAD"), remap = false)
    void addExhaustion(float amount, boolean ignoreModifier, CallbackInfo ci) {
        if(CommonConfig.shouldAgeAffectExhaustion.get() && ignoreModifier) {
            if(player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).getModifier(VampiricAgeingCapabilityManager.EXHAUSTION_UUID) != null) {
                double exhaustionMult = player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).getModifier(VampiricAgeingCapabilityManager.EXHAUSTION_UUID).getAmount();
                amount = (float) (amount * exhaustionMult);
            }
        }
    }
}
