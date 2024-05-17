package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.LimitedHunterBatModeAction;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import com.doctor.vampiricageing.mixin.FoodStatsAccessor;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class HunterAgeingManager {



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
            CapabilityHelper.increasePoints(player, pointWorth);
        }
        if(HunterAgeingConfig.permanentTransformationDeathReset.get()) {
            if(Helper.isHunter(event.getEntity()) && event.getEntity() instanceof ServerPlayer) {
                VampiricAgeingCapabilityManager.getAge(event.getEntity()).ifPresent(hunter -> hunter.setTransformed(false));
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if(!(event.getEntity().level().getGameTime() % 20 == 0)) {
            return;
        }
        if(!(event.getEntity() instanceof Player) || event.getEntity().level().isClientSide) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);

        //Faster Regeneration
        if(age >= HunterAgeingConfig.fasterRegenerationAge.get()) {
            Difficulty difficulty = player.level().getDifficulty();
            boolean flag = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
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
                DamageHandler.hurtVanilla(player, DamageSources::starve, 1.0F);
            }
        }
        //Tainted Blood
        if(HunterAgeingConfig.taintedBloodAvailable.get() && age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
            VampiricAgeingCapabilityManager.getAge(player).ifPresent(hunter -> {
                if(hunter.getTemporaryTaintedAgeBonus() > 0 || hunter.isTransformed()) {
                    if(!hunter.isTransformed()) {
                        hunter.setTemporaryTainedTicks(hunter.getTemporaryTainedTicks() - 20);
                        if (hunter.getTemporaryTainedTicks() <= 0) {
                            hunter.setTemporaryTaintedAgeBonus(0);
                        }
                    }
                    int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                    if(HunterAgeingConfig.sunAffectTainted.get() && cumulativeAge >= HunterAgeingConfig.taintedSunAffectAge.get()) {
                        int ticksInSun = hunter.getTicksInSun();
                        if(Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && ticksInSun <= HunterAgeingConfig.maxTicksInSun.get() ) {
                            hunter.setTicksInSun(ticksInSun + 20 * HunterAgeingConfig.taintedAgeSunBadnessMultiplier.get().get(cumulativeAge));
                        } else if(hunter.getTicksInSun() >= 100) {
                            int reductionAmount = hunter.getTicksInSun() < 1000 ? 100 : 1000;
                            hunter.setTicksInSun(Math.max(0, ticksInSun - reductionAmount));
                        }
                        applySunEffects(player, hunter.getTicksInSun());
                    }
                    if(cumulativeAge >= HunterAgeingConfig.underwaterBreathingTaintedAge.get()) {
                        player.setAirSupply(300);
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
            DamageHandler.hurtModded(player, ModDamageSources::sunDamage, 1.5f);
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 2) {
                DamageHandler.hurtModded(player, ModDamageSources::sunDamage, 1.5f);
            }
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 3) {
                DamageHandler.hurtModded(player, ModDamageSources::sunDamage, 2f);
            }
        }
        if(ticksInSun >= HunterAgeingConfig.sunBlindnessTicks.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        }
    }
    @SubscribeEvent
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (event.getHand() == InteractionHand.MAIN_HAND && Helper.isHunter(player) && !player.getCommandSenderWorld().isClientSide) {
            if (player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof MedChairBlock && VampiricAgeingCapabilityManager.canAge(player)) {
                int age = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
                int huntedPoints = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getHunted()).orElse(0);
                int huntedForNextAge = HunterAgeingConfig.huntedForNextAge.get().get(age) - huntedPoints;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_hunted", huntedForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if(event.getItemStack().is(ModItems.INJECTION_GARLIC.get()) && CapabilityHelper.getCumulativeTaintedAge(player) > 0) {
            VampiricAgeingCapabilityManager.getAge(player).ifPresent(hunter -> {
                hunter.setTemporaryTaintedAgeBonus(0);
                hunter.setTemporaryTainedTicks(0);
                hunter.setTransformed(false);
            });
            VampiricAgeingCapabilityManager.syncAgeCap(player);
            event.getItemStack().shrink(1);
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
        Item item = ((Player) sourceEntity).getMainHandItem().getItem();
        Multimap<Attribute, AttributeModifier> attributes = item.getAttributeModifiers(EquipmentSlot.MAINHAND, ((Player) sourceEntity).getMainHandItem());
        //only real weapons get the benefit to prevent dealing tons of damage with just fists. possibly do something similar for other damage bonuses
        if(!attributes.get(Attributes.ATTACK_DAMAGE).isEmpty() && (Helper.isVampire(event.getEntity()) || CapabilityHelper.isWerewolfCheckMod(event.getEntity()))) {
            Player hunterSource = (Player) sourceEntity;
            int age = VampiricAgeingCapabilityManager.getAge(hunterSource).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() + HunterAgeingConfig.ageEnemyFactionDamageIncrease.get().get(age).floatValue());
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

            if(event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE)) {
                event.setAmount(event.getAmount() * HunterAgeingConfig.taintedFireDamageMultiplier.get().get(cumulativeAge).floatValue());
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
        event.setAmount(Math.round((float)event.getAmount() / HunterAgeingConfig.xpGainReduction.get().get(age).floatValue()));
    }
    @SubscribeEvent
    public static void breakSpeed(PlayerEvent.BreakSpeed event) {
        if(!HunterAgeingConfig.hunterIncreasedMiningSpeed.get() || !HunterAgeingConfig.hunterAgeing.get() ) {
            return;
        }
        if(!Helper.isHunter(event.getEntity())) {
            return;
        }
        if(isBat(event.getEntity())) {
            event.setCanceled(true);
        }
        int age = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
        event.setNewSpeed(event.getOriginalSpeed() * HunterAgeingConfig.hunterMiningSpeedBonus.get().get(age).floatValue());

    }
    @SubscribeEvent
    public static void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getInventory() != null /*make sure we are not in the player's contructor*/) {
            if((event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null)) {
                Player player = (Player) event.getEntity();
                if(VampiricAgeingCapabilityManager.getAge(player).map(hunter -> hunter.getBatMode()).orElse(false)) {
                    event.setNewSize(LimitedHunterBatModeAction.BAT_SIZE);
                    event.setNewEyeHeight(LimitedHunterBatModeAction.BAT_EYE_HEIGHT);
                }
            }
        }
    }

    //Limited Bat Mode removals

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.isAlive()) {
            if (isBat(player)) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onTryMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player && isBat((Player) event.getEntity())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
        if (isBat(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemUse(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isBat(player)) {
                event.setCanceled(true);
            }
        }

    }
    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {

        if ((event.getItemStack().getItem() instanceof ThrowablePotionItem || event.getItemStack().getItem() instanceof CrossbowItem)) {
            if (isBat(event.getEntity())) {
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player) || !event.getEntity().isAlive()) return;
        if (event.getPlacedBlock().isAir()) return;
        try {
            if (isBat((Player) event.getEntity())) {
                event.setCanceled(true);
                if (event.getPlacedBlock().hasBlockEntity()) {
                    BlockEntity t = event.getLevel().getBlockEntity(event.getPos());
                    if (t instanceof Container) {
                        ((Container) t).clearContent();
                    }
                }

                if (event.getEntity() instanceof ServerPlayer) { //For some reason this event is only run serverside. Therefore, we have to make sure the client is notified about the not-placed block.
                    MinecraftServer server = event.getEntity().level().getServer();
                    if (server != null) {
                        server.getPlayerList().sendAllPlayerInfo((ServerPlayer) event.getEntity()); //Would probably suffice to just sent a SHeldItemChangePacket
                    }
                }
            }
        } catch (Exception e) {
        }
    }
    public static boolean isBat(Player player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getBatMode()).orElse(false);
    }
}
