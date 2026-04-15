package com.thedrofdoctoring.vampiricageing.items;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.init.ModEffects;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!HunterAgeingConfig.taintedBloodAvailable.get()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        AgeingManager age = AgeingManager.getAge(player);
        if(age.getAge() >= HunterAgeingConfig.taintedBloodBottleAge.get() && !((HunterAgeingType.HunterState) age.getTypeState()).isTransformed()) {
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
        int age = AgeingManager.getAge(player).getAge();
        if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            pLivingEntity.startUsingItem(pLivingEntity.getUsedItemHand());
        }
    }
    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if(entityLiving instanceof Player player && Helper.isHunter(entityLiving)) {
            int age = stack.getDamageValue();
            AgeingManager hunter = AgeingManager.getAge(player);
            HunterAgeingType.HunterState state = (HunterAgeingType.HunterState) hunter.getTypeState();
            state.setTemporaryTaintedTicks(HunterAgeingConfig.temporaryTaintedBloodBaseTicks.get() * hunter.getAge());
            state.setTemporaryTaintedAgeBonus(age);
            entityLiving.addEffect(new MobEffectInstance(ModEffects.TAINTED_BLOOD_EFFECT,HunterAgeingConfig.temporaryTaintedBloodBaseTicks.get() * hunter.getAge(), 0, false, false));
            stack.shrink(1);
            if(entityLiving instanceof ServerPlayer sp) {
                hunter.getType().handleSkills(hunter.getAge(), sp);
                hunter.onAgeChange(sp, hunter.getAgeType());
            }
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity entity) {
        return 45;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @NotNull TooltipContext pContext, List<Component> components, @NotNull TooltipFlag pTooltipFlag) {
        components.add(Component.translatable("text.vampiricageing.tainted_blood_useage", HunterAgeingConfig.taintedBloodBottleAge.get()).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("text.vampiricageing.tainted_blood_rank", pStack.getDamageValue()).withStyle(ChatFormatting.RED));
        super.appendHoverText(pStack, pContext, components, pTooltipFlag);
    }
}
