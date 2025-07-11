package com.thedrofdoctoring.vampiricageing.capabilities.handlers;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.DevourMethod;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.werewolves.blocks.StoneAltarFireBowlBlock;
import de.teamlapen.werewolves.core.ModDamageTypes;
import de.teamlapen.werewolves.core.ModEffects;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class WerewolfAgeingHandler {



    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(event.getSource().getEntity() instanceof Player player && event.getSource().is(ModDamageTypes.BITE)) {
            AgeingManager age = AgeingManager.getAge(player);
            int pointWorth;
            if(!(age.getMethod() instanceof DevourMethod)) return;

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
            age.increaseRankPoints(pointWorth);
            age.sync(false);
        }

    }
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        AgeingManager manager = AgeingManager.getAge(player);
        if (Helper.isWerewolf(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof StoneAltarFireBowlBlock && manager.canAge() && event.getHand() == InteractionHand.MAIN_HAND) {
            int points = manager.getRankProgress();
            manager.getMethod().displayLevelRequirements(player, points, manager.getAge());
        }
    }
    @SubscribeEvent
    public void onHurt(LivingIncomingDamageEvent event) {
        if(event.getSource().is(ModDamageTypes.BITE)) {
            if(event.getSource().getEntity() instanceof Player player && Helper.isWerewolf(player) && !player.getCommandSenderWorld().isClientSide) {
                AgeingManager manager = AgeingManager.getAge(player);
                int age = manager.getAge();
                player.heal(WerewolvesAgeingConfig.healonBiteAmount.get().get(age).floatValue());
                if(WerewolvesAgeingConfig.bitingGivesFood.get() && age >= WerewolvesAgeingConfig.rankForBiteFood.get()) {
                    FoodData foodData = player.getFoodData();
                    foodData.eat(WerewolvesAgeingConfig.biteNutrition.get(), WerewolvesAgeingConfig.biteSaturation.get().floatValue());
                }
            }
        }
    }
    @SubscribeEvent
    public void onFoodEatenFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && Helper.isWerewolf((Player) event.getEntity())) {
            if (Helper.isRawMeat(player, event.getItem())) {
                AgeingManager manager = AgeingManager.getAge(player);
                int age = manager.getAge();
                int multiplier = WerewolvesAgeingConfig.nourishmentMultipliers.get().get(age) - 1;
                for(int i = 1; i <= multiplier; i++) {
                    player.eat(player.getCommandSenderWorld(), event.getItem());
                }
            }
        }
    }
    @SubscribeEvent
    public void onDamageWerewolves(LivingIncomingDamageEvent event) {
        if(!Helper.isWerewolf(event.getEntity())) {
            return;
        }
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(player.hasEffect(ModEffects.SILVER)) {
            int age = AgeingManager.getAge(player).getAge();
            event.setAmount(event.getAmount() * WerewolvesAgeingConfig.silverDamageMultiplier.get().get(age).floatValue());
        }

    }
}
