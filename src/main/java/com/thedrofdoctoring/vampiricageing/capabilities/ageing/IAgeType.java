package com.thedrofdoctoring.vampiricageing.capabilities.ageing;

import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.TypeState;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public interface IAgeType {

    IPlayableFaction<?> faction();

    Map<Holder<Attribute>, AttributeModifier> getAgeAttributes(int rank, Player player, boolean cleanup);

    void handleSkills(int age, ServerPlayer player);

    int minFactionRank();

    boolean isEnabled();

    String getId();

    Optional<? extends TypeState> getStateType();


}
