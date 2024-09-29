package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.werewolves.items.oil.SilverOil;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SilverOil.class)
public class SilverOilMixin {

    @Inject(method = "onDamage", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source, CallbackInfoReturnable<Float> cir) {
        if(Helper.isWerewolf(target) && (target instanceof PathfinderMob || target instanceof Player)) {

            int age = AgeingManager.getAge(target).map(AgeingManager::getAge).orElse(0);
            cir.setReturnValue(cir.getReturnValue() * WerewolvesAgeingConfig.silverOilDamageMultiplier.get().get(age).floatValue());
        }
    }
}
