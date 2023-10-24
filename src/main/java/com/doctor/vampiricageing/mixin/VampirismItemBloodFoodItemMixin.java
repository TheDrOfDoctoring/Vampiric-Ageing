package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.VampirismItemBloodFoodItem;
import net.minecraft.world.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VampirismItemBloodFoodItem.class)
public class VampirismItemBloodFoodItemMixin {
    @Shadow @Final private FoodProperties vampireFood;

    @Redirect(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/entity/player/vampire/VampirePlayer;drinkBlood(IF)V"), remap = false)
    private void drinkBlood(VampirePlayer instance, int i, float v) {
        BloodStats stats = (BloodStats) instance.getBloodStats();
        int blood = ((BloodStatsInvoker)stats).increaseBlood(i, v);
    }

    @Inject(method = "finishUsingItem", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if(entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
            if(cumulativeAge > 0 && cumulativeAge >= HunterAgeingConfig.noNegativeEffectsFromBadFoodAge.get()) {
                FoodData foodData = player.getFoodData();
                foodData.eat(this.vampireFood.getNutrition(), this.vampireFood.getSaturationModifier());
                stack.shrink(1);
                cir.setReturnValue(stack);
            }
        }

    }
}
