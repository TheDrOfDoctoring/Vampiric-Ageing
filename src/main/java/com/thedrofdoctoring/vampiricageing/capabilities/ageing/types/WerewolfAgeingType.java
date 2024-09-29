package com.thedrofdoctoring.vampiricageing.capabilities.ageing.types;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.core.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

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
    public void handleSkills(int age, ServerPlayer player) {}

    @Override
    public int minFactionRank() {
        return CommonConfig.levelToBeginAgeMechanic.get();
    }

    @Override
    public boolean isEnabled() {
        return WerewolvesAgeingConfig.werewolfAgeing.get();
    }

    @Override
    public String getId() {
        return id;
    }


}
