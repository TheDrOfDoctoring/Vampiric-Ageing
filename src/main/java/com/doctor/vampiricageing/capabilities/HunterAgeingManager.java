package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import com.doctor.vampiricageing.mixin.FoodStatsAccessor;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class HunterAgeingManager {

    public static boolean shouldIncreaseRankHunted(PlayerEntity player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getHunted() >= HunterAgeingConfig.huntedForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static void increasePoints(ServerPlayerEntity player, int points) {
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(age -> {
            age.setHunted(age.getHunted() + points);
            VampiricAgeingCapabilityManager.syncAgeCap(player);
            if(shouldIncreaseRankHunted(player)) {
                VampiricAgeingCapabilityManager.increaseAge(player);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if(event.getSource().getEntity() instanceof ServerPlayerEntity && Helper.isHunter(event.getSource().getEntity()) && VampiricAgeingCapabilityManager.canAge((LivingEntity) event.getSource().getEntity())) {
            int pointWorth;
            if(event.getEntity().getType().is(EntityTypeTagProvider.pettyHunt)) {
                pointWorth = HunterAgeingConfig.pettyHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.commonHunt)) {
                pointWorth = HunterAgeingConfig.commonHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.greaterHunt)) {
                pointWorth = HunterAgeingConfig.pettyHuntWorth.get();
            } else {
                return;
            }
            increasePoints((ServerPlayerEntity) event.getSource().getEntity(), pointWorth);
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(!(event.player.level.getGameTime() % 20 == 0)) {
            return;
        }
        if(event.player.level.isClientSide) {
            return;
        }
        PlayerEntity player = event.player;
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        if(age >= HunterAgeingConfig.fasterRegenerationAge.get()) {
            Difficulty difficulty = player.level.getDifficulty();
            boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
            FoodStats stats = player.getFoodData();
            if (flag && stats.getSaturationLevel() > 0.0F && player.isHurt() && stats.getFoodLevel() >= 20) {
                if (((FoodStatsAccessor)stats).getFoodTimer() >= 9) {
                    float f = Math.min(stats.getSaturationLevel(), 6.0F);
                    player.heal(f / 6.0F);
                    stats.addExhaustion(f);
                }
            } else if (flag && stats.getFoodLevel() >= 18 && player.isHurt()) {
                if (((FoodStatsAccessor)stats).getFoodTimer() >= 79) {
                    player.heal(1.0F);
                    stats.addExhaustion(6.0F);
                }
            } else if (stats.getFoodLevel() <= 0 && ((FoodStatsAccessor)stats).getFoodTimer() >= 79 && (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)) {
                player.hurt(DamageSource.STARVE, 1.0F);
            }
        }

    }
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        if (Helper.isHunter(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof MedChairBlock && VampiricAgeingCapabilityManager.canAge(player)) {
            int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                int huntedPoints = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getHunted()).orElse(0);
                int huntedForNextAge = HunterAgeingConfig.huntedForNextAge.get().get(age) - huntedPoints;
                player.sendMessage(new TranslationTextComponent("text.vampiricageing.progress_hunted", huntedForNextAge).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
        }
    }
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if(event.getSource().getEntity() == null) {
            return;
        }
        Entity sourceEntity = event.getSource().getEntity();
        if(!Helper.isHunter(sourceEntity) || !HunterAgeingConfig.hunterAgeing.get() || !(sourceEntity instanceof PlayerEntity)) {
            return;
        }
        if(Helper.isVampire(event.getEntity()) || CapabilityHelper.isWerewolfCheckMod(event.getEntity())) {
            PlayerEntity hunterSource = (PlayerEntity) sourceEntity;
            int age = VampiricAgeingCapabilityManager.getAge(hunterSource).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() + HunterAgeingConfig.ageEnemyFactionDamageIncrease.get().get(age));

        }
    }
    @SubscribeEvent
    public static void onXpGain(PlayerXpEvent.XpChange event) {
        PlayerEntity player = event.getPlayer();
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get() ) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        event.setAmount(Math.round((float)event.getAmount() / HunterAgeingConfig.xpGainReduction.get().get(age)));
    }
    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        if(!HunterAgeingConfig.hunterIncreasedMiningSpeed.get() && !HunterAgeingConfig.hunterAgeing.get() ) {
            return;
        }
        if(!Helper.isHunter(event.getEntity())) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(event.getPlayer()).map(ageCap -> ageCap.getAge()).orElse(0);
        event.setNewSpeed(event.getOriginalSpeed() + HunterAgeingConfig.hunterMiningSpeedBonus.get().get(age));

    }
}
