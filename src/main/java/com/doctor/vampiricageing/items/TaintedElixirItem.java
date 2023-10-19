package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.init.ModEffects;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!HunterAgeingConfig.permanentTransformationAvailable.get()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= 5) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack stack, int count) {
        if(pLivingEntity instanceof IHunterPlayer) return;
        if(!(pLivingEntity instanceof Player) || !pLivingEntity.isAlive() || !HunterAgeingConfig.permanentTransformationAvailable.get()) {
            pLivingEntity.releaseUsingItem();
            return;
        }
        Player player = (Player) pLivingEntity;
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= 5) {
            pLivingEntity.startUsingItem(pLivingEntity.getUsedItemHand());
        }
    }
    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if(entityLiving instanceof Player && Helper.isHunter(entityLiving)) {
            Player player = (Player) entityLiving;
            VampiricAgeingCapabilityManager.getAge(entityLiving).ifPresent(hunter -> {
                hunter.setTransformed(true);
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1, 1);
                ModParticles.spawnParticlesServer(player.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
                VampiricAgeingCapabilityManager.syncAgeCap((Player) entityLiving);
                stack.shrink(1);
            });
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 45;
    }
}