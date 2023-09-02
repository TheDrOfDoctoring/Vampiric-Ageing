package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BloodStats.class)
public abstract class BloodStatsMixin implements IBloodStats {

    //Forcing vampirism to take into account the Age based multiplier for blood exhaustion.

    @Shadow @Final private Player player;

    @Shadow private float bloodExhaustionLevel;

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/entity/player/vampire/BloodStats;addExhaustion(FZ)V"), remap = false)
    private void addExhaustion(BloodStats bloodStats, float amount, boolean ignoreModifier) {
        if (!ignoreModifier) {
            AttributeInstance attribute = player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get());
            amount *= attribute.getValue();
        }
        else if(CommonConfig.shouldAgeAffectExhaustion.get()) {
            if(this.player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).getModifier(VampiricAgeingCapabilityManager.EXHAUSTION_UUID) != null) {
                double exhaustionMult = player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).getModifier(VampiricAgeingCapabilityManager.EXHAUSTION_UUID).getAmount();
                amount = (float) (amount * exhaustionMult);
            }
        }
        this.bloodExhaustionLevel = Math.min(this.bloodExhaustionLevel + amount, 40.0F);
    }
}
