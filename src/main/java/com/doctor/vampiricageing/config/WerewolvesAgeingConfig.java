package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class WerewolvesAgeingConfig {
    public static ForgeConfigSpec WEREWOLF_AGEING_CONFIG;

    public static final ForgeConfigSpec.BooleanValue werewolfAgeing;
    public static final ForgeConfigSpec.BooleanValue devourBasedAgeing;
    public static final ForgeConfigSpec.BooleanValue ageBuffsHowl;
    public static final ForgeConfigSpec.IntValue pettyDevourWorth;
    public static final ForgeConfigSpec.IntValue commonDevourWorth;
    public static final ForgeConfigSpec.IntValue greaterDevourWorth;
    public static final ForgeConfigSpec.IntValue exquisiteDevourWorth;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> devouredForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> biteDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> silverOilDamageMultiplier;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> healonBiteAmount;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> ageDamageIncrease;


    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Whilst the Werewolf specific Ageing options are here, the general configuration options are in the main vampiricAgeing config. For example, resetting age on death or the level to begin ageing is managed in the main config, not this one");

        werewolfAgeing = COMMON_BUILDER.comment("Whether Werewolves can Age. Requires Werewolves Addon for Vampirism.").define("werewolfAgeing", true);
        devourBasedAgeing = COMMON_BUILDER.comment("Whether to use Devour Based Ageing. This means killing a mob with your bite attacks. Different mobs are worth different amounts of points").define("devourBasedAgeing", true);
        devouredForNextAge = COMMON_BUILDER.comment("How many points worth of devoured entities are needed to increase Age Rank. Count is reset on Rank Up").defineList("devouredforNextAge", Arrays.asList(30, 60, 100, 250, 500), it -> it instanceof Integer);
        pettyDevourWorth = COMMON_BUILDER.comment("How much a petty devour is worth. These are things like basic animals").defineInRange("pettyDevourWorth", 1, 0, 99);
        commonDevourWorth = COMMON_BUILDER.comment("How much a common devour is worth. These are things like certain hostile mobs").defineInRange("commonDevourWorth", 2, 0, 99);
        greaterDevourWorth = COMMON_BUILDER.comment("How much a greater devour is worth. These are things like advanced faction mobs").defineInRange("greaterDevourWorth", 4, 0, 99);
        exquisiteDevourWorth = COMMON_BUILDER.comment("How much a exquisite devour is worth. These are things like vampire barons").defineInRange("exquisiteDevourWorth", 8, 0, 99);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 2D, 4D, 6D, 8D, 10D), t -> t instanceof Double);
        biteDamageMultiplier = COMMON_BUILDER.comment("How much each rank multiplies bite damage").defineList("biteDamageMultiplier", Arrays.asList(1f, 1f, 1.25f, 1.5f, 1.75f, 2f), it -> it instanceof Float);
        silverOilDamageMultiplier = COMMON_BUILDER.comment("How much each rank multiplies damages from Silver Oil. Values are set to 1 by default as to disable it by default.").defineList("silverOilMultiplier", Arrays.asList(1f, 1f, 1f, 1f, 1f, 1f), it -> it instanceof Float);
        healonBiteAmount = COMMON_BUILDER.comment("How much the player is healed for biting an entity based on rank").defineList("healonBiteAmount", Arrays.asList(0f, 0f, 0f, 1f, 2f, 4f), it -> it instanceof Float);
        ageBuffsHowl = COMMON_BUILDER.comment("Whether higher age ranks buffs the mobs summoned by howling").define("ageBuffsHowl", true);
        ageDamageIncrease = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. Set all to 0 to disable completely.").defineList("ageDamageIncrease", Arrays.asList(0D, 0D, 1D, 1.50D, 3D, 4.5D), it -> it instanceof Double);
        WEREWOLF_AGEING_CONFIG = COMMON_BUILDER.build();
    }
}
