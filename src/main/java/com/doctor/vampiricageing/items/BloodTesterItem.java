package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodTesterItem extends Item {
    public BloodTesterItem(Properties props) {
        super(props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity source) {
        if(source instanceof Player player && !source.getCommandSenderWorld().isClientSide &&target.isAlive() && Helper.isVampire(target) && (target.getType() == EntityType.PLAYER || (target.getType().is(ModTags.Entities.ADVANCED_VAMPIRE) && CommonConfig.advancedVampireAge.get()))) {
            int age = VampiricAgeingCapabilityManager.getAge(target).map(ageCap -> ageCap.getAge()).orElse(0);
            player.sendSystemMessage(Component.translatable("text.vampiricageing.vampire_blood_rank", age));
        }
        return super.hurtEnemy(stack, target, source);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tf) {
        components.add(Component.translatable("text.vampiricageing.blood_tester_usage").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, components, tf);
    }
}
