package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.data.ItemTagProvider;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow
    public abstract void eat(int p_38708_, float p_38709_);

    @Inject(method = "eat(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), remap = false, cancellable = true)
    private void eat(Item item, ItemStack stack, LivingEntity entity, CallbackInfo ci) {
        if(!stack.is(ItemTagProvider.taintedFood) && HunterAgeingConfig.reducedBenefitFromNormalFoods.get()) {
            if(Helper.isHunter(entity) && entity instanceof Player) {
                FoodProperties foodproperties = stack.getFoodProperties(entity);
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge((Player) entity);
                this.eat(foodproperties.getNutrition() - HunterAgeingConfig.taintedAgeNutritionReduction.get().get(cumulativeAge), foodproperties.getSaturationModifier() - HunterAgeingConfig.taintedAgeSaturationReduction.get().get(cumulativeAge).floatValue());
                ci.cancel();
            }
        }
    }
}