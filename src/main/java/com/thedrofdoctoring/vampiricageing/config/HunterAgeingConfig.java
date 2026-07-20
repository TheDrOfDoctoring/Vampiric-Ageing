package com.thedrofdoctoring.vampiricageing.config;


import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class HunterAgeingConfig {

    public static ModConfigSpec HUNTER_AGEING_CONFIG;

    public static final ModConfigSpec.BooleanValue hunterAgeing;
    public static final ModConfigSpec.BooleanValue hunterIncreasedMiningSpeed;
    public static final ModConfigSpec.BooleanValue hunterTeleportAction;
    public static final ModConfigSpec.BooleanValue taintedBloodAvailable;
    public static final ModConfigSpec.BooleanValue hunterLimitedBatModeAction;
    public static final ModConfigSpec.BooleanValue sunAffectLimitedBatMode;
    public static final ModConfigSpec.BooleanValue sunAffectTainted;
    public static final ModConfigSpec.BooleanValue permanentTransformationAvailable;
    public static final ModConfigSpec.BooleanValue permanentTransformationDeathReset;
    public static final ModConfigSpec.BooleanValue reducedBenefitFromNormalFoods;
    public static final ModConfigSpec.IntValue taintedSunAffectAge;
    public static final ModConfigSpec.IntValue taintedAgeForCoffinUse;
    public static final ModConfigSpec.IntValue seniorityOilUses;


    public static final ModConfigSpec.IntValue noNegativeEffectsFromBadFoodAge;
    public static final ModConfigSpec.IntValue limitedBatModeDurationTransformed;
    public static final ModConfigSpec.IntValue underwaterBreathingTaintedAge;
    public static final ModConfigSpec.IntValue pettyHuntWorth;
    public static final ModConfigSpec.IntValue commonHuntWorth;
    public static final ModConfigSpec.IntValue greaterHuntWorth;
    public static final ModConfigSpec.IntValue seniorityOilUseAge;
    public static final ModConfigSpec.IntValue stepAssistAge;
    public static final ModConfigSpec.IntValue taintedBloodBottleAge;
    public static final ModConfigSpec.IntValue fasterRegenerationAge;
    public static final ModConfigSpec.IntValue hunterTeleportActionAge;
    public static final ModConfigSpec.IntValue hunterTeleportActionCooldown;
    public static final ModConfigSpec.IntValue limitedBatModeAge;
    public static final ModConfigSpec.IntValue limitedBatModeCooldown;
    public static final ModConfigSpec.IntValue limitedBatModeDuration;
    public static final ModConfigSpec.IntValue hunterTeleportActionMaxDistance;
    public static final ModConfigSpec.IntValue temporaryTaintedBloodBaseTicks;
    public static final ModConfigSpec.IntValue taintedBloodWorseTradeDealsAge;
    public static final ModConfigSpec.IntValue taintedBloodHolyWaterAffectedAge;
    public static final ModConfigSpec.IntValue sunWeaknessTicks;
    public static final ModConfigSpec.IntValue sunSlownessTicks;
    public static final ModConfigSpec.IntValue sunSlownessThreeTicks;
    public static final ModConfigSpec.IntValue sunDamageTicks;
    public static final ModConfigSpec.IntValue maxTicksInSun;
    public static final ModConfigSpec.IntValue sunBlindnessTicks;
    public static final ModConfigSpec.IntValue lordLevelRankRequirement;
    public static final ModConfigSpec.DoubleValue limitedBatExhaustion;
    public static final ModConfigSpec.DoubleValue limitedBatFlightSpeed;
    public static final ModConfigSpec.DoubleValue baseSunDamageAmount;
    public static final ModConfigSpec.IntValue stepAssistDuration;
    public static final ModConfigSpec.IntValue stepAssistCooldown;
    public static final ModConfigSpec.IntValue wiseEyeAge;
    public static final ModConfigSpec.IntValue wiseEyeDuration;
    public static final ModConfigSpec.IntValue wiseEyeCooldown;
    public static final ModConfigSpec.BooleanValue wiseEyeSlowdown;


    public static final ModConfigSpec.ConfigValue<List<? extends String>> hunterAgeRankTitles;

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> huntedForNextAge;
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> taintedAgeNutritionReduction;
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> taintedAgeSunBadnessMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> fasterRegenerationMultPerAgeRank;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> regularTradeDealPricesMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> xpGainReduction;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> fasterExhaustionAmounts;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> ageDamageIncrease;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> movementSpeedBonus;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> hunterMiningSpeedBonus;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedHunterMiningSpeedBonus;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> seniorityOilDamageBonus;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedDamageBonuses;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedFireDamageMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedBloodTradeDealPricesMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedBloodMaxHealthIncreases;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedBloodMovementSpeedIncreases;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> taintedAgeSaturationReduction;


    public static final ModConfigSpec.ConfigValue<String> ageingMethod;
    public static final ModConfigSpec.IntValue levelToBeginAgeMechanic;



    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        COMMON_BUILDER.push("Base Hunter Ageing");

        COMMON_BUILDER.comment("Whenever there is a list with 6 values you can change, the first value refers to a player with Age 0 Rank. The final value therefore refers to an Age 5 Player");
        hunterAgeing = COMMON_BUILDER.comment("Whether hunters can age.").define("hunterAgeing", true);
        ageingMethod = COMMON_BUILDER.comment("Change to select Ageing type. Valid Options include: HUNTING").define("ageingMethod", "HUNTING");
        levelToBeginAgeMechanic = COMMON_BUILDER.comment("The level at which the age mechanic begins").defineInRange("levelToBeginAgeMechanic", 14, 0, 14);
        lordLevelRankRequirement = COMMON_BUILDER.comment("This defines the minimum lord level, for hunters, required to age, 0 to not require a lord level").defineInRange("hunterLordLevelRankRequirement", 0, 0, 5);

        hunterIncreasedMiningSpeed = COMMON_BUILDER.comment("Whether hunters receive increased mining speed based on age").define("hunterMiningSpeed", true);
        seniorityOilUseAge = COMMON_BUILDER.comment("At what age should a hunter be able to use Seniority Oil").defineInRange("seniorityOilAge", 2, 0, 5);
        stepAssistAge = COMMON_BUILDER.comment("At what age should a hunter gain Step Assist").defineInRange("hunterStepAssistAge", 4, 0, 6);
        stepAssistCooldown = COMMON_BUILDER.comment("Cooldown for step assist action").defineInRange("stepAssistCooldown", 0, 0, Integer.MAX_VALUE);
        stepAssistDuration = COMMON_BUILDER.comment("Duration for step assist action").defineInRange("stepAssistDuration", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
        wiseEyeAge = COMMON_BUILDER.comment("At what age should a hunter gain the Wise Eye action, allowing you to see invisible players.").defineInRange("wiseEyeAge", 5, 0, 6);
        wiseEyeCooldown = COMMON_BUILDER.comment("Cooldown for wise eye action").defineInRange("wiseEyeCooldown", 10, 0, Integer.MAX_VALUE);
        wiseEyeDuration = COMMON_BUILDER.comment("Duration for wise eye action").defineInRange("wiseEyeDuration", 120, 0, Integer.MAX_VALUE);
        wiseEyeSlowdown = COMMON_BUILDER.comment("Whether you will be slowed down whilst using wise eye").define("wiseEyeSlowdown", true);
        fasterRegenerationAge = COMMON_BUILDER.comment("At what age should a hunter gain Faster Regeneration").defineInRange("fasterRegeneration", 3, 0, 5);
        fasterRegenerationMultPerAgeRank  = COMMON_BUILDER.comment("Multiplies the rate of healing and exhaustion gain from Faster Regeneration").defineList("fasterRegenerationMultPerAgeRank", Arrays.asList(1d, 1d, 1d, 1d, 1d, 1d), it -> true);
        pettyHuntWorth = COMMON_BUILDER.comment("How much a petty hunt is worth. These are things like basic versions of vampires").defineInRange("pettyHuntWorth", 1, 0, 999999999);
        commonHuntWorth = COMMON_BUILDER.comment("How much a common hunt is worth. These are things like advanced vampires").defineInRange("commonHuntWorth", 3, 0, 999999999);
        greaterHuntWorth = COMMON_BUILDER.comment("How much a greater hunt is worth. These are things like vampire barons").defineInRange("greaterHuntWorth", 5, 0, 999999999);
        huntedForNextAge = COMMON_BUILDER.comment("How many points worth of hunted entities are needed to increase Age Rank. Count is reset on Rank Up").defineList("huntedforNextAge", Arrays.asList(20, 40, 80, 160, 250), it -> true);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 1D, 1D, 2D, 2D, 4D), t -> true);
        movementSpeedBonus = COMMON_BUILDER.comment("How much faster a hunter is at each age rank. This is additive, not a multiplier").defineList("movementSpeedBonus", Arrays.asList(0d, 0.0125d, 0.015d, 0.02d, 0.025d, 0.035d), t -> true);
        xpGainReduction = COMMON_BUILDER.comment("How much XP gain is divided by based on age rank. Decimal numbers can be used to instead make it an XP multiplier").defineList("xpGainReduction", Arrays.asList(1d, 1d, 1.25d, 1.5d, 1.75d, 2d), t -> true);
        fasterExhaustionAmounts = COMMON_BUILDER.comment("How much food exhaustion is multiplied by based on age").defineList("fasterExhaustionMultiplier", Arrays.asList(1d, 1d, 1.25d, 1.5d, 2d, 2.5d), t -> true);
        regularTradeDealPricesMultiplier  = COMMON_BUILDER.comment("When tainted ageing is not active, applies a multiplier to villager trading prices. Otherwise, defers to taintedBloodTradeDealPricesMultiplier").defineList("regularTradeDealPricesMultiplier", Arrays.asList(1d, 1d, 1d, 1d, 1d, 1d), it -> true);
        hunterMiningSpeedBonus  = COMMON_BUILDER.comment("When tainted ageing is not active, improves mining speed for each age rank by the corresponding multiplier").defineList("hunterIncreasedMiningSpeed", Arrays.asList(1d, 1d, 1d, 1d, 1d, 1d), it -> true);

        seniorityOilUses = COMMON_BUILDER.comment("The amount of hits the seniority oil is useful for").defineInRange("seniorityOilUses", 15, 1, Integer.MAX_VALUE);
        seniorityOilDamageBonus = COMMON_BUILDER.comment("How much more damage seniority oil does to each age rank").defineList("seniorityOilDamageBonus", Arrays.asList(0d, 0d, 0.1d, 0.2d, 0.4d, 0.6d), t -> true);
        ageDamageIncrease  = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. Replaced with tainted blood version Set all to 0 to disable completely.").defineList("hunterAgeDamageIncrease", Arrays.asList(0d, 0d, 1d, 1d, 1.5d, 2d), it -> true);

        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("Tainted Blood");

        COMMON_BUILDER.comment("The next few configs apply to the Tainted Blood mechanic for hunters. When there is a list of values, the first value will refer to a cumulative age (base age rank + tainted age rank) of 0 and the second to last value 10. The last value, 11, is only possible if the hunter has underwent permanent tainted transformation and is age rank 5.");
        taintedBloodAvailable = COMMON_BUILDER.comment("Whether tainted blood mechanic is enabled").define("taintedBloodAvailable", true);
        sunAffectTainted = COMMON_BUILDER.comment("Whether the sun affects tainted blood hunters").define("sunAffectTainted", true);
        taintedAgeSunBadnessMultiplier = COMMON_BUILDER.comment("The speed at which sun effect negative effects are gained.").defineList("taintedSunAgePenaltyGainSpeedMultiplier", Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 8), t -> true);
        taintedSunAffectAge = COMMON_BUILDER.comment("At what tainted blood cumulative age is a hunter affected by the sun").defineInRange("taintedSunAffectAge", 9, 0, 12);
        sunWeaknessTicks = COMMON_BUILDER.comment("How many ticks before a hunter eligible for sun damage gains weakness effect").defineInRange("sunWeaknessTicks", 1200, 1, Integer.MAX_VALUE);
        sunSlownessTicks = COMMON_BUILDER.comment("How many ticks before a hunter eligible for sun damage gains slowness effect").defineInRange("sunSlownessTicks", 1800, 1, Integer.MAX_VALUE);
        sunSlownessThreeTicks = COMMON_BUILDER.comment("How many ticks before a hunter eligible for sun damage gains slowness three effect").defineInRange("sunSlownessTwhreeTicks", 2800, 1, Integer.MAX_VALUE);
        sunDamageTicks = COMMON_BUILDER.comment("How many ticks before a hunter eligible for sun damage begins to take damage").defineInRange("sunDamageTicks", 3600, 1, Integer.MAX_VALUE);
        baseSunDamageAmount = COMMON_BUILDER.comment("Sun Damage amount").defineInRange("baseSunDamageAmount", 2.5d, 1, 100d);
        sunBlindnessTicks = COMMON_BUILDER.comment("How many ticks before a hunter eligible for sun damage gains blindness effect").defineInRange("sunBlindnessTicks", 5000, 1, Integer.MAX_VALUE);
        temporaryTaintedBloodBaseTicks = COMMON_BUILDER.comment("Base amount of ticks tainted blood bonus lasts for").defineInRange("temporaryTaintedBloodBaseTicks", 3600, 1, Integer.MAX_VALUE);
        maxTicksInSun = COMMON_BUILDER.comment("Max sun tick").defineInRange("maxTicksInSun", 10000, 1, Integer.MAX_VALUE);
        taintedAgeForCoffinUse = COMMON_BUILDER.comment("At what tainted age can a hunter use a coffin to skip day").defineInRange("taintedAgeForCoffinUse", 8, 0, 12);
        taintedBloodBottleAge = COMMON_BUILDER.comment("At what age can a hunter use tainted blood").defineInRange("taintedBloodBottleAge", 2, 0, 5);
        taintedBloodHolyWaterAffectedAge = COMMON_BUILDER.comment("At what tainted blood cumulative age is a hunter affected by holy water").defineInRange("taintedBloodHolyWaterAffectedAge", 6, 0, 12);
        taintedBloodWorseTradeDealsAge = COMMON_BUILDER.comment("At what cumulative age does a hunter begin to get worse trade deals").defineInRange("taintedBloodWorseTradeDealsAge", 7, 0, 12);
        taintedDamageBonuses = COMMON_BUILDER.comment("Damage bonus at each cumulative age rank").defineList("taintedAgeDamageBonus", Arrays.asList(0d, 0d, 0d, 1d, 1.2d, 1.4d, 1.6d, 1.8d, 2d, 2.25d, 2.5d, 3d), t -> true);
        taintedFireDamageMultiplier = COMMON_BUILDER.comment("How much damage from fire is multiplied by ").defineList("taintedAgeFireDamageMultiplier", Arrays.asList(1d, 1d, 1d, 1.2d, 1.2d, 1.4d, 1.6d, 1.8d, 2d, 2.25d, 2.5d, 3d), t -> true);
        taintedBloodMaxHealthIncreases = COMMON_BUILDER.comment("Max health bonus at each age rank. This replaces the regular age rank max health bonus").defineList("taintedAgeMaxHealthBonus", Arrays.asList(0d, 0d, 1d, 1d, 2d, 2d, 3d, 3d, 4d, 4d, 5d, 5d), t -> true);
        taintedBloodMovementSpeedIncreases = COMMON_BUILDER.comment("Movement speed bonuses at each age rank").defineList("taintedAgeMovementSpeedIncreases", Arrays.asList(0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0.02d, 0.03d, 0.04d, 0.06d), t -> true);
        taintedBloodTradeDealPricesMultiplier = COMMON_BUILDER.comment("How the price of villager trades are multiplied by baesd on tainted blood age").defineList("taintedBloodtradeDealPricesMultiplier", Arrays.asList(1d, 1d, 1d, 1d, 1d, 1d, 1d, 1.25d, 1.5d, 1.75d, 2d, 2.5d), t -> true);
        taintedHunterMiningSpeedBonus = COMMON_BUILDER.comment("How much mining speed is increased based on age. This is a multiplier of total mining speed").defineList("taintedHunterMiningSpeedBonus", Arrays.asList(1d, 1d, 1d, 1.05d, 1.1d, 1.15d, 1.2d, 1.25d, 1.3d, 1.325d, 1.35d, 1.5d), t -> true);
        hunterTeleportAction = COMMON_BUILDER.comment("Whether teleport action is enabled / disabled").define("hunterTeleportAction", true);
        hunterTeleportActionAge = COMMON_BUILDER.comment("At what cumulative tainted age can a hunter use the teleport action").defineInRange("hunterTeleportActionAge", 8, 0, 12);
        hunterTeleportActionCooldown = COMMON_BUILDER.comment("Cooldown of hunter teleport action in seconds").defineInRange("hunterTeleportActionCooldown", 20, 1, Integer.MAX_VALUE);
        hunterTeleportActionMaxDistance = COMMON_BUILDER.comment("Max range of hunter teleport action ").defineInRange("hunterTeleportActionMaxDistance", 35, 1, 1000);
        hunterLimitedBatModeAction = COMMON_BUILDER.comment("Whether limited bat mode is available").define("hunterLimitedBatModeAction", true);
        sunAffectLimitedBatMode = COMMON_BUILDER.comment("Whether limited bat mode is affected by the sun").define("sunAffectLimitedBatMode", false);
        limitedBatModeAge = COMMON_BUILDER.comment("At what cumulative tainted age can a hunter use the limited bat mode").defineInRange("limitedBatModeAge", 10, 0, 12);
        limitedBatModeCooldown = COMMON_BUILDER.comment("Cooldown of limited bat mode in seconds").defineInRange("limitedBatModeCooldown", 120, 1, Integer.MAX_VALUE);
        limitedBatFlightSpeed = COMMON_BUILDER.comment("Limited bat mode flight speed").defineInRange("limitedBatFlightSpeed", 0.02, 0.001, 0.2);
        limitedBatModeDuration = COMMON_BUILDER.comment("Duration of limited bat mode in seconds ").defineInRange("limitedBatModeDuration", 240, 1, 1000);
        limitedBatModeDurationTransformed = COMMON_BUILDER.comment("Duration of limited bat mode when in permanent tainted transformation").defineInRange("limitedBatModeDurationTransformed", Integer.MAX_VALUE - 1, 10, Integer.MAX_VALUE - 1);
        limitedBatExhaustion = COMMON_BUILDER.comment("Additional exhaustion added while in bat mode. ").defineInRange("limitedBatExhaustion", 0.008f, 0, 0.05);
        permanentTransformationAvailable = COMMON_BUILDER.comment("Whether permanent tainted transformation is available").define("permanentTaintedTransformation", true);
        permanentTransformationDeathReset = COMMON_BUILDER.comment("Whether the permanent tainted transformation is reset on death").define("permanentTransformationDeathReset", false);
        underwaterBreathingTaintedAge = COMMON_BUILDER.comment("At what age can a hunter breath forever underwater").defineInRange("underwaterBraethingTaintedAge", 11, 0, 12);
        reducedBenefitFromNormalFoods = COMMON_BUILDER.comment("Whether tainted blood can reduce nutrition from regular food").define("reducedBenefitFromNormalFoods", true);
        taintedAgeNutritionReduction = COMMON_BUILDER.comment("How much nutrition from food is reduced by at each tainted age").defineList("taintedAgeNutritionReduction", Arrays.asList(0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 4, 5), t -> true);
        taintedAgeSaturationReduction = COMMON_BUILDER.comment("How much saturation from food is reduced at each tainted age").defineList("taintedAgeSaturationReduction", Arrays.asList(0d, 0d, 0d, 0d, 0d, 0d, 0d, 0.025d, 0.05d, 0.1d, 0.15d, 0.2d), t -> true);
        noNegativeEffectsFromBadFoodAge = COMMON_BUILDER.comment("At what cumulative tainted age does a hunter not have negative effects from eating human hearts").defineInRange("noNegativeEffectsFromBadFoodAge", 6, 0, 12);
        hunterAgeRankTitles = COMMON_BUILDER.comment("If changed from default, changes the rank number for a given age rank to the provided text.").defineList("hunterAgeRankTitles", Arrays.asList("1", "2", "3", "4", "5"), it -> it instanceof String);

        COMMON_BUILDER.pop();

        HUNTER_AGEING_CONFIG = COMMON_BUILDER.build();
    }

}
