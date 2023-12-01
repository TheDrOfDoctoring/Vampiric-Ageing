package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.werewolves.blocks.StoneAltarFireBowlBlock;
import de.teamlapen.werewolves.core.ModDamageTypes;
import de.teamlapen.werewolves.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WerewolfAgeingManager {
    public static boolean shouldIncreaseRankDevoured(Player player) {
        return VampiricAgeingCapabilityManager.getAge(player).map(age -> age.getDevoured() >= WerewolvesAgeingConfig.devouredForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static void increasePoints(ServerPlayer player, int points) {
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
        if(WerewolvesAgeingConfig.devourBasedAgeing.get() && event.getSource().getEntity() instanceof ServerPlayer player && Helper.isWerewolf(event.getSource().getEntity()) && event.getSource().is(ModDamageTypes.BITE) && VampiricAgeingCapabilityManager.canAge(player)) {
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
            increasePoints(player, pointWorth);
        }
    }
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (Helper.isWerewolf(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof StoneAltarFireBowlBlock && VampiricAgeingCapabilityManager.canAge(player) && event.getHand() == InteractionHand.MAIN_HAND) {
            int age = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            if(WerewolvesAgeingConfig.devourBasedAgeing.get()) {
                int devourPoints = VampiricAgeingCapabilityManager.getAge(event.getEntity()).map(ageCap -> ageCap.getDevoured()).orElse(0);
                int devouredForNextAge = WerewolvesAgeingConfig.devouredForNextAge.get().get(age) - devourPoints;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_devour", devouredForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if(event.getSource().is(ModDamageTypes.BITE)) {
            if(event.getSource().getEntity() instanceof Player player && Helper.isWerewolf(player) && !player.getCommandSenderWorld().isClientSide) {
                int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                player.heal(WerewolvesAgeingConfig.healonBiteAmount.get().get(age).floatValue());
                if(WerewolvesAgeingConfig.bitingGivesFood.get() && age >= WerewolvesAgeingConfig.rankForBiteFood.get()) {
                    FoodData foodData = player.getFoodData();
                    foodData.eat(WerewolvesAgeingConfig.biteNutrition.get(), WerewolvesAgeingConfig.biteSaturation.get().floatValue());
                }
            }
        }
    }
    @SubscribeEvent
    public void onFootEatenFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && Helper.isWerewolf((Player) event.getEntity())) {
            if (Helper.isRawMeat(event.getItem())) {
                int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                int multiplier = WerewolvesAgeingConfig.nourishmentMultipliers.get().get(age) - 1;
                for(int i = 1; i <= multiplier; i++) {
                    player.getFoodData().eat(event.getItem().getItem(), event.getItem(), player);
                }
            }
        }
    }
}
