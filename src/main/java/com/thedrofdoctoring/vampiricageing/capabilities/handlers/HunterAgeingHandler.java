package com.thedrofdoctoring.vampiricageing.capabilities.handlers;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.actions.LimitedHunterBatModeAction;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.mixin.FoodStatsAccessor;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.ModDamageSources;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(modid = VampiricAgeing.MODID)
public class HunterAgeingHandler {

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if(Helper.isHunter(event.getEntity()) && event.getEntity() instanceof ServerPlayer player) {
            AgeingManager age = AgeingManager.getAge(player);
            if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
                if(HunterAgeingConfig.permanentTransformationDeathReset.get()) {
                    state.setTransformed(false);
                }
                state.setTemporaryTaintedAgeBonus(0);
                state.setTemporaryTaintedTicks(0);
                state.setTicksInSun(0);
                age.sync(false);
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if(!(event.getEntity().level().getGameTime() % 20 == 0)) {
            return;
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(!Helper.isHunter(player) || !HunterAgeingConfig.hunterAgeing.get()) {
            return;
        }
        int age = AgeingManager.getAge(player).getAge();

        //Faster Regeneration
        if(age >= HunterAgeingConfig.fasterRegenerationAge.get() && !player.getCommandSenderWorld().isClientSide()) {
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
        if(HunterAgeingConfig.taintedBloodAvailable.get()) {
            AgeingManager manager = AgeingManager.getAge(player);
            if(manager.getTypeState() instanceof HunterAgeingType.HunterState state) {
                if(state.getTemporaryTaintedAgeBonus() > 0 || state.isTransformed()) {
                    if(!state.isTransformed()) {
                        state.setTemporaryTaintedTicks(state.getTemporaryTainedTicks() - 20);
                        if (state.getTemporaryTainedTicks() <= 0) {
                            state.setTemporaryTaintedAgeBonus(0);
                        }
                    }
                    int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                    if(HunterAgeingConfig.sunAffectTainted.get() && cumulativeAge >= HunterAgeingConfig.taintedSunAffectAge.get()) {
                        int ticksInSun = state.getTicksInSun();
                        if(Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && ticksInSun <= HunterAgeingConfig.maxTicksInSun.get() ) {
                            state.setTicksInSun(ticksInSun + 20 * HunterAgeingConfig.taintedAgeSunBadnessMultiplier.get().get(cumulativeAge));
                        } else if(state.getTicksInSun() >= 100) {
                            int reductionAmount = state.getTicksInSun() < 1000 ? 60 : 250;
                            state.setTicksInSun(Math.max(0, ticksInSun - reductionAmount));
                        }
                        applySunEffects(player, state.getTicksInSun());
                    }
                    if(cumulativeAge >= HunterAgeingConfig.underwaterBreathingTaintedAge.get()) {
                        player.setAirSupply(300);
                    }
                    manager.sync(false);
                }
            }

        }

    }
    public static void applySunEffects(Player player, int ticksInSun) {
        //Very simplified sun damage mechanic
        if(player.getAbilities().instabuild || player.hasEffect(ModEffects.SUNSCREEN)) {
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
            float baseDamage = HunterAgeingConfig.baseSunDamageAmount.get().floatValue();
            DamageHandler.hurtModded(player, ModDamageSources::sunDamage, baseDamage);
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 2) {
                DamageHandler.hurtModded(player, ModDamageSources::sunDamage, baseDamage);
            }
            if(ticksInSun >= HunterAgeingConfig.sunDamageTicks.get() * 3) {
                DamageHandler.hurtModded(player, ModDamageSources::sunDamage, baseDamage);
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
            AgeingManager manager = AgeingManager.getAge(player);
            if (player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof MedChairBlock && manager.canAge()) {
                int age = manager.getAge();
                int points = manager.getRankProgress();
                manager.getMethod().displayLevelRequirements(player, points, age);
            }
        }
    }
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if(event.getItemStack().is(ModItems.INJECTION_GARLIC.get()) && CapabilityHelper.getCumulativeTaintedAge(player) > 0) {
            AgeingManager age = AgeingManager.getAge(player);
            if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
                state.setTemporaryTaintedAgeBonus(0);
                state.setTemporaryTaintedTicks(0);
                state.setTransformed(false);
            }

            age.sync(false);
            event.getItemStack().shrink(1);
        }
    }
    @SubscribeEvent
    public static void onDamageByHunter(LivingIncomingDamageEvent event) {
        if(event.getSource().getEntity() == null || event.getEntity().getCommandSenderWorld().isClientSide) {
            return;
        }
        Entity sourceEntity = event.getSource().getEntity();
        if(!Helper.isHunter(sourceEntity) || !HunterAgeingConfig.hunterAgeing.get() || !(sourceEntity instanceof Player)) {
            return;
        }
        ItemStack item = ((Player) sourceEntity).getMainHandItem();
        ItemAttributeModifiers modifiers = item.getAttributeModifiers();
        double baseDamage = 0;
        for (ItemAttributeModifiers.Entry modifier : modifiers.modifiers()) {
            if(modifier.attribute().is(Attributes.ATTACK_DAMAGE)); {
                baseDamage = modifier.modifier().amount();
            }
        }


        if(baseDamage >= 2 && (Helper.isVampire(event.getEntity()) || CapabilityHelper.isWerewolfCheckMod(event.getEntity()))); {
            Player hunterSource = (Player) sourceEntity;
            int age = AgeingManager.getAge(hunterSource).getAge();
            event.setAmount(event.getAmount() + HunterAgeingConfig.ageEnemyFactionDamageIncrease.get().get(age).floatValue());
        }
    }


    @SubscribeEvent
    public static void onDamageHunter(LivingIncomingDamageEvent event) {
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

        int age = AgeingManager.getAge(player).getAge();
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
        int age = AgeingManager.getAge(event.getEntity()).getAge();
        event.setNewSpeed(event.getOriginalSpeed() * HunterAgeingConfig.hunterMiningSpeedBonus.get().get(age).floatValue());

    }
    @SubscribeEvent
    public static void eyeHeight(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player && ((Player) event.getEntity()).getInventory() != null /*make sure we are not in the player's contructor*/) {
            if((event.getEntity().isAlive() && event.getEntity().position().lengthSqr() != 0 && event.getEntity().getVehicle() == null)) {
                Player player = (Player) event.getEntity();
                boolean batMode = isBat(player);
                if(batMode) {
                    event.setNewSize(LimitedHunterBatModeAction.BAT_SIZE);
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
        } catch (Exception ignored) {
        }
    }
    public static boolean isBat(Player player) {
        return ((IHunterSpecialAttributes) HunterPlayer.get(player).getSpecialAttributes()).ageing$getBatMode();
    }
}
