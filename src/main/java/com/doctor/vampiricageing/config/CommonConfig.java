package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class CommonConfig {

    public static ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue timeBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue biteBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue drainBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue huntingBasedIncrease;
    public static final ForgeConfigSpec.BooleanValue deathReset;
    public static final ForgeConfigSpec.BooleanValue advancedVampireAge;
    public static final ForgeConfigSpec.BooleanValue drainBloodAction;
    public static final ForgeConfigSpec.BooleanValue celerityAction;
    public static final ForgeConfigSpec.BooleanValue doesAgeAffectPrices;
    public static final ForgeConfigSpec.BooleanValue harsherOutOfBlood;
    public static final ForgeConfigSpec.BooleanValue sireingMechanic;
    public static final ForgeConfigSpec.BooleanValue ageWaterWalking;
    public static final ForgeConfigSpec.BooleanValue shouldAgeIncreaseHunterMobDamage;
    public static final ForgeConfigSpec.BooleanValue vampirePowderedSnowImmunity;
    public static final ForgeConfigSpec.BooleanValue shouldAgeAffectExhaustion;
    public static final ForgeConfigSpec.BooleanValue rageModeWeaknessToggle;
    public static final ForgeConfigSpec.BooleanValue shouldAgeAffectHealing;
    public static final ForgeConfigSpec.BooleanValue shouldOnlyDieFromKillingSources;
    public static final ForgeConfigSpec.BooleanValue deadlySourcesFastDrainExhaustion;
    public static final ForgeConfigSpec.IntValue ageWaterWalkingRank;
    public static final ForgeConfigSpec.IntValue shouldOnlyDieFromKillingSourcesAgeRank;
    public static final ForgeConfigSpec.IntValue levelToBeginAgeMechanic;
    public static final ForgeConfigSpec.IntValue stepAssistBonus;
    public static final ForgeConfigSpec.IntValue drainBloodActionDuration;
    public static final ForgeConfigSpec.IntValue drainBloodActionCooldown;
    public static final ForgeConfigSpec.IntValue drainBloodActionRank;
    public static final ForgeConfigSpec.IntValue celerityActionDuration;
    public static final ForgeConfigSpec.IntValue celerityActionCooldown;
    public static final ForgeConfigSpec.IntValue celerityActionRank;
    public static final ForgeConfigSpec.IntValue pettyHuntWorth;
    public static final ForgeConfigSpec.IntValue commonHuntWorth;
    public static final ForgeConfigSpec.IntValue greaterHuntWorth;
    public static final ForgeConfigSpec.IntValue ageLossDBNO;
    public static final ForgeConfigSpec.DoubleValue celerityActionMultiplier;


    //I have no idea whats going on with these lists. If they're set to be float lists, they get randomly converted to doubles and then are converted improperly back to floats which breaks everything. Setting them to doubles and then converting them properly seems to be the only fix
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> sunDamageReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> percentageAdvancedVampireAges;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> genericVampireWeaknessReduction;

    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageExhaustionEffect;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageAffectTradePrices;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageDamageIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageHealingMultiplier;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> damageMultiplierFromHunters;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> DBNOTimeMultiplier;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> neonatalTimeMultiplier;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> amountExhaustionDrainFromSources;


    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> ticksForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> huntedForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> drainedBloodForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> infectedForNextAge;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        deathReset = COMMON_BUILDER.comment("Whether dying resets Age").define("deathReset", true);
        biteBasedIncrease = COMMON_BUILDER.comment("Whether to use Number of Bites to increase Age. Enable only one option").define("infectionBasedIncrease", false);
        drainBasedIncrease = COMMON_BUILDER.comment("Whether to use draining blood to increase Age. Enable only one option").define("drainBasedIncrease", true);
        timeBasedIncrease = COMMON_BUILDER.comment("Whether to use Time Alive  to increase Age. Enable only one option").define("timeBasedIncrease", false);
        huntingBasedIncrease = COMMON_BUILDER.comment("Whether to use Hunted Entities to increase Age, this is effectively the same as the hunter form of ageing. Enable only one option").define("huntingBasedIncrease", false);
        pettyHuntWorth = COMMON_BUILDER.comment("How much a petty hunt is worth. These are things like basic versions of vampires or hunters").defineInRange("pettyHuntWorth", 1, 0, 99);
        commonHuntWorth = COMMON_BUILDER.comment("How much a common hunt is worth. These are things like advanced vampires or advanced hunters").defineInRange("commonHuntWorth", 3, 0, 99);
        greaterHuntWorth = COMMON_BUILDER.comment("How much a greater hunt is worth. These are things like vampire barons ").defineInRange("greaterHuntWorth", 5, 0, 99);
        huntedForNextAge = COMMON_BUILDER.comment("How many points worth of hunted entities are needed to increase Age Rank. Count is reset on Rank Up").defineList("huntedforNextAge", Arrays.asList(20, 40, 80, 160, 250), it -> true);
        sireingMechanic = COMMON_BUILDER.comment("Intended to be a replacement for other forms of ageing, though will work with them. Overrides the mechanic to always begin at Level 1. Ranks can be gained by drinking blood of more powerful vampires. Highly recommended to turn off Death Reset and to make sure Advanced Vampire Age is turned on. More information on GitHub Readme or Curseforge Page").define("sireingMechanic", false);
        vampirePowderedSnowImmunity = COMMON_BUILDER.comment("Whether vampires should be immune to the effects of Powdered Snow. Applies to ALL vampires").define("powderedSnowImmunity", true);
        ageWaterWalking = COMMON_BUILDER.comment("Whether high Age Rank vampires can walk on water").define("ageWaterWalking", true);
        ageWaterWalkingRank = COMMON_BUILDER.comment("Age rank a vampire must be to walk on water").defineInRange("ageWaterWalkingRank", 4, 0,  5);
        celerityActionRank = COMMON_BUILDER.comment("What Age Rank a vampire must be to use the Celerity Action").defineInRange("celerityActionRank", 1, 0, 5);
        celerityActionCooldown = COMMON_BUILDER.comment("Cooldown of the Celerity action in seconds").defineInRange("celerityActionCooldown", 60, 20, 36000);
        celerityActionMultiplier = COMMON_BUILDER.comment("Speed Multiplier provided by Celerity Action").defineInRange("celerityActionMultiplier", 1.025D, 1, 5D);
        celerityActionDuration = COMMON_BUILDER.comment("Duration of the Celerity action in seconds").defineInRange("celerityBloodActionDuration", 8, 1, 36000);
        celerityAction = COMMON_BUILDER.comment("Whether the Celerity action is available for Aged Vampires").define("celerityBloodAction", true);
        drainBloodActionRank = COMMON_BUILDER.comment("What Age Rank a vampire must be to use the Blood Tap Action").defineInRange("drainBloodActionRank", 3, 0, 5);
        drainBloodActionCooldown = COMMON_BUILDER.comment("Cooldown of the Blood Tap action in seconds").defineInRange("drainBloodActionCooldown", 150, 1, 36000);
        drainBloodActionDuration = COMMON_BUILDER.comment("Duration of the Blood Tap action in seconds").defineInRange("drainBloodActionDuration", 10, 1, 36000);
        drainBloodAction = COMMON_BUILDER.comment("Whether the Blood Tap action is available for Aged Vampires").define("drainBloodAction", true);
        COMMON_BUILDER.comment("For any config with a list of 6 numbers, the very first number refers to a vampire with no age rank and the second number is the first age rank.");
        levelToBeginAgeMechanic = COMMON_BUILDER.comment("The level at which the age mechanic begins, Level 4 is the minimum age to have access to the Infect Action").defineInRange("levelToBeginAgeMechanic", 14, 0, 14);
        percentageAdvancedVampireAges = COMMON_BUILDER.comment("The percentage, as a decimal, of how likely an advanced vampire is to get each rank with advanced vampire ages enabled").defineList("percentageAdvancedVampireAges", Arrays.asList(0.5D, 0.3D, 0.1D, 0.08D, 0.02D), it -> true);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 2D, 2D, 3D, 4D, 5D), t -> true);
        doesAgeAffectPrices = COMMON_BUILDER.comment("Whether Age makes a difference on Trade Prices").define("doesAgeAffectPrices", true);
        harsherOutOfBlood = COMMON_BUILDER.comment("Makes running out of blood harsher on vampire, scaling with age").define("harsherOutOfBlood", false);
        ageAffectTradePrices = COMMON_BUILDER.comment("How much each rank affects Villager trade prices. ").defineList("ageAffectTradePrices", Arrays.asList(1D, 1.1D, 1.25D, 1.5D, 1.75D, 2D), t -> true);
        rageModeWeaknessToggle = COMMON_BUILDER.comment("If enabled, vampire rage will nullify increased damage from generic vampire weakness reduction").define("rageModeWeaknessToggle", true);
        genericVampireWeaknessReduction = COMMON_BUILDER.comment("How much each rank reduces/increases generic vampire weakness damage sources (such as Fire) in terms of how much the damage is divided by. Set all to 1 to have no change, use decimal values to increase damage").defineList("genericVampireWeaknessReduction", Arrays.asList(1d, 1d, 0.95d, 0.9d, 0.8d, 0.75d), it -> true);
        sunDamageReduction = COMMON_BUILDER.comment("How much each rank reduces/increases Sun Damage in terms of how much the sun damage is divided by. Set all to 1 to have no change, use decimal values to increase sun damage").defineList("sunDamageReduction", Arrays.asList(1d, 1.25d, 1.5d, 1.75d, 2d, 2.5d), it -> true);
        ticksForNextAge = COMMON_BUILDER.comment("How much time in ticks for a player to advance to the next Age Rank. Count is reset on Rank Up").defineList("ticksForNextAge", Arrays.asList(72000, 144000, 288000, 576000, 1152000), it -> true);
        infectedForNextAge = COMMON_BUILDER.comment("How many entities infected for next Age Rank. Count is reset on Rank Up").defineList("infectedForNextAge", Arrays.asList(30, 45, 70, 100, 200), it -> true);
        ageDamageIncrease = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. Set all to 0 to disable completely.").defineList("ageDamageIncrease", Arrays.asList(0D, 1D, 1.5D, 2D, 3D, 4D), it -> true);
        drainedBloodForNextAge = COMMON_BUILDER.comment("How much blood drained for next Age Rank. Count is reset on Rank Up").defineList("drainedBloodForNextAge", Arrays.asList(150, 300, 600, 900, 1250), it -> true);
        stepAssistBonus = COMMON_BUILDER.comment("The Age Rank at which a vampire gains step assist. Set to 0 to disable. ").defineInRange("stepAssistLevel", 2, 0, 6);
        shouldAgeAffectExhaustion = COMMON_BUILDER.comment("Whether Age affects Blood Exhaustion").define("ageAffectsBloodExhaustion", true);
        ageExhaustionEffect = COMMON_BUILDER.comment("How much each rank affects Blood Exhaustion Rate (Blood Drain), lower numbers are a lower decrease in exhaustion, higher numbers decrease exhaustion, values above 1 will cause 0 blood drain. Set all to 0 to have no change in exhaustion rate. Negative Numbers can be used for inverse effect").defineList("ageExhaustionEffect", Arrays.asList(0.0D, 0.1D, 0.2D, 0.3D, 0.4D, 0.5D), it -> true);
        advancedVampireAge = COMMON_BUILDER.comment("Whether Advanced Vampires should spawn with an Age Tier").define("advancedVampireAge", true);
        shouldAgeAffectHealing = COMMON_BUILDER.comment("Whether Age Rank affects healing").define("ageHealingAffect", false);
        ageHealingMultiplier = COMMON_BUILDER.comment("How much each rank multiplies healing, this affects all types of healing ").defineList("ageHealingMultiplier", Arrays.asList(1d, 1d, 1d, 1.5d, 1.75d, 2d), it -> true);
        shouldAgeIncreaseHunterMobDamage = COMMON_BUILDER.comment("Whether Age Rank affects how much damage Hunter mobs do").define("shouldAgeIncreaseHunterMobDamage", true);
        damageMultiplierFromHunters = COMMON_BUILDER.comment("How much each rank multiplies damage from hunters, this only affects damage from hunter mobs, not players").defineList("damageMultiplierFromHunters", List.of(1.0d, 1.0d, 1.0d, 1.5d, 1.75d, 2d), it -> true);
        DBNOTimeMultiplier = COMMON_BUILDER.comment("How much each rank multiplies the time spent in DBNO. Decimal values decrease the time spent in DBNO ").defineList("DBNOTimeMultiplier", Arrays.asList(1d, 1d, 1d, 0.8d, 0.5d, 0.25d), it -> true);
        neonatalTimeMultiplier = COMMON_BUILDER.comment("How much each rank multiplies the duration of the neonatal effect. Decimal values decrease the time spent in neonatal").defineList("neonatalTimeMultiplier", Arrays.asList(1d, 1d, 1d, 1d, 0.75d, 0.5d), it -> true);
        shouldOnlyDieFromKillingSources = COMMON_BUILDER.comment("If enabled, (and the vampire meets the age rank requirement) a vampire will not go down when losing all of its health, it has to die from a deadly source of damage").define("shouldOnlyDieFromKillingSources", false);
        shouldOnlyDieFromKillingSourcesAgeRank = COMMON_BUILDER.comment("The age rank at which shouldOnlyDieFromKillingSources activates at").defineInRange("shouldOnlyDieFromKillingSourcesAgeRank", 4, 0, 5);
        deadlySourcesFastDrainExhaustion = COMMON_BUILDER.comment("Garlic and fire will quickly drain them of blood saturation").define("deadlySourcesFastDrainExhaustion", true);
        amountExhaustionDrainFromSources = COMMON_BUILDER.comment("How much extra exhaustion there is of each garlic and fire tick at each age rank").defineList("amountExhaustionDrainFromSources", Arrays.asList(0d, 0d, 0d, 0.08d, 0.16d, 0.3d), it -> true);
        ageLossDBNO = COMMON_BUILDER.comment("How many ranks of age are lost after resurrecting from DBNO").defineInRange("ageLossDBNO", 1, 0, 5);
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
