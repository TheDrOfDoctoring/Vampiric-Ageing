package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.ConfigValue<Boolean> timeBasedIncrease;
    public static final ForgeConfigSpec.ConfigValue<Boolean> biteBasedIncrease;
    public static final ForgeConfigSpec.ConfigValue<Boolean> deathReset;
    public static final ForgeConfigSpec.ConfigValue<Boolean> advancedVampireAge;
    public static final ForgeConfigSpec.ConfigValue<Boolean> highAgeBadOmen;
    public static final ForgeConfigSpec.ConfigValue<Boolean> drainBloodAction;
    public static final ForgeConfigSpec.ConfigValue<Boolean> doesAgeAffectPrices;
    public static final ForgeConfigSpec.ConfigValue<Integer> levelToBeginAgeMechanic;
    public static final ForgeConfigSpec.ConfigValue<Integer> stepAssistBonus;
    public static final ForgeConfigSpec.ConfigValue<Integer> drainBloodActionDuration;
    public static final ForgeConfigSpec.ConfigValue<Integer> drainBloodActionCooldown;
    public static final ForgeConfigSpec.ConfigValue<Integer> drainBloodActionRank;



    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> sunDamageReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> genericVampireWeaknessReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> ageExhaustionEffect;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> maxHealthIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> ageAffectTradePrices;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ticksForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> infectedForNextAge;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        deathReset = COMMON_BUILDER.comment("Whether dying resets Age").define("deathReset", true);
        drainBloodActionRank = COMMON_BUILDER.comment("What Age Rank a vampire must be to use the Drain Blood Action").defineInRange("drainBloodActionRank", 3, 0, 5);
        drainBloodActionCooldown = COMMON_BUILDER.comment("Cooldown of the Drain Blood action in seconds").define("drainBloodActionCooldown", 600);
        drainBloodActionDuration = COMMON_BUILDER.comment("Duration of the Drain Blood action in seconds").define("drainBloodActionDuration", 60);
        drainBloodAction = COMMON_BUILDER.comment("Whether the Drain Blood action is available for Aged Vampires").define("drainBloodAction", true);
        COMMON_BUILDER.comment("For any config with a list of 6 numbers, the very first number refers to a vampire with no age rank and the second number is the first age rank.");
        levelToBeginAgeMechanic = COMMON_BUILDER.comment("The level at which the age mechanic begins").defineInRange("levelToBeginAgeMechanic", 14, 1, 14);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").define("maxHealthIncrease", List.of(0f, 2f, 4f, 6f, 8f, 10f));
        doesAgeAffectPrices = COMMON_BUILDER.comment("Whether Age makes a difference on Trade Prices").define("doesAgeAffectPrices", true);
        ageAffectTradePrices = COMMON_BUILDER.comment("How much each rank affects Villager trade prices. ").define("ageAffectTradePrices", List.of(1f, 1.1f, 1.25f, 1.5f, 1.75f, 2f));
        genericVampireWeaknessReduction = COMMON_BUILDER.comment("How much each rank reduces/increases generic vampire weakness damage sources (such as Fire) in terms of how much the damage is divided by. Set all to 1 to have no change, use decimal values to increase damage").define("genericVampireWeaknessReduction", List.of(1f, 1f, 0.95f, 0.9f, 0.75f, 0.5f));
        sunDamageReduction = COMMON_BUILDER.comment("How much each rank reduces/increases Sun Damage in terms of how much the sun damage is divided by. Set all to 1 to have no change, use decimal values to increase sun damage").define("sunDamageReduction", List.of(1f, 1.5f, 2f, 3f, 4f, 5f));
        biteBasedIncrease = COMMON_BUILDER.comment("Whether to use Time Alive or Number of Bites to increase Age. Enable only one option").define("infectionBasedIncrease", true);
        timeBasedIncrease = COMMON_BUILDER.comment("Whether to use Time Alive or Number of Bites to increase Age. Enable only one option").define("timeBasedIncrease", false);
        ticksForNextAge = COMMON_BUILDER.comment("How much time in ticks for a player to advance to the next Age Rank. Count is reset on Rank Up").define("ticksForNextAge", List.of(72000, 144000, 288000, 576000, 1152000));
        infectedForNextAge = COMMON_BUILDER.comment("How many entities infected for next Age Rank. Count is reset on Rank Up").define("infectedForNextAge", List.of(30, 45, 70, 100, 200));
        stepAssistBonus = COMMON_BUILDER.comment("The Age Rank at which a vampire gains step assist. Set to 0 to disable. ").defineInRange("stepAssistLevel", 2, 0, 5);
        ageExhaustionEffect = COMMON_BUILDER.comment("How much each rank affects Blood Exhaustion Rate (Blood Drain), lower numbers are a lower decrease in exhaustion, higher numbers decrease exhaustion, values above 1 will cause 0 blood drain. Set all to 0 to have no change in exhaustion rate. Negative Numbers can be used for inverse effect").define("ageExhaustionEffect", List.of(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f));
        advancedVampireAge = COMMON_BUILDER.comment("Whether Advanced Vampires should spawn with an Age Tier").define("advancedVampireAge", true);
        highAgeBadOmen = COMMON_BUILDER.comment("When enabled, there is a chance to randomly get the Bad Omen effect as a High Age Vampire").define("highAgeOmen", true);
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
