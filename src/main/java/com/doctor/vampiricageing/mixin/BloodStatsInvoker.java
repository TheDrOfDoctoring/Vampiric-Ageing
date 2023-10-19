package com.doctor.vampiricageing.mixin;

import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BloodStats.class)
public interface BloodStatsInvoker {
    @Invoker("addBlood")
    int increaseBlood(int amount, float saturationModifier);
}
