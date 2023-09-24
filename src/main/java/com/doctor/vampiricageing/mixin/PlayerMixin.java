package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private PlayerAbilities abilities;

    @Shadow protected FoodStats foodData;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, World p_20967_) {
        super(p_20966_, p_20967_);
    }

    //Uses to increase food exhaustion for the hunter player. Doesn't apply to all types of exhaustion, for example healing is still normal. Changing healing exhaustion would likely require a redirect on FoodData#tick which may be worse for compatability
    //Alternatively, a player tick event could be used for this to roughly guess when a player should add more exhaustion

    @Redirect(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;causeFoodExhaustion(F)V"))
    private void causeFoodExhaustion(PlayerEntity instance, float amount) {
        if(this.abilities.invulnerable) {
            return;
        }
        if(this.level.isClientSide) {
            return;
        }
        if(Helper.isHunter(this) && HunterAgeingConfig.hunterAgeing.get()) {
            int age = VampiricAgeingCapabilityManager.getAge(this).map(ageCap -> ageCap.getAge()).orElse(0);
            this.foodData.addExhaustion(amount * HunterAgeingConfig.fasterExhaustionAmounts.get().get(age));
        } else {
            this.foodData.addExhaustion(amount);
        }
    }
}
