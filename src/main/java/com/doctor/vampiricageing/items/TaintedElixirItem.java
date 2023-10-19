package com.doctor.vampiricageing.items;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TaintedElixirItem extends Item implements IFactionExclusiveItem {
    public TaintedElixirItem(Properties props) {
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
        if(!HunterAgeingConfig.permanentTransformationAvailable.get()) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        if(!Helper.isHunter(player)) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= 5) {
            player.startUsingItem(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if(player instanceof IHunterPlayer) return;
        if(!player.isAlive() || !HunterAgeingConfig.permanentTransformationAvailable.get()) {
            player.releaseUsingItem();
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= 5) {
            player.startUsingItem(player.getUsedItemHand());
        }
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if(entityLiving instanceof PlayerEntity && Helper.isHunter(entityLiving)) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            VampiricAgeingCapabilityManager.getAge(entityLiving).ifPresent(hunter -> {
                hunter.setTransformed(true);
                player.addEffect(new EffectInstance(Effects.BLINDNESS, 60, 1));
                player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, 1));
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1, 1);
                ModParticles.spawnParticlesServer(player.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
                VampiricAgeingCapabilityManager.syncAgeCap((PlayerEntity) entityLiving);
                stack.shrink(1);
            });
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }

    public int getUseDuration(ItemStack stack) {
        return 45;
    }


}
