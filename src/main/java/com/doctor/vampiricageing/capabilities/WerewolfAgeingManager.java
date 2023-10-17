package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.werewolves.blocks.StoneAltarFireBowlBlock;
import de.teamlapen.werewolves.util.BiteDamageSource;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WerewolfAgeingManager {
    public static boolean shouldIncreaseRankDevoured(PlayerEntity player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getDevoured() >= WerewolvesAgeingConfig.devouredForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static void increasePoints(ServerPlayerEntity player, int points) {
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(age -> {
            age.setDevoured(age.getDevoured() + points);
            VampiricAgeingCapabilityManager.syncAgeCap(player);
            if(shouldIncreaseRankDevoured(player)) {
                VampiricAgeingCapabilityManager.increaseAge(player);
            }
        });
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(WerewolvesAgeingConfig.devourBasedAgeing.get() && event.getSource().getEntity() instanceof ServerPlayerEntity && Helper.isWerewolf(event.getSource().getEntity()) && event.getSource() instanceof BiteDamageSource && VampiricAgeingCapabilityManager.canAge((LivingEntity) event.getSource().getEntity())) {

            int pointWorth;
            if(event.getEntity().getType().is(EntityTypeTagProvider.pettyDevour)) {
                pointWorth = WerewolvesAgeingConfig.pettyDevourWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.commonDevour)) {
                pointWorth = WerewolvesAgeingConfig.commonDevourWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.greaterDevour)) {
                pointWorth = WerewolvesAgeingConfig.greaterDevourWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.exquisiteDevour)) {
                pointWorth = WerewolvesAgeingConfig.exquisiteDevourWorth.get();
            } else {
                pointWorth = 0;
            }
            increasePoints((ServerPlayerEntity) event.getSource().getEntity(), pointWorth);
        }
    }
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (event.getHand() == Hand.MAIN_HAND && Helper.isWerewolf(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof StoneAltarFireBowlBlock && VampiricAgeingCapabilityManager.canAge(player)) {
            int age = VampiricAgeingCapabilityManager.getAge(event.getPlayer()).map(ageCap -> ageCap.getAge()).orElse(0);
            if(WerewolvesAgeingConfig.devourBasedAgeing.get()) {
                int devourPoints = VampiricAgeingCapabilityManager.getAge(event.getPlayer()).map(ageCap -> ageCap.getDevoured()).orElse(0);
                int devouredForNextAge = WerewolvesAgeingConfig.devouredForNextAge.get().get(age) - devourPoints;
                player.sendMessage(new TranslationTextComponent("text.vampiricageing.progress_devour", devouredForNextAge).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
            }
        }
    }
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if(event.getSource() instanceof BiteDamageSource) {
            if(event.getSource().getEntity() instanceof PlayerEntity && Helper.isWerewolf(event.getSource().getEntity()) && !event.getEntity().getCommandSenderWorld().isClientSide) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                player.heal(WerewolvesAgeingConfig.healonBiteAmount.get().get(age));
                if(WerewolvesAgeingConfig.bitingGivesFood.get() && age >= WerewolvesAgeingConfig.rankForBiteFood.get()) {
                    FoodStats foodData = player.getFoodData();
                    foodData.eat(WerewolvesAgeingConfig.biteNutrition.get(), WerewolvesAgeingConfig.biteSaturation.get().floatValue());
                }
            }
        }
    }
    @SubscribeEvent
    public void onFootEatenFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving() instanceof PlayerEntity && Helper.isWerewolf((PlayerEntity) event.getEntity())) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (Helper.isRawMeat(event.getItem())) {
                int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                int multiplier = WerewolvesAgeingConfig.nourishmentMultipliers.get().get(age) - 1;
                for(int i = 1; i <= multiplier; i++) {
                    player.getFoodData().eat(event.getItem().getItem(), event.getItem());
                }
            }
        }
    }
}
