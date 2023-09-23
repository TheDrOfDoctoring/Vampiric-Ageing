package com.doctor.vampiricageing.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class HunterAgeingConfig {

    public static ForgeConfigSpec HUNTER_AGEING_CONFIG;

    public static final ForgeConfigSpec.BooleanValue hunterAgeing;
    public static final ForgeConfigSpec.BooleanValue hunterIncreasedMiningSpeed;
    public static final ForgeConfigSpec.BooleanValue hunterTeleportAction;
    public static final ForgeConfigSpec.IntValue pettyHuntWorth;
    public static final ForgeConfigSpec.IntValue commonHuntWorth;
    public static final ForgeConfigSpec.IntValue greaterHuntWorth;
    public static final ForgeConfigSpec.IntValue seniorityOilUseAge;
    public static final ForgeConfigSpec.IntValue stepAssistAge;
    public static final ForgeConfigSpec.IntValue fasterRegenerationAge;
    public static final ForgeConfigSpec.IntValue hunterTeleportActionAge;
    public static final ForgeConfigSpec.IntValue hunterTeleportActionCooldown;
    public static final ForgeConfigSpec.IntValue hunterTeleportActionMaxDistance;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> huntedForNextAge;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> maxHealthIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> xpGainReduction;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> fasterExhaustionAmounts;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> ageEnemyFactionDamageIncrease;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> movementSpeedBonus;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> hunterMiningSpeedBonus;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Float>> seniorityOilDamageBonus;


    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Whenever there is a list with 6 values you can change, the first value refers to a player with Age 0 Rank. The final value therefore refers to an Age 5 Player");
        hunterAgeing = COMMON_BUILDER.comment("Whether hunters can age.").define("hunterAgeing", true);
        hunterTeleportAction = COMMON_BUILDER.comment("Whether teleport action is enabled / disabled").define("hunterTeleportAction", true);
        hunterTeleportActionAge = COMMON_BUILDER.comment("At what age can a hunter use the teleport action").defineInRange("hunterTeleportActionAge", 5, 0, 5);
        hunterTeleportActionCooldown = COMMON_BUILDER.comment("Cooldown of hunter teleport action in seconds").defineInRange("hunterTeleportActionCooldown", 20, 1, Integer.MAX_VALUE);
        hunterTeleportActionMaxDistance = COMMON_BUILDER.comment("Max range of hunter teleport action ").defineInRange("hunterTeleportActionMaxDistance", 35, 1, 1000);
        hunterIncreasedMiningSpeed = COMMON_BUILDER.comment("Whether hunters receive increased mining speed based on age").define("hunterMiningSpeed", true);
        seniorityOilUseAge = COMMON_BUILDER.comment("At what age should a hunter be able to use Seniority Oil").defineInRange("seniorityOilAge", 2, 0, 5);
        stepAssistAge = COMMON_BUILDER.comment("At what age should a hunter gain Step Assist").defineInRange("hunterStepAssistAge", 4, 0, 5);
        fasterRegenerationAge = COMMON_BUILDER.comment("At what age should a hunter gain Faster Regeneration").defineInRange("fasterRegeneration", 3, 0, 5);
        pettyHuntWorth = COMMON_BUILDER.comment("How much a petty hunt is worth. These are things like basic versions of vampires or survivalist werewolves").defineInRange("pettyHuntWorth", 1, 0, 99);
        commonHuntWorth = COMMON_BUILDER.comment("How much a common hunt is worth. These are things like advanced vampires or beast werewolves").defineInRange("commonHuntWorth", 3, 0, 99);
        greaterHuntWorth = COMMON_BUILDER.comment("How much a greater hunt is worth. These are things like vampire barons or alpha werewolves").defineInRange("greaterHuntWorth", 5, 0, 99);
        huntedForNextAge = COMMON_BUILDER.comment("How many points worth of devoured entities are needed to increase Age Rank. Count is reset on Rank Up").defineList("huntedforNextAge", Arrays.asList(20, 40, 80, 160, 250), it -> it instanceof Integer);
        maxHealthIncrease = COMMON_BUILDER.comment("Max Health Increase for each rank. This is addition, not multiplier based").defineList("maxHealthIncrease", Arrays.asList(0D, 2D, 4D, 6D, 8D, 10D), t -> t instanceof Double);
        movementSpeedBonus = COMMON_BUILDER.comment("How much faster a hunter is at each age rank. This is additive, not a multiplier").defineList("movementSpeedBonus", Arrays.asList(0f, 0.025f, 0.035f, 0.05f, 0.065f, 0.08f), t -> t instanceof Float);
        xpGainReduction = COMMON_BUILDER.comment("How much XP gain is divided by based on age rank. Decimal numbers can be used to instead make it an XP multiplier").defineList("xpGainReduction", Arrays.asList(1f, 1f, 1.25f, 1.5f, 1.75f, 2f), t -> t instanceof Float);
        fasterExhaustionAmounts = COMMON_BUILDER.comment("How much food exhaustion is multiplied by based on age").defineList("fasterExhaustionMultiplier", Arrays.asList(1f, 1f, 1.25f, 1.5f, 2f, 2.5f), t -> t instanceof Float);
        hunterMiningSpeedBonus = COMMON_BUILDER.comment("How much mining speed is increased based on age. This is addition based, not a multiplier. Set all to 0 to comppletely disable").defineList("hunterMiningSpeedBonus", Arrays.asList(0f, 1.25f, 2f, 2.5f, 3.25f, 5f), t -> t instanceof Float);
        seniorityOilDamageBonus = COMMON_BUILDER.comment("How much more damage seniority oil does to each age rank").defineList("seniorityOilDamageBonus", Arrays.asList(0f, 0f, 1f, 1.25f, 1.75f, 2.5f), t -> t instanceof Float);
        ageEnemyFactionDamageIncrease  = COMMON_BUILDER.comment("How much each age rank increases damage by adding on to base damage. This extra damage only applies to enemy faction creatures. Set all to 0 to disable completely.").defineList("ageEnemyFactionDamageIncrease", Arrays.asList(0f, 2f, 4f, 6f, 8f, 8f), it -> it instanceof Float);
        HUNTER_AGEING_CONFIG = COMMON_BUILDER.build();
    }

}
