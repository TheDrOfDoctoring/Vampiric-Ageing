package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import com.doctor.vampiricageing.mixin.FoodStatsAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class HunterAgeingManager {

    public static boolean shouldIncreaseRankHunted(Player player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getHunted() >= HunterAgeingConfig.huntedForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static void increasePoints(ServerPlayer player, int points) {
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
        if(event.getSource().getEntity() instanceof ServerPlayer player && Helper.isHunter(player) && VampiricAgeingCapabilityManager.canAge(player)) {
            int pointWorth;
            if(event.getEntity().getType().is(EntityTypeTagProvider.pettyHunt)) {
                pointWorth = HunterAgeingConfig.pettyHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.commonHunt)) {
                pointWorth = HunterAgeingConfig.commonHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.greaterHunt)) {
                pointWorth = HunterAgeingConfig.greaterHuntWorth.get();
            } else {
                return;
            }
            increasePoints(player, pointWorth);
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if(!(event.getEntity().level.getGameTime() % 20 == 0)) {
            return;
        }
        if(!(event.getEntity() instanceof Player) || event.getEntity().level.isClientSide) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);

        //Faster Regeneration
        if(age >= HunterAgeingConfig.fasterRegenerationAge.get()) {
            Difficulty difficulty = player.level.getDifficulty();
            boolean flag = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
            FoodData stats = player.getFoodData();
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
        //Tainted Blood
        if(HunterAgeingConfig.taintedBloodAvailable.get() && age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            VampiricAgeingCapabilityManager.getAge(player).ifPresent(hunter -> {
                if(hunter.getTemporaryTaintedAgeBonus() > 0) {
                    hunter.setTemporaryTainedTicks(hunter.getTemporaryTainedTicks() - 20);
                    if(hunter.getTemporaryTainedTicks() <= 0) {
                        hunter.setTemporaryTaintedAgeBonus(0);
                    }
                    int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                    if(HunterAgeingConfig.sunAffectTainted.get() && cumulativeAge >= HunterAgeingConfig.taintedSunAffectAge.get()) {
                        int ticksInSun = hunter.getTicksInSun();
                        if(Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && ticksInSun <= HunterAgeingConfig.maxTicksInSun.get() ) {
                            hunter.setTicksInSun(ticksInSun + 20 * HunterAgeingConfig.taintedAgeSunBadnessMultiplier.get().get(cumulativeAge));
                        } else if(hunter.getTicksInSun() >= 20) {
                            int reductionAmount = hunter.getTicksInSun() < 400 ? 20 : 400;
                            hunter.setTicksInSun(ticksInSun - reductionAmount);
                        }
                        applySunEffects(player, hunter.getTicksInSun());
                    }
                    VampiricAgeingCapabilityManager.syncAgeCap(player);
                }
            });
        }

    }
    public static void applySunEffects(Player player, int ticksInSun) {
        //Very simplified sun damage mechanic
        if(player.getAbilities().instabuild || player.hasEffect(ModEffects.SUNSCREEN.get())) {
            return;
        }

        if(ticksInSun >= HunterAgeingConfig.sunWeaknessTicks.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
        }
        if(ticksInSun >= HunterAgeingConfig.sunSlownessTicks.get() && ticksInSun < HunterAgeingConfig.sunSlownessThreeTicks.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        } else if(ticksInSun >= HunterAgeingConfig.sunSlownessThreeTicks.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2));
        }
        if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get()) {
            player.hurt(VReference.SUNDAMAGE, 1.5f);
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 2) {
                player.hurt(VReference.SUNDAMAGE, 1.5f);
            }
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 3) {
                player.hurt(VReference.SUNDAMAGE, 2f);
            }
        }
        if(ticksInSun >= HunterAgeingConfig.sunBlindnessTicks.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        }
    }
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if (event.getHand() == InteractionHand.MAIN_HAND && Helper.isHunter(player) && !player.getCommandSenderWorld().isClientSide) {
            if(player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof MedChairBlock && VampiricAgeingCapabilityManager.canAge(player)) {
                int age = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
                int huntedPoints = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getHunted()).orElse(0);
                int huntedForNextAge = HunterAgeingConfig.huntedForNextAge.get().get(age) - huntedPoints;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_hunted", huntedForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
            else if(event.getItemStack().is(ModItems.INJECTION_GARLIC.get()) && CapabilityHelper.getCumulativeTaintedAge(player) > 0) {
                VampiricAgeingCapabilityManager.getAge(player).ifPresent(hunter -> {
                    hunter.setTemporaryTaintedAgeBonus(0);
                    hunter.setTemporaryTainedTicks(0);
                });
                VampiricAgeingCapabilityManager.syncAgeCap(player);
                event.getItemStack().shrink(1);
            }
        }
    }
    @SubscribeEvent
    public static void onDamageByHunter(LivingDamageEvent event) {
        if(event.getSource().getEntity() == null || event.getEntity().getCommandSenderWorld().isClientSide) {
            return;
        }
        Entity sourceEntity = event.getSource().getEntity();
        if(!Helper.isHunter(sourceEntity) || !HunterAgeingConfig.hunterAgeing.get() || !(sourceEntity instanceof Player)) {
            return;
        }
        if(Helper.isVampire(event.getEntity()) || CapabilityHelper.isWerewolfCheckMod(event.getEntity())) {
            Player hunterSource = (Player) sourceEntity;
            int age = VampiricAgeingCapabilityManager.getAge(hunterSource).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() + HunterAgeingConfig.ageEnemyFactionDamageIncrease.get().get(age));

        }
    }

    @SubscribeEvent
    public static void onDamageHunter(LivingDamageEvent event) {
        if(!Helper.isHunter(event.getEntity()) || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(CapabilityHelper.getCumulativeTaintedAge(player) > 0 && !player.getCommandSenderWorld().isClientSide) {
            int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);

            if(event.getSource().isFire()) {
                event.setAmount(event.getAmount() * HunterAgeingConfig.taintedFireDamageMultiplier.get().get(cumulativeAge));
            }
        }

    }
    @SubscribeEvent
    public static void onXpGain(PlayerXpEvent.XpChange event) {
        Player player = event.getEntity();
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get() ) {
            return;
        }

        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        event.setAmount(Math.round((float)event.getAmount() / HunterAgeingConfig.xpGainReduction.get().get(age)));
    }
    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        if(!HunterAgeingConfig.hunterIncreasedMiningSpeed.get() || !HunterAgeingConfig.hunterAgeing.get() ) {
            return;
        }
        if(!Helper.isHunter(event.getEntity())) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
        event.setNewSpeed(event.getOriginalSpeed() * HunterAgeingConfig.hunterMiningSpeedBonus.get().get(age));

    }
    @SubscribeEvent
    public static void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getInventory() != null /*make sure we are not in the player's contructor*/) {
            if((event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null)) {
                Player player = (Player) event.getEntity();
                if(Helper.isHunter(event.getEntity()) && VampiricAgeingCapabilityManager.getAge(player).map(hunter -> hunter.getBatMode()).orElse(false)) {
                    event.setNewSize(EntityDimensions.fixed(0.6f, 0.95f));
                    event.setNewEyeHeight(0.725f);
                }
            }
        }
    }
}
