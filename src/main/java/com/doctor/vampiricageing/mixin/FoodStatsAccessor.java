package com.doctor.vampiricageing.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({FoodData.class})
public interface FoodStatsAccessor {
    @Accessor("tickTimer")
    int getFoodTimer();

    @Accessor("tickTimer")
    void setFoodTimer(int var1);
}