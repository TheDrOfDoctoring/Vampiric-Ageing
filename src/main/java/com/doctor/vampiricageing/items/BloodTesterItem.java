package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class BloodTesterItem extends Item {
    public BloodTesterItem(Properties props) {
        super(props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity source) {
        if(source instanceof PlayerEntity && !source.getCommandSenderWorld().isClientSide &&target.isAlive() && Helper.isVampire(target) && (target instanceof PlayerEntity || (target.getType().is(ModTags.Entities.ADVANCED_VAMPIRE) && CommonConfig.advancedVampireAge.get()))) {
            int age = VampiricAgeingCapabilityManager.getAge(target).map(ageCap -> ageCap.getAge()).orElse(0);
            source.sendMessage(new TranslationTextComponent("text.vampiricageing.vampire_blood_rank", age), Util.NIL_UUID);
        }
        return super.hurtEnemy(stack, target, source);
    }

    @Override
    public void appendHoverText(ItemStack stack, World level, List<ITextComponent> components, ITooltipFlag tf) {
        components.add(new TranslationTextComponent("text.vampiricageing.blood_tester_usage").withStyle(TextFormatting.GRAY));
        super.appendHoverText(stack, level, components, tf);
    }
}
