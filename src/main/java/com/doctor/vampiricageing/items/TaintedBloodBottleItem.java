package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.init.ModItems;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TaintedBloodBottleItem extends Item implements IFactionExclusiveItem {
    public TaintedBloodBottleItem(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }
    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!HunterAgeingConfig.taintedBloodAvailable.get()) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age > HunterAgeingConfig.taintedBloodBottleAge.get()) {
            player.startUsingItem(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if(player instanceof IHunterPlayer) return;
        if(!player.isAlive() || !HunterAgeingConfig.taintedBloodAvailable.get()) {
            player.releaseUsingItem();
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age > HunterAgeingConfig.taintedBloodBottleAge.get()) {
            player.startUsingItem(player.getUsedItemHand());
        }
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if(entityLiving instanceof PlayerEntity && Helper.isHunter(entityLiving)) {
            int age = stack.getDamageValue();
            VampiricAgeingCapabilityManager.getAge(entityLiving).ifPresent(hunter -> {
                hunter.setTemporaryTainedTicks(HunterAgeingConfig.temporaryTaintedBloodBaseTicks.get() * hunter.getAge());
                hunter.setTemporaryTaintedAgeBonus(age);
                VampiricAgeingCapabilityManager.syncAgeCap((PlayerEntity) entityLiving);
                //stack.shrink(1);
            });
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    public int getUseDuration(ItemStack stack) {
        return 45;
    }
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> components, ITooltipFlag tf) {
        components.add(new TranslationTextComponent("text.vampiricageing.tainted_blood_useage", HunterAgeingConfig.taintedBloodBottleAge.get()).withStyle(TextFormatting.GRAY));
        components.add(new TranslationTextComponent("text.vampiricageing.tainted_blood_rank", stack.getDamageValue()).withStyle(TextFormatting.RED));
        super.appendHoverText(stack, world, components, tf);
    }
}
