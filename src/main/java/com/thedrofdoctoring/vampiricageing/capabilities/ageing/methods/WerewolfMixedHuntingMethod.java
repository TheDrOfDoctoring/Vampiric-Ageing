package com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods;

import com.thedrofdoctoring.vampiricageing.AgeingReference;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.werewolves.core.ModDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.util.Arrays;

public class WerewolfMixedHuntingMethod extends HuntingMethod {

    private static int[] devoured;
    private static final String ID = "W_MIXED";

    @Override
    public int[] getRankProgressions() {
        if(devoured == null) {
            devoured = Arrays.stream(WerewolvesAgeingConfig.huntedForNextAge.get().toArray()).mapToInt(o -> (int)o).toArray();
        }
        return devoured;
    }
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded("werewolves") && WerewolvesAgeingConfig.ageingMethod.get().equals(ID);
    }
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public IAgeType getValidType() {
        return AgeingReference.WEREWOLF;
    }

    @Override
    public void displayLevelRequirements(Player player, int points, int age) {
        int pointsForNextAge = getRankProgressions()[age] - points;
        player.displayClientMessage(Component.translatable("text.vampiricageing.progress_hunted", pointsForNextAge).withStyle(ChatFormatting.DARK_RED), true);
    }

    @Override
    public void onAgedKill(LivingEntity target, Player sourceKiller, DamageSource source) {
        int pointWorth = 0;
        if(source.is(ModDamageTypes.BITE)) {
            if(target.getType().is(EntityTypeTagProvider.pettyDevour)) {
                pointWorth = WerewolvesAgeingConfig.pettyDevourWorth.get();
            } else if(target.getType().is(EntityTypeTagProvider.commonDevour)) {
                pointWorth = WerewolvesAgeingConfig.commonDevourWorth.get();
            } else if(target.getType().is(EntityTypeTagProvider.greaterDevour)) {
                pointWorth = WerewolvesAgeingConfig.greaterDevourWorth.get();
            } else if(target.getType().is(EntityTypeTagProvider.exquisiteDevour)) {
                pointWorth = WerewolvesAgeingConfig.exquisiteDevourWorth.get();
            }
        } else {
            if(target.getType().is(EntityTypeTagProvider.pettyHuntWerewolf)) {
                pointWorth = WerewolvesAgeingConfig.pettyHuntWorth.get();
            } else if(target.getType().is(EntityTypeTagProvider.commonHuntWerewolf)) {
                pointWorth = WerewolvesAgeingConfig.commonHuntWorth.get();
            } else if(target.getType().is(EntityTypeTagProvider.greaterHuntWerewolf)) {
                pointWorth = WerewolvesAgeingConfig.greaterHuntWorth.get();
            }
        }
        if(pointWorth > 0) {
            AgeingManager.getAge(sourceKiller).increaseRankPoints(pointWorth);
        }
    }
}
