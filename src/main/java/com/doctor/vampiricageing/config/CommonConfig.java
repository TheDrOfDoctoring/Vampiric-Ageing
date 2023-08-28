package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class CommonConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue timeBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue biteBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue drainBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue deathReset;
    public static final ForgeConfigSpec.BooleanValue advancedVampireAge;
    public static final ForgeConfigSpec.BooleanValue highAgeBadOmen;
    public static final ForgeConfigSpec.BooleanValue drainBloodAction;
    public static final ForgeConfigSpec.BooleanValue celerityAction;
    public static final ForgeConfigSpec.BooleanValue doesAgeAffectPrices;
    public static final ForgeConfigSpec.BooleanValue harsherOutOfBlood;
    public static final ForgeConfigSpec.BooleanValue sireingMechanic;
    public static final ForgeConfigSpec.BooleanValue ageWaterWalking;
    public static final ForgeConfigSpec.BooleanValue vampirePowderedSnowImmunity;
    public static final ForgeConfigSpec.IntValue ageWaterWalkingRank;
    public static final ForgeConfigSpec.IntValue levelToBeginAgeMechanic;
    public static final ForgeConfigSpec.IntValue stepAssistBonus;
    public static final ForgeConfigSpec.IntValue drainBloodActionDuration;
    public static final ForgeConfigSpec.IntValue drainBloodActionCooldown;
    public static final ForgeConfigSpec.IntValue drainBloodActionRank;
    public static final ForgeConfigSpec.IntValue celerityActionDuration;
    public static final ForgeConfigSpec.IntValue celerityActionCooldown;
    public static final ForgeConfigSpec.IntValue celerityActionRank;
    public static final ForgeConfigSpec.DoubleValue celerityActionMultiplier;


    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> sunDamageReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> percentageAdvancedVampireAges;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> genericVampireWeaknessReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageExhaustionEffect;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageAffectTradePrices;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageDamageIncrease;

    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ticksForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> drainedForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> infectedForNextAge;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        deathReset = COMMON_BUILDER.comment("Whether dying resets Age").define("deathReset", true);
        sireingMechanic = COMMON_BUILDER.comment("Intended to be a replacement for other forms of ageing, though will work with them. Overrides the mechanic to always begin at Level 1. Ranks can be gained by drinking blood of more powerful vampires. Highly recommended to turn off Death Reset and to make sure Advanced Vampire Age is turned on. More information on GitHub Readme or Curseforge Page").define("sireingMechanic", false);
        vampirePowderedSnowImmunity = COMMON_BUILDER.comment("Whether vampires should be immune to the effects of Powdered Snow. Applies to ALL vampires").define("powderedSnowImmunity", true);
        ageWaterWalking = COMMON_BUILDER.comment("Whether high Age Rank vampires can walk on water").define("ageWaterWalking", true);
        ageWaterWalkingRank = COMMON_BUILDER.comment("Age rank a vampire must be to walk on water").defineInRange("ageWaterWalkingRank", 4, 0,  5);
        celerityActionRank = COMMON_BUILDER.comment("What Age Rank a vampire must be to use the Celerity Action").defineInRange("celerityActionRank", 1, 0, 5);
        celerityActionCooldown = COMMON_BUILDER.comment("Cooldown of the Celerity action in seconds").defineInRange("celerityActionCooldown", 300, 20, 36000);
        celerityActionMultiplier = COMMON_BUILDER.comment("Speed Multiplier provided by Celerity Action").defineInRange("celerityActionMultiplier", 1.025D, 1, 5D);
        celerityActionDuration = COMMON_BUILDER.comment("Duration of the Celerity action in seconds").defineInRange("celerityBloodActionDuration", 8, 1, 36000);
        celerityAction = COMMON_BUILDER.comment("Whether the Celerity action is available for Aged Vampires").define("celerityBloodAction", true);
        drainBloodActionRank = COMMON_BUILDER.comment("What Age Rank a vampire must be to use the Blood Tap Action").defineInRange("drainBloodActionRank", 3, 0, 5);
        drainBloodActionCooldown = COMMON_BUILDER.comment("Cooldown of the Blood Tap action in seconds").defineInRange("drainBloodActionCooldown", 900, 1, 36000);
        drainBloodActionDuration = COMMON_BUILDER.comment("Duration of the Blood Tap action in seconds").defineInRange("drainBloodActionDuration", 10, 1, 36000);
        drainBloodAction = COMMON_BUILDER.comment("Whether the Blood Tap action is available for Aged Vampires").define("drainBloodAction", true);
        COMMON_BUILDER.comment("For any config with a list of 6 numbers, the very first number refers to a vampire with no age rank and the second number is the first age rank.");
        levelToBeginAgeMechanic = COMMON_BUILDER.comment("The level at which the age mechanic begins, Level 4 is the minimum age to have access to the Infect Action").defineInRange("levelToBeginAgeMechanic", 14, 0, 14);
        percentageAdvancedVampireAges = COMMON_BUILDER.comment("The percentage, as a decimal, of how likely an advanced vampire is to get each rank with advanced vampire ages enabled").defineList("percentageAdvancedVampireAges", Arrays.asList(0.5D, 0.3D, 0.1D, 0.08D, 0.02D), it -> it instanceof Double);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 2D, 4D, 6D, 8D, 10D), t -> t instanceof Double);
        doesAgeAffectPrices = COMMON_BUILDER.comment("Whether Age makes a difference on Trade Prices").define("doesAgeAffectPrices", true);
        harsherOutOfBlood = COMMON_BUILDER.comment("Makes running out of blood harsher on vampire, scaling with age").define("harsherOutOfBlood", false);
        ageAffectTradePrices = COMMON_BUILDER.comment("How much each rank affects Villager trade prices. ").defineList("ageAffectTradePrices", Arrays.asList(1D, 1.1D, 1.25D, 1.5D, 1.75D, 2D), t -> t instanceof Double);
        genericVampireWeaknessReduction = COMMON_BUILDER.comment("How much each rank reduces/increases generic vampire weakness damage sources (such as Fire) in terms of how much the damage is divided by. Set all to 1 to have no change, use decimal values to increase damage").defineList("genericVampireWeaknessReduction", Arrays.asList(1D, 1D, 0.95D, 0.9D, 0.75D, 0.5D), it -> it instanceof Double);
        sunDamageReduction = COMMON_BUILDER.comment("How much each rank reduces/increases Sun Damage in terms of how much the sun damage is divided by. Set all to 1 to have no change, use decimal values to increase sun damage").defineList("sunDamageReduction", Arrays.asList(1D, 1.5D, 2D, 3D, 4D, 5D), it -> it instanceof Double);
        biteBasedIncrease = COMMON_BUILDER.comment("Whether to use Number of Bites to increase Age. Enable only one option").define("infectionBasedIncrease", true);
        drainBasedIncrease = COMMON_BUILDER.comment("Whether to use fully draining villagers of blood to increase Age. Enable only one option").define("drainBasedIncrease", false);
        timeBasedIncrease = COMMON_BUILDER.comment("Whether to use Time Alive  to increase Age. Enable only one option").define("timeBasedIncrease", false);
        ticksForNextAge = COMMON_BUILDER.comment("How much time in ticks for a player to advance to the next Age Rank. Count is reset on Rank Up").defineList("ticksForNextAge", Arrays.asList(72000, 144000, 288000, 576000, 1152000), it -> it instanceof Integer);
        infectedForNextAge = COMMON_BUILDER.comment("How many entities infected for next Age Rank. Count is reset on Rank Up").defineList("infectedForNextAge", Arrays.asList(30, 45, 70, 100, 200), it -> it instanceof Integer);
        ageDamageIncrease = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. Set all to 0 to disable completely.").defineList("ageDamageIncrease", Arrays.asList(0D, 0D, 1D, 1.50D, 3D, 4.5D), it -> it instanceof Double);
        drainedForNextAge = COMMON_BUILDER.comment("How many entities drained for next Age Rank. Count is reset on Rank Up").defineList("drainedForNextAge", Arrays.asList(10, 25, 40, 70, 100), it -> it instanceof Integer);
        stepAssistBonus = COMMON_BUILDER.comment("The Age Rank at which a vampire gains step assist. Set to 0 to disable. ").defineInRange("stepAssistLevel", 2, 0, 5);
        ageExhaustionEffect = COMMON_BUILDER.comment("How much each rank affects Blood Exhaustion Rate (Blood Drain), lower numbers are a lower decrease in exhaustion, higher numbers decrease exhaustion, values above 1 will cause 0 blood drain. Set all to 0 to have no change in exhaustion rate. Negative Numbers can be used for inverse effect").defineList("ageExhaustionEffect", Arrays.asList(0.0D, 0.1D, 0.2D, 0.3D, 0.4D, 0.5D), it -> it instanceof Double);
        advancedVampireAge = COMMON_BUILDER.comment("Whether Advanced Vampires should spawn with an Age Tier").define("advancedVampireAge", true);
        highAgeBadOmen = COMMON_BUILDER.comment("When enabled, there is a chance to randomly get the Bad Omen effect as a High Age Vampire").define("highAgeOmen", true);
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
