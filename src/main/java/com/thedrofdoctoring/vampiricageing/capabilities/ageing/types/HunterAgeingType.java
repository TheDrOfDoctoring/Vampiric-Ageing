package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class HunterAgeingType implements IAgeType {

    private static final String id = "HUNTER";
    @Override
    public IPlayableFaction<?> faction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public Map<Holder<Attribute>, AttributeModifier> getAgeAttributes(int age, Player player, boolean cleanup) {
        HashMap<Holder<Attribute>, AttributeModifier> attributeMap = new HashMap<>();
        attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_max_health"), HunterAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_speed"), HunterAgeingConfig.movementSpeedBonus.get().get(age), AttributeModifier.Operation.ADD_VALUE));
        int cumulativeTaintedBloodAge = CapabilityHelper.getCumulativeTaintedAge(player);
        attributeMap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(VampiricAgeing.rl("hunter_ageing_attack_damage"), HunterAgeingConfig.taintedDamageBonuses.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MAX_HEALTH, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_max_health"), HunterAgeingConfig.taintedBloodMaxHealthIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
        attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VampiricAgeing.rl("hunter_tainted_ageing_speed"), HunterAgeingConfig.taintedBloodMovementSpeedIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADD_VALUE));
        return attributeMap;
    }

    @Override
    public void handleSkills(int age, ServerPlayer player) {

    }

    @Override
    public int minFactionRank() {
        return HunterAgeingConfig.levelToBeginAgeMechanic.get();
    }

    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterAgeing.get();
    }

    @Override
    public String getId() {
        return id;
    }
}
