package com.thedrofdoctoring.vampiricageing.oils;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.items.oil.WeaponOil;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SeniorityOil extends WeaponOil {

    public SeniorityOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public int getMaxDuration(ItemStack stack) {
        return HunterAgeingConfig.seniorityOilUses.get();
    }

    @Override
    public float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        float bonusDamage = 0;
        if(!Helper.isHunter(source)) {
            return bonusDamage;
        }

        int sourceAge = AgeingManager.getAge(source).map(AgeingManager::getAge).orElse(0);
        if(sourceAge < HunterAgeingConfig.seniorityOilUseAge.get()) {
            return bonusDamage;
        }

        if(Helper.isVampire(target) || CapabilityHelper.isWerewolfCheckMod(target)) {
            int targetAge = AgeingManager.getAge(target).map(AgeingManager::getAge).orElse(0);
            bonusDamage = HunterAgeingConfig.seniorityOilDamageBonus.get().get(targetAge).floatValue();
            return amount * bonusDamage;
        }
        return bonusDamage;
    }

    @Override
    public void getDescription(ItemStack stack, Item.TooltipContext context, List<Component> tooltips) {
        super.getDescription(stack, context, tooltips);
        tooltips.add(Component.translatable("text.vampiricageing.useable_by", HunterAgeingConfig.seniorityOilUseAge.get()).withStyle(ChatFormatting.DARK_RED));
        tooltips.add(Component.translatable("text.vampiricageing.seniority_oil.when_applied").withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.literal("  ").append(Component.translatable("text.vampiricageing.oil.seniority_oil.more_damage").withStyle(ChatFormatting.DARK_GREEN)));
    }


}
