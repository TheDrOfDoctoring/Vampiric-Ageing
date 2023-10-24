package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.items.VampirismItemBloodFood;
import de.teamlapen.vampirism.player.vampire.BloodStats;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VampirismItemBloodFood.class)
public class VampirismItemBloodFoodItemMixin {
    @Shadow @Final private Food vampireFood;

    @Redirect(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/player/vampire/VampirePlayer;drinkBlood(IF)V"), remap = false)
    private void drinkBlood(VampirePlayer instance, int i, float v) {
        BloodStats stats = (BloodStats) instance.getBloodStats();
        int blood = ((BloodStatsInvoker)stats).increaseBlood(i, v);
    }
    @Inject(method = "finishUsingItem", at = @At(value = "HEAD"), remap = false, cancellable = true)
    private void finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if(entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
            if(cumulativeAge > 0 && cumulativeAge >= HunterAgeingConfig.noNegativeEffectsFromBadFoodAge.get()) {
                FoodStats foodData = player.getFoodData();
                foodData.eat(this.vampireFood.getNutrition(), this.vampireFood.getSaturationModifier());
                stack.shrink(1);
                cir.setReturnValue(stack);
            }
        }

    }
}