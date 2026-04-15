package com.thedrofdoctoring.vampiricageing.items;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.AgeingRegistry;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class TaintedElixirItem extends Item implements IFactionExclusiveItem {
    public TaintedElixirItem(Properties props) {
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
        if(!HunterAgeingConfig.permanentTransformationAvailable.get()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        int age = AgeingManager.getAge(player).getAge();
        if(age >= 5) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack stack, int count) {
        if(pLivingEntity instanceof IHunterPlayer) return;
        if(!(pLivingEntity instanceof Player player) || !pLivingEntity.isAlive() || !HunterAgeingConfig.permanentTransformationAvailable.get()) {
            pLivingEntity.releaseUsingItem();
            return;
        }
        int age = AgeingManager.getAge(player).getAge();
        if(age >= 5) {
            pLivingEntity.startUsingItem(pLivingEntity.getUsedItemHand());
        }
    }
    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if(entityLiving instanceof Player player && Helper.isHunter(entityLiving)) {

            AgeingManager ageingManager = AgeingManager.getAge(player);
            ((HunterAgeingType.HunterState) ageingManager.getTypeState()).setTransformed(true);

            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1, 1);
            ModParticles.spawnParticlesServer(player.level(), new GenericParticleOptions(ResourceLocation.fromNamespaceAndPath("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
            stack.shrink(1);
            if(entityLiving instanceof ServerPlayer sp) {
                ageingManager.getType().handleSkills(ageingManager.getAge(), sp);
                ageingManager.onAgeChange(sp, ageingManager.getAgeType());
            }

        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull TooltipContext pContext, List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("text.vampiricageing.tainted_elixir_useage", 5).withStyle(ChatFormatting.RED));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, @NotNull LivingEntity p_344979_) {
        return 45;
    }
}