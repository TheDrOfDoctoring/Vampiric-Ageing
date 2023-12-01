package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.data.ItemTagProvider;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            this.foodData.addExhaustion(amount * HunterAgeingConfig.fasterExhaustionAmounts.get().get(age).floatValue());
        } else {
            this.foodData.addExhaustion(amount);
        }
    }
    @Inject(method = "eat", at = @At(value = "HEAD"), cancellable = true)
    private void eat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if(!stack.getItem().is(ItemTagProvider.taintedFood) && HunterAgeingConfig.reducedBenefitFromNormalFoods.get()) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            if(Helper.isHunter(player)) {
                Food food = stack.getItem().getFoodProperties();
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                FoodStats stats = player.getFoodData();
                stats.eat(food.getNutrition() - HunterAgeingConfig.taintedAgeNutritionReduction.get().get(cumulativeAge), food.getSaturationModifier() - HunterAgeingConfig.taintedAgeSaturationReduction.get().get(cumulativeAge).floatValue());
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
                if (player instanceof ServerPlayerEntity) {
                    CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)player, stack
                    );
                }
                cir.setReturnValue(super.eat(world, stack));
            }
        }
    }
}
