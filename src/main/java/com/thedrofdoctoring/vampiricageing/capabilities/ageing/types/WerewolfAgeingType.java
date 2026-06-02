package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import com.thedrofdoctoring.vampiricageing.skills.VampiricAgeingSkills;
import com.thedrofdoctoring.vampiricageing.skills.WerewolfAgeingSkills;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.core.ModAttributes;
import de.teamlapen.werewolves.core.ModSkills;
import de.teamlapen.werewolves.entities.player.werewolf.WerewolfPlayer;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WerewolfAgeingType implements IAgeType {

    private static final String id = "WEREWOLF";

    @Override
    public IPlayableFaction<?> faction() {
        return WReference.WEREWOLF_FACTION;
    }

    @Override
    public Map<Holder<Attribute>, AttributeModifier> getAgeAttributes(int age, Player player, boolean cleanup) {
        HashMap<Holder<Attribute>, AttributeModifier> attributeMap = new HashMap<>();
        attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(VampiricAgeing.rl("werewolf_ageing_attack_damage_increase"), WerewolvesAgeingConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("werewolf_ageing_max_health"), WerewolvesAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(ModAttributes.BITE_DAMAGE, new AttributeModifier(VampiricAgeing.rl("werewolf_ageing_bite_damage"), WerewolvesAgeingConfig.biteDamageMultiplier.get().get(age), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        return attributeMap;
    }

    @Override
    public void handleSkills(int age, ServerPlayer player) {
        ISkillHandler<IWerewolfPlayer> skillHandler = WerewolfPlayer.get(player).getSkillHandler();
        boolean requiresSensesSkill = WerewolvesAgeingConfig.improvedSensesSensesRequirement.getAsBoolean();
        boolean hasImprovedSensesAge = age >= WerewolvesAgeingConfig.improvedSensesAge.get();
        if((requiresSensesSkill && skillHandler.isSkillEnabled(ModSkills.SENSE) && hasImprovedSensesAge) || !requiresSensesSkill && hasImprovedSensesAge) {
            skillHandler.enableSkill(WerewolfAgeingSkills.IMPORVED_SENSES_SKILL.get());
        } else {
            skillHandler.disableSkill(WerewolfAgeingSkills.IMPORVED_SENSES_SKILL.get());
        }
    }

    @Override
    public int minFactionRank() {
        return CommonConfig.levelToBeginAgeMechanic.get();
    }

    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded("werewolves") && WerewolvesAgeingConfig.werewolfAgeing.get();
    }

    @Override
    public boolean canAge(ServerPlayer player) {
        return FactionPlayerHandler.get(player).getLordLevel() >= WerewolvesAgeingConfig.lordLevelRankRequirement.get();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAgeTitle(int ageRank) {
        return WerewolvesAgeingConfig.werewolfAgeRankTitles.get().get(ageRank - 1);
    }

    @Override
    public Optional<TypeState> getStateType() {
        return Optional.empty();
    }



}
