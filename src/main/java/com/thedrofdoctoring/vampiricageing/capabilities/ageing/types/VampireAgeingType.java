package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.actions.StepAssistAction;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.skills.VampiricAgeingSkills;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VampireAgeingType implements IAgeType  {
    private static final String id = "VAMPIRE";

    @Override
    public IPlayableFaction<?> faction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public Map<Holder<Attribute>, AttributeModifier> getAgeAttributes(int age, Player player, boolean cleanup) {
        HashMap<Holder<Attribute>, AttributeModifier> attributeMap = new HashMap<>();

        if(CommonConfig.shouldAgeAffectExhaustion.get() || cleanup) {
            attributeMap.put(ModAttributes.BLOOD_EXHAUSTION, new AttributeModifier(VampiricAgeing.rl("vamp_ageing_exhaustion"), CommonConfig.ageExhaustionEffect.get().get(age), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        if(cleanup) {
            attributeMap.put(Attributes.STEP_HEIGHT, new AttributeModifier(StepAssistAction.STEP_HEIGHT, 0.0f, AttributeModifier.Operation.ADD_VALUE));
        }

        attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(VampiricAgeing.rl("vamp_ageing_attack_damage"), CommonConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("vamp_ageing_max_health"), CommonConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(ModAttributes.DBNO_DURATION, new AttributeModifier(VampiricAgeing.rl("vamp_ageing_dbno_duration"), CommonConfig.DBNOTimeMultiplier.get().get(age), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        attributeMap.put(ModAttributes.NEONATAL_DURATION, new AttributeModifier(VampiricAgeing.rl("vamp_ageing_neonatal_duration"), CommonConfig.neonatalTimeMultiplier.get().get(age), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributeMap;
    }

    @Override
    public void handleSkills(int age, ServerPlayer player) {
        ISkillHandler<IVampirePlayer> skillHandler = VampirePlayer.get(player).getSkillHandler();

        if(age >= CommonConfig.ageWaterWalkingRank.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.WATER_WALKING_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.WATER_WALKING_SKILL.get());
        }

        if(age >= CommonConfig.stepAssistBonus.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.STEP_ASSIST_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.STEP_ASSIST_SKILL.get());
        }

        if(age >= CommonConfig.celerityActionRank.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
        }

        if(age >= CommonConfig.drainBloodActionRank.get()) {
            skillHandler.enableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
        } else {
            skillHandler.disableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
        }

    }

    @Override
    public int minFactionRank() {
        return CommonConfig.levelToBeginAgeMechanic.get();
    }

    @Override
    public boolean isEnabled() {
        return CommonConfig.vampireAgeing.get();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAgeTitle(int ageRank) {
        return CommonConfig.vampireAgeRankTitles.get().get(ageRank - 1);

    }

    @Override
    public Optional<TypeState> getStateType() {
        return Optional.empty();
    }


}
