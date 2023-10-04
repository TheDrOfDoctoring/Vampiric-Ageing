package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageHandler.class)
public abstract class DamageHandlerMixin {

    @Inject(method = "affectEntityHolyWaterSplash(Lnet/minecraft/world/entity/LivingEntity;Lde/teamlapen/vampirism/api/EnumStrength;DZLnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/util/Helper;isVampire(Lnet/minecraft/world/entity/Entity;)Z"), remap = false)
    private static void affectEntityHolyWaterSplash(LivingEntity entity, EnumStrength strength, double distSq, boolean directHit, LivingEntity source, CallbackInfo ci) {
        if(entity instanceof Player) {
            Player player = (Player) entity;
            int cumulative = CapabilityHelper.getCumulativeTaintedAge(player);
            if(cumulative >= HunterAgeingConfig.taintedBloodHolyWaterAffectedAge.get()) {
                double affect = 1.0 - Math.sqrt(distSq) / 4.0;
                if (directHit) {
                    affect = 1.0;
                }
                double amount = (affect * (VampirismConfig.BALANCE.holyWaterSplashDamage.get() * (strength == EnumStrength.WEAK ? 1 : strength == EnumStrength.MEDIUM ? VampirismConfig.BALANCE.holyWaterTierDamageInc.get() : (VampirismConfig.BALANCE.holyWaterTierDamageInc.get() * VampirismConfig.BALANCE.holyWaterTierDamageInc.get()))) + 0.5D);
                amount = DamageHandler.scaleDamageWithLevel(cumulative, 10, amount * 0.6, amount * 1.15);
                DamageHandler.hurtModded(entity, ModDamageSources::holyWater, (float) amount);
                if (strength.isStrongerThan(EnumStrength.WEAK)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, VampirismConfig.BALANCE.holyWaterNauseaDuration.get(), 2));
                }
                if (strength.isStrongerThan(EnumStrength.MEDIUM)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, VampirismConfig.BALANCE.holyWaterBlindnessDuration.get(), 1));
                }
            }
        }
    }
}
