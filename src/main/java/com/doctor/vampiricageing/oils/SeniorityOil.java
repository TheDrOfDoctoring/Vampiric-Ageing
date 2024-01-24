package com.doctor.vampiricageing.oils;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.items.oil.WeaponOil;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class SeniorityOil extends WeaponOil {

    public SeniorityOil(int color, int maxDuration) {
        super(color, maxDuration);
    }



    @Override
    public float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        float bonusDamage = 0;
        if(!Helper.isHunter(source)) {
            return bonusDamage;
        }
        int sourceAge = VampiricAgeingCapabilityManager.getAge(source).map(ageCap -> ageCap.getAge()).orElse(0);
        if(sourceAge < HunterAgeingConfig.seniorityOilUseAge.get()) {
            return bonusDamage;
        }
        if(Helper.isVampire(target) || CapabilityHelper.isWerewolfCheckMod(target)) {
            int targetAge = VampiricAgeingCapabilityManager.getAge(target).map(ageCap -> ageCap.getAge()).orElse(0);
            bonusDamage = HunterAgeingConfig.seniorityOilDamageBonus.get().get(targetAge).floatValue();
            return amount * bonusDamage;
        }
        return bonusDamage;
    }

    @Override
    public void getDescription(ItemStack stack, List<ITextComponent> tooltips) {
        super.getDescription(stack, tooltips);
        tooltips.add(new TranslationTextComponent("text.vampiricageing.useable_by", HunterAgeingConfig.seniorityOilUseAge.get()).withStyle(TextFormatting.DARK_RED));
        tooltips.add(new TranslationTextComponent("text.vampiricageing.seniority_oil.when_applied").withStyle(TextFormatting.GRAY));
        tooltips.add(new StringTextComponent("  ").append(new TranslationTextComponent("text.vampiricageing.oil.seniority_oil.more_damage").withStyle(TextFormatting.DARK_GREEN)));
    }

}
