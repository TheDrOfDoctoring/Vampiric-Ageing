package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private Abilities abilities;

    @Shadow protected FoodData foodData;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    //Uses to increase food exhaustion for the hunter player. Doesn't apply to all types of exhaustion, for example healing is still normal. Changing healing exhaustion would likely require a redirect on FoodData#tick which may be worse for compatability
    //Alternatively, a player tick event could be used for this to roughly guess when a player should add more exhaustion

    @Redirect(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void causeFoodExhaustion(Player instance, float amount) {
        if(this.abilities.invulnerable) {
            return;
        }
        if(this.level.isClientSide) {
            return;
        }
        if(Helper.isHunter(this) && HunterAgeingConfig.hunterAgeing.get()) {
            int age = VampiricAgeingCapabilityManager.getAge(this).map(ageCap -> ageCap.getAge()).orElse(0);
            this.foodData.addExhaustion(amount * HunterAgeingConfig.fasterExhaustionAmounts.get().get(age).floatValue());
        } else {
            this.foodData.addExhaustion(amount);
        }
    }
}
