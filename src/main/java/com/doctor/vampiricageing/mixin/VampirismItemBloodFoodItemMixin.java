package com.doctor.vampiricageing.mixin;

import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.VampirismItemBloodFoodItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VampirismItemBloodFoodItem.class)
public class VampirismItemBloodFoodItemMixin {
    @Redirect(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lde/teamlapen/vampirism/entity/player/vampire/VampirePlayer;drinkBlood(IF)V"), remap = false)
    private void drinkBlood(VampirePlayer instance, int i, float v) {
        BloodStats stats = (BloodStats) instance.getBloodStats();
        int blood = ((BloodStatsInvoker)stats).increaseBlood(i, v);
    }
}
