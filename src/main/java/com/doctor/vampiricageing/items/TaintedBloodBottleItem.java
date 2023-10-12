package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.init.ModEffects;
import com.doctor.vampiricageing.init.ModItems;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TaintedBloodBottleItem extends Item implements IFactionExclusiveItem {
    public TaintedBloodBottleItem(Properties props) {
        super(props);
    }

    @Override
    public @Nullable IFaction<?> getExclusiveFaction(@NotNull ItemStack itemStack) {
        return VReference.HUNTER_FACTION;
    }
    @NotNull
    @Override
    public UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!HunterAgeingConfig.taintedBloodAvailable.get()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack stack, int count) {
        if(pLivingEntity instanceof IHunterPlayer) return;
        if(!(pLivingEntity instanceof Player) || !pLivingEntity.isAlive() || !HunterAgeingConfig.taintedBloodAvailable.get()) {
            pLivingEntity.releaseUsingItem();
            return;
        }
        Player player = (Player) pLivingEntity;
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            pLivingEntity.startUsingItem(pLivingEntity.getUsedItemHand());
        }
    }
    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if(entityLiving instanceof Player && Helper.isHunter(entityLiving)) {
            int age = stack.getDamageValue();
            VampiricAgeingCapabilityManager.getAge(entityLiving).ifPresent(hunter -> {
                hunter.setTemporaryTainedTicks(HunterAgeingConfig.temporaryTaintedBloodBaseTicks.get() * hunter.getAge());
                hunter.setTemporaryTaintedAgeBonus(age);
                entityLiving.addEffect(new MobEffectInstance(ModEffects.TAINTED_BLOOD_EFFECT.get(),HunterAgeingConfig.temporaryTaintedBloodBaseTicks.get() * hunter.getAge(), 0, false, false));
                VampiricAgeingCapabilityManager.syncAgeCap((Player) entityLiving);
                stack.shrink(1);
            });
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 45;
    }
    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tf) {
        components.add(Component.translatable("text.vampiricageing.tainted_blood_useage", HunterAgeingConfig.taintedBloodBottleAge.get()).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("text.vampiricageing.tainted_blood_rank", stack.getDamageValue()).withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, level, components, tf);
    }
}
