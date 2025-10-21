package com.thedrofdoctoring.vampiricageing.data.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;

public record AgeItemRestriction(int ageRank, IPlayableFaction<?> faction) {

    public static final Codec<AgeItemRestriction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("age_rank").forGetter(AgeItemRestriction::ageRank),
            IPlayableFaction.CODEC.fieldOf("for_faction").forGetter(AgeItemRestriction::faction)
    ).apply(instance, AgeItemRestriction::new));

}
