package com.thedrofdoctoring.vampiricageing.capabilities;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.actions.VampiricAgeingActions;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.AgeingRegistry;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.DrinkBloodMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.HuntingMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.TimeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.HunterAgeingType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.data.AgeingDataComponents;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.fluids.BloodHelper;
import de.teamlapen.vampirism.items.component.BottleBlood;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;

import static com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager.removeModifier;

@EventBusSubscriber(modid = VampiricAgeing.MODID)
public class AgeingEventHandler {


    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            AgeingManager manager = AgeingManager.getAge(player);
            if (manager.getMethod() instanceof HuntingMethod method) {
                method.onAgedKill(event.getEntity(), player);
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (source instanceof Player player && Helper.isVampire(player)) {
            VampirePlayer vamp = VampirePlayer.get(player);
            if (vamp.getActionHandler().isActionActive(VampiricAgeingActions.DRAIN_BLOOD_ACTION.get())) {
                IVampirePlayer.BITE_TYPE biteType = vamp.determineBiteType(target);
                int blood = 0;
                float saturationMod = 1.0F;

                if(biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD) {

                    IBiteableEntity entity = (IBiteableEntity) target;
                    blood = entity.onBite(vamp);
                }

                if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_PLAYER) {
                    VampirePlayer v = VampirePlayer.get((Player) target);

                    blood = v.onBite(vamp);
                    saturationMod = v.getBloodSaturation();
                } else if (biteType == IVampirePlayer.BITE_TYPE.HUNTER_CREATURE && target instanceof Player targetPlayer) {
                    targetPlayer.getFoodData().addExhaustion(1f);
                    blood = 1;
                    saturationMod = 0.1f;
                } else if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_CREATURE) {
                    Optional<ExtendedCreature> opt = ExtendedCreature.getSafe(target);
                    blood = opt.map((creature) -> {
                        return creature.onBite(vamp);
                    }).orElse(0);
                    saturationMod = opt.map(IBiteableEntity::getBloodSaturation).orElse(0.0F);
                }

                DrinkBloodContext context = new DrinkBloodContext(target);
                vamp.drinkBlood(blood, saturationMod, context);
                VampirismEventFactory.fireVampirePlayerDrinkBloodEvent(vamp, blood, saturationMod, true, context);
            }
        }
    }
    @SubscribeEvent
    public static void onCoffinInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if(Helper.isVampire(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof CoffinBlock && AgeingManager.getAge(player).canAge()) {
            AgeingManager age = AgeingManager.getAge(player);
            int points = age.getRankProgress();
            age.getMethod().displayLevelRequirements(player, points, age.getAge());

        }
    }

    @SubscribeEvent
    public static void vampireDrinkBlood(BloodDrinkEvent.PlayerDrinkBloodEvent event) {
        if(event.getBloodSource().getEntity().isPresent()) {
            Player player = (Player) event.getVampire().getRepresentingEntity();
            AgeingManager age = AgeingManager.getAge(player);
            if(age.getMethod() instanceof DrinkBloodMethod && !player.getCommandSenderWorld().isClientSide) {
                if(age.canAge()) {
                    age.increaseRankPoints(event.getAmount());
                }
            }
        }
    }
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof AdvancedVampireEntity vamp && CommonConfig.advancedVampireAge.get() && !event.getEntity().getCommandSenderWorld().isClientSide) {
            AgeingManager manager = AgeingManager.getAge(vamp);
            if(manager.getAge() != 0) {
                return;
            }
            List<? extends Double> percentages = CommonConfig.percentageAdvancedVampireAges.get();
            double random = vamp.getRandom().nextDouble();
            double cumulative = 0;
            manager.setType(AgeingReference.VAMP);

            for (int i = 0; i < percentages.size(); i++) {
                cumulative += percentages.get(i);
                if (random <= cumulative) {
                    manager.setAge(i);
                    break;
                }
            }
            float ageMultiplier = Math.min(1, (float) manager.getAge() / 2);

            ResourceLocation MAX_HEALTH = VampiricAgeing.rl("adv_vamp_age_health");
            ResourceLocation ATTACK_DAMAGE = VampiricAgeing.rl("adv_vamp_age_damage");
            ResourceLocation KNOCKBACK_RESISTANCE = VampiricAgeing.rl("adv_vamp_age_resistance");
            ResourceLocation SPEED = VampiricAgeing.rl("adv_vamp_age_speed");

            removeModifier(vamp.getAttribute(Attributes.MAX_HEALTH), MAX_HEALTH);
            removeModifier(vamp.getAttribute(Attributes.ATTACK_DAMAGE), ATTACK_DAMAGE);
            removeModifier(vamp.getAttribute(Attributes.KNOCKBACK_RESISTANCE), KNOCKBACK_RESISTANCE);
            removeModifier(vamp.getAttribute(Attributes.MOVEMENT_SPEED), SPEED);
            vamp.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH,  ageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            vamp.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE,  ageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            vamp.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier(KNOCKBACK_RESISTANCE,  0.25 * ageMultiplier, AttributeModifier.Operation.ADD_VALUE));
            vamp.setHealth(vamp.getMaxHealth());
            if(manager.getAge() > 2) {
                vamp.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SPEED, 0.2 * ageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    @SubscribeEvent
    public static void handleAgeMethod(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            AgeingManager manager = AgeingManager.getAge(player);
            IAgeType oldType = manager.getType();
            CapabilityHelper.setDefaultAgeTypeAndMethod(player);
            manager.onAgeChange(player, oldType);
            manager.sync(true);
        }
    }

    @SubscribeEvent
    public static void onPotionEffectRemove(MobEffectEvent.Remove event) {
        if(CommonConfig.sireingMechanic.get() && event.getEffect() == ModEffects.SANGUINARE.get() && event.getEntity() instanceof Player player && !event.getEntity().getCommandSenderWorld().isClientSide) {
            player.getPersistentData().remove("AGE");
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        if(event.getEntity() instanceof Player player && Helper.isVampire(event.getEntity())) {
            int age = AgeingManager.getAge(player).getAge();

            if(event.getSource().is(ModDamageTypes.SUN_DAMAGE)) {
                event.setAmount(event.getAmount() / CommonConfig.sunDamageReduction.get().get(age).floatValue());
            } else if(event.getSource().is(ModDamageTypes.VAMPIRE_IN_FIRE) || event.getSource().is(ModDamageTypes.VAMPIRE_ON_FIRE)  || event.getSource().is(ModDamageTypes.HOLY_WATER) ) {
                if(CommonConfig.rageModeWeaknessToggle.get() && VampirePlayer.getOpt(player).map(vamp -> vamp.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())).orElse(false) && CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue() < 1) {
                    return;
                }
                if(!event.getSource().is(ModDamageTypes.HOLY_WATER) && CommonConfig.deadlySourcesFastDrainExhaustion.get() && event.getEntity() instanceof Player) {
                    VampirePlayer.getOpt(player).ifPresent(vamp -> {
                        vamp.addExhaustion(CommonConfig.amountExhaustionDrainFromSources.get().get(age).floatValue());
                    });
                }
                event.setAmount(event.getAmount() / CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue());
            } else if(event.getSource().is(DamageTypes.STARVE) && CommonConfig.harsherOutOfBlood.get() && age > 0) {
                event.setAmount(event.getAmount() * age);
            } else if(event.getSource().getEntity() != null && event.getSource().getEntity().getType().is(ModTags.Entities.HUNTER) && CommonConfig.shouldAgeIncreaseHunterMobDamage.get()) {
                event.setAmount(event.getAmount() * CommonConfig.damageMultiplierFromHunters.get().get(age).floatValue());
            }

        }
        if(event.getSource().getEntity() instanceof AdvancedVampireEntity vamp && CommonConfig.sireingMechanic.get() && event.getEntity() instanceof ServerPlayer player && Helper.canBecomeVampire(player)) {
            AgeingManager age = AgeingManager.getAge(vamp);
            if (age.getAge() > 1 && vamp.getRandom().nextFloat() > 0.85) {
                SanguinareEffect.addRandom(player, true);
                player.getPersistentData().remove("AGE");
                player.getPersistentData().putInt("AGE", age.getAge() - 1);
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamageLowest(LivingIncomingDamageEvent event) {

        if(event.getAmount() < event.getEntity().getHealth()) return;

        if(event.getEntity() instanceof Player player && Helper.isVampire(player)) {
            int age = AgeingManager.getAge(player).getAge();

            if(!Helper.canKillVampires(event.getSource()) && CommonConfig.shouldOnlyDieFromKillingSources.get() && age >= CommonConfig.shouldOnlyDieFromKillingSourcesAgeRank.get()) {

                if(CommonConfig.immortalBloodLoss.get()) {
                    VampirePlayer vp = VampirePlayer.get(player);
                    if(event.getAmount() >= CommonConfig.bloodlossDamageThreshold.get()) {
                        int bloodLoss = Math.round(CommonConfig.bloodlossScaleFactor.get().floatValue() * Math.min(event.getAmount(), CommonConfig.bloodlossDamageCap.get().floatValue()));
                        vp.useBlood(bloodLoss, true);
                    }
                    if(vp.getBloodLevel() >= 0.5f) {
                        player.setHealth(1);
                        event.setCanceled(true);
                    }
                } else {
                    player.setHealth(1);
                    event.setCanceled(true);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onChangeFaction(PlayerFactionEvent.FactionLevelChanged event) {

        if(event.getNewLevel() == 0 || (event.getCurrentFaction() != event.getOldFaction() && event.getOldFaction() != null)) {
            Player player = event.getPlayer().asEntity();
            AgeingManager age = AgeingManager.getAge(player);
            IAgeType type = age.getAgeType();
            age.setAge(0);
            age.setRankProgress(0);
            CapabilityHelper.setDefaultAgeTypeAndMethod(player);

            if(!player.getCommandSenderWorld().isClientSide) {
                age.onAgeChange((ServerPlayer) player ,type);
            }

            if(event.getOldFaction() == VReference.HUNTER_FACTION && event.getCurrentFaction() != event.getOldFaction()) {
                if(player.hasEffect(com.thedrofdoctoring.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT)) {
                    player.removeEffect(com.thedrofdoctoring.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT);
                }
                if(age.getTypeState() instanceof HunterAgeingType.HunterState state) {
                    state.setTransformed(false);
                }
            }

        } else if (event.getOldLevel() == 0 && event.getNewLevel() > 0 && event.getCurrentFaction() == VReference.VAMPIRE_FACTION && CommonConfig.sireingMechanic.get() && event.getPlayer().getPlayer().getPersistentData().contains("AGE")) {
            int sireAge = event.getPlayer().asEntity().getPersistentData().getInt("AGE");
            AgeingManager age = AgeingManager.getAge(event.getPlayer().asEntity());
            age.setAge(sireAge);
            age.sync(true);
            event.getPlayer().asEntity().getPersistentData().remove("AGE");
        }
        if(event.getNewLevel() > 0 && event.getCurrentFaction() != null) {
            AgeingManager man = AgeingManager.getAge(event.getPlayer().asEntity());
            for(IAgeType type : AgeingRegistry.getAgeingTypes()) {
                if(event.getNewLevel() >= type.minFactionRank() && type.faction() == event.getCurrentFaction()) {
                    man.setType(type);
                    man.setAge(0);
                    CapabilityHelper.setDefaultAgeTypeAndMethod(event.getPlayer().asEntity());

                }
            }
        }
    }


    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if(player.tickCount % 100 == 0 && event.getEntity() instanceof ServerPlayer sPlayer) {
            AgeingManager age = AgeingManager.getAge(sPlayer);
            if(age.getMethod() instanceof TimeMethod && age.canAge()) {
                age.increaseRankPoints(100);
                age.sync(false);
            }
        }
        if(player.tickCount % 20 == 0 && CommonConfig.deadlySourcesFastDrainExhaustion.get()) {
            if(!event.getEntity().hasEffect(ModEffects.GARLIC)) {
                return;
            }
            if(!Helper.isVampire(player)) {
                return;
            }
            int age = AgeingManager.getAge(player).getAge();
            VampirePlayer vamp = VampirePlayer.get(player);
            vamp.addExhaustion(CommonConfig.amountExhaustionDrainFromSources.get().get(age).floatValue());
        }
    }


    @SubscribeEvent
    public static void sireBloodInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if(player.isShiftKeyDown() && CommonConfig.sireingMechanic.get() && Helper.isVampire(player) && event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().is(Items.GLASS_BOTTLE)) {
            int age = AgeingManager.getAge(player).getAge();
            if(age > 1 && VampirePlayer.get(player).getBloodLevel() > 8) {
                age -= 1;
                ItemStack mainHandStack = player.getMainHandItem();
                mainHandStack.shrink(1);
                ItemStack stack = ModItems.BLOOD_BOTTLE.get().getDefaultInstance();
                stack.set(AgeingDataComponents.AGE_RANK, age);
                stack.setDamageValue(4);
                player.addItem(stack);
                VampirePlayer.getOpt(player).ifPresent(vamp -> vamp.removeBlood(0.5f));
            }

        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
         if(event.getEntity() instanceof ServerPlayer player && CommonConfig.deathReset.get()) {
            AgeingManager age = AgeingManager.getAge(player);
            IAgeType originalAgeType = age.getAgeType();
            age.setAge(0);
            age.sync(true);
            age.onAgeChange(player, originalAgeType);
        }
        LivingEntity dead = event.getEntity();
        if(!dead.getCommandSenderWorld().isClientSide && event.getSource().getEntity() instanceof ServerPlayer player ) {
            if (CommonConfig.sireingMechanic.get() && player.getOffhandItem().is(Items.GLASS_BOTTLE) && Helper.isVampire(dead) && (dead instanceof AdvancedVampireEntity || dead instanceof Player)) {
                int age = AgeingManager.getAge(dead).get().getAge();
                ItemStack offHandStack = player.getOffhandItem();
                offHandStack.shrink(1);
                ItemStack stack = ModItems.BLOOD_BOTTLE.get().getDefaultInstance();
                stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(1));
                stack.set(AgeingDataComponents.AGE_RANK, age);
                player.addItem(stack);
            }
        }
        if(dead.getPersistentData().contains("AGE")) {
            dead.getPersistentData().remove("AGE");
        }
    }
    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.is(ModItems.BLOOD_BOTTLE.get()) && CommonConfig.sireingMechanic.get()) {
            int age = stack.getOrDefault(AgeingDataComponents.AGE_RANK, 0);
            if(age > 0) {
                event.getToolTip().add(Component.translatable("text.vampiricageing.blood_rank").append(String.valueOf(age)));
            }
        }
    }
    @SubscribeEvent
    public static void useItem(LivingEntityUseItemEvent.Stop event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();

        if(BloodHelper.getBlood(stack) != 0) return;

        if(CommonConfig.sireingMechanic.get()  && entity instanceof Player player && Helper.isVampire(entity) && event.getItem().is(ModItems.BLOOD_BOTTLE.get())) {
            int age = stack.getOrDefault(AgeingDataComponents.AGE_RANK, 0);
            AgeingManager manager = AgeingManager.getAge(player);
            if(manager.getAge() < age) {
                manager.setAge(age);
                manager.sync(false);
            }
            }

        }
}

