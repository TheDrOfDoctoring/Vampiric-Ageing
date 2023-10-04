package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.DamageHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageHandler.class)
public abstract class DamageHandlerMixin {

    @Inject(method = "affectEntityHolyWaterSplash(Lnet/minecraft/entity/LivingEntity;Lde/teamlapen/vampirism/api/EnumStrength;DZLnet/minecraft/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/util/Helper;isVampire(Lnet/minecraft/entity/Entity;)Z"), remap = false)
    private static void affectEntityHolyWaterSplash(LivingEntity entity, EnumStrength strength, double distSq, boolean directHit, LivingEntity source, CallbackInfo ci) {
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            int cumulative = CapabilityHelper.getCumulativeTaintedAge(player);
            if(cumulative >= HunterAgeingConfig.taintedBloodHolyWaterAffectedAge.get()) {
                double affect = 1.0 - Math.sqrt(distSq) / 4.0;
                if (directHit) {
                    affect = 1.0;
                }
                double amount = (affect * (VampirismConfig.BALANCE.holyWaterSplashDamage.get() * (strength == EnumStrength.WEAK ? 1 : strength == EnumStrength.MEDIUM ? VampirismConfig.BALANCE.holyWaterTierDamageInc.get() : (VampirismConfig.BALANCE.holyWaterTierDamageInc.get() * VampirismConfig.BALANCE.holyWaterTierDamageInc.get()))) + 0.5D);
                amount = DamageHandler.scaleDamageWithLevel(cumulative, 10, amount * 0.6, amount * 1.15);
                entity.hurt(VReference.HOLY_WATER, (float) amount);
                if (strength.isStrongerThan(EnumStrength.WEAK)) {
                    entity.addEffect(new EffectInstance(Effects.CONFUSION, VampirismConfig.BALANCE.holyWaterNauseaDuration.get(), 2));
                }
                if (strength.isStrongerThan(EnumStrength.MEDIUM)) {
                    entity.addEffect(new EffectInstance(Effects.BLINDNESS, VampirismConfig.BALANCE.holyWaterBlindnessDuration.get(), 1));
                }
            }
        }
    }
}
