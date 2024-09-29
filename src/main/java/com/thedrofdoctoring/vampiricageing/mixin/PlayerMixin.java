package com.thedrofdoctoring.vampiricageing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.data.ItemTagProvider;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private Abilities abilities;

    @Shadow protected FoodData foodData;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    //Uses to increase food exhaustion for the hunter player. Doesn't apply to all types of exhaustion, for example healing is still normal. Changing healing exhaustion would likely require a redirect on FoodData#tick which may be worse for compatability
    //Alternatively, a player tick event could be used for this to roughly guess when a player should add more exhaustion

    @Inject(method = "causeFoodExhaustion", at = @At(value = "HEAD"), cancellable = true)
    private void causeFoodExhaustion(float pExhaustion, CallbackInfo ci) {
        if(this.abilities.invulnerable) {
            return;
        }
        if(this.level().isClientSide) {
            return;
        }
        if(Helper.isHunter(this) && HunterAgeingConfig.hunterAgeing.get()) {
            ci.cancel();
            int age = AgeingManager.getAge(((Player) (Object) this)).getAge();
            this.foodData.addExhaustion(pExhaustion * HunterAgeingConfig.fasterExhaustionAmounts.get().get(age).floatValue());
        }
    }
    @ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
    private FoodProperties modifyTaintedFoodProperties(FoodProperties props, @Local(ordinal = 0) ItemStack stack) {
        if(!stack.is(ItemTagProvider.taintedFood) && HunterAgeingConfig.reducedBenefitFromNormalFoods.get()) {
            if(Helper.isHunter(this)) {

                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(((Player) (Object) this));
                int nutritionReduction = HunterAgeingConfig.taintedAgeNutritionReduction.get().get(cumulativeAge);
                float saturationReduction = HunterAgeingConfig.taintedAgeSaturationReduction.get().get(cumulativeAge).floatValue();
                return new FoodProperties(props.nutrition() - nutritionReduction, props.saturation() - saturationReduction, props.canAlwaysEat(), props.eatSeconds(), props.usingConvertsTo(), props.effects());
            }
        }
        return props;
    }
}
