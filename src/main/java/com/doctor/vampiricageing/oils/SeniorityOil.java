package com.doctor.vampiricageing.oils;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.items.oil.WeaponOil;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SeniorityOil extends WeaponOil {

    public SeniorityOil(int color, int maxDuration) {
        super(color, maxDuration);
    }



    @Override
    public float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        if(!Helper.isHunter(source)) {
            return amount;
        }
        int sourceAge = VampiricAgeingCapabilityManager.getAge(source).map(ageCap -> ageCap.getAge()).orElse(0);
        if(sourceAge < HunterAgeingConfig.seniorityOilUseAge.get()) {
            return amount;
        }
        if(Helper.isVampire(target) || CapabilityHelper.isWerewolfCheckMod(target)) {
            int targetAge = VampiricAgeingCapabilityManager.getAge(target).map(ageCap -> ageCap.getAge()).orElse(0);
            float bonusDamage = HunterAgeingConfig.seniorityOilDamageBonus.get().get(targetAge);
            return amount + bonusDamage;
        }
        return amount;
    }

    @Override
    public void getDescription(ItemStack stack, List<Component> tooltips) {
        super.getDescription(stack, tooltips);
        tooltips.add(Component.translatable("text.vampiricageing.useable_by", HunterAgeingConfig.seniorityOilUseAge.get()).withStyle(ChatFormatting.DARK_RED));
        tooltips.add(Component.translatable("text.vampiricageing.seniority_oil.when_applied").withStyle(ChatFormatting.GRAY));
        tooltips.add(Component.literal("  ").append(Component.translatable("text.vampiricageing.oil.seniority_oil.more_damage").withStyle(ChatFormatting.DARK_GREEN)));
    }

}
