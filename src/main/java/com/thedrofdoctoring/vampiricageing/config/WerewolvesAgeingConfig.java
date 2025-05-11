package com.thedrofdoctoring.vampiricageing.config;


import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class WerewolvesAgeingConfig {
    public static ModConfigSpec WEREWOLF_AGEING_CONFIG;

    public static final ModConfigSpec.BooleanValue werewolfAgeing;
    public static final ModConfigSpec.BooleanValue ageBuffsHowl;
    public static final ModConfigSpec.BooleanValue bitingGivesFood;
    public static final ModConfigSpec.IntValue pettyDevourWorth;
    public static final ModConfigSpec.IntValue commonDevourWorth;
    public static final ModConfigSpec.IntValue greaterDevourWorth;
    public static final ModConfigSpec.IntValue exquisiteDevourWorth;
    public static final ModConfigSpec.IntValue rankForBiteFood;
    public static final ModConfigSpec.IntValue biteNutrition;
    public static final ModConfigSpec.IntValue levelToBeginAgeMechanic;

    public static final ModConfigSpec.DoubleValue biteSaturation;
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> devouredForNextAge;
    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> nourishmentMultipliers;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> biteDamageMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> silverOilDamageMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> formTimeMultiplier;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> healonBiteAmount;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> ageDamageIncrease;
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> silverDamageMultiplier;

    public static final ModConfigSpec.ConfigValue<String> ageingMethod;



    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("Whilst the Werewolf specific Ageing options are here, the general configuration options are in the main vampiricAgeing config. For example, resetting age on death or the level to begin ageing is managed in the main config, not this one");
        COMMON_BUILDER.comment("Whenever there is a list with 6 values you can change, the first value refers to a player with Age 0 Rank. The final value therefore refers to an Age 5 Player");
        werewolfAgeing = COMMON_BUILDER.comment("Whether Werewolves can Age. Requires Werewolves Addon for Vampirism.").define("werewolfAgeing", true);
        levelToBeginAgeMechanic = COMMON_BUILDER.comment("The level at which the age mechanic begins").defineInRange("levelToBeginAgeMechanic", 14, 0, 14);
        ageingMethod = COMMON_BUILDER.comment("Change to select Ageing type. Valid Options include: DEVOUR").define("ageingMethod", "DEVOUR");

        devouredForNextAge = COMMON_BUILDER.comment("How many points worth of devoured entities are needed to increase Age Rank. Count is reset on Rank Up").defineList("devouredforNextAge", Arrays.asList(30, 60, 100, 250, 500), it -> true);
        pettyDevourWorth = COMMON_BUILDER.comment("How much a petty devour is worth. These are things like basic animals").defineInRange("pettyDevourWorth", 1, 0, 99);
        commonDevourWorth = COMMON_BUILDER.comment("How much a common devour is worth. These are things like certain hostile mobs").defineInRange("commonDevourWorth", 2, 0, 99);
        greaterDevourWorth = COMMON_BUILDER.comment("How much a greater devour is worth. These are things like advanced faction mobs").defineInRange("greaterDevourWorth", 5, 0, 99);
        exquisiteDevourWorth = COMMON_BUILDER.comment("How much a exquisite devour is worth. These are things like vampire barons").defineInRange("exquisiteDevourWorth", 10, 0, 99);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 2D, 2D, 4D, 4D, 6D), t -> true);
        biteDamageMultiplier = COMMON_BUILDER.comment("How much each rank multiplies bite damage. Bite damage multiplier is strange and values of 0 should be treated as 1, values like 0.2 should be treated as a 1.2 multiplier").defineList("biteDamageMultiplier", Arrays.asList(0d, 0d, 0d, 0.125d, 0.25d, 0.5d), it -> true);
        silverOilDamageMultiplier = COMMON_BUILDER.comment("How much each rank multiplies damages from Silver Oil. Values are set to 1 by default as to disable it by default.").defineList("silverOilMultiplier", Arrays.asList(1d, 1d, 1d, 1d, 1d, 1d), it -> true);
        healonBiteAmount = COMMON_BUILDER.comment("How much the player is healed for biting an entity based on rank").defineList("healonBiteAmount", Arrays.asList(0d, 0d, 0d, 1d, 2d, 2d), it -> true);
        formTimeMultiplier = COMMON_BUILDER.comment("How much the duration of time a player can stay in werewolf form is multiplied by").defineList("formTimeMultiplier", Arrays.asList(1d, 1.5d, 2d, 3d, 4d, 5d), it -> true);
        ageBuffsHowl = COMMON_BUILDER.comment("Whether higher age ranks buffs the mobs summoned by howling").define("ageBuffsHowl", true);
        bitingGivesFood = COMMON_BUILDER.comment("When enabled, biting an entity provides sustenance").define("bitingGivesFood", true);
        rankForBiteFood = COMMON_BUILDER.comment("Requires bitingGivesFood to be enabled. At what rank should biting begin to give food").defineInRange("rankForBitefood", 2, 0, 5);
        biteNutrition = COMMON_BUILDER.comment("How much nutrition (hungar bar value) a bite gives").defineInRange("biteNutrition", 1, 0, 20);
        biteSaturation = COMMON_BUILDER.comment("How much saturation a bite gives").defineInRange("biteSaturation", 0.1D, 0D, 1.2D);
        nourishmentMultipliers = COMMON_BUILDER.comment("How much more nourishing raw meat is for a werewolf based on age rank").defineList("nourishmentMultipliers", Arrays.asList(1, 1, 2, 2, 3, 3), it -> true);
        ageDamageIncrease = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. Set all to 0 to disable completely.").defineList("ageDamageIncrease", Arrays.asList(0D, 0D, 1D, 2D, 3D, 4D), it -> true);
        silverDamageMultiplier = COMMON_BUILDER.comment("How much each age rank multiples damage taken, when the silver effect is active.").defineList("silverDamageMultiplier", Arrays.asList(1.0D, 1.0D, 1.1D, 1.15D, 1.2D, 1.25D), it -> true);

        WEREWOLF_AGEING_CONFIG = COMMON_BUILDER.build();
    }
}
