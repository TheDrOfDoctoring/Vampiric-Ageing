package com.thedrofdoctoring.vampiricageing.data.datamaps;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class AgeingDatamaps {

    public static final DataMapType<Item, AgeItemRestriction> AGE_ITEM_RESTRICTION = DataMapType.builder(
            VampiricAgeing.rl("age_item_restriction"),
            Registries.ITEM,
            AgeItemRestriction.CODEC
    ).synced(AgeItemRestriction.CODEC, true).build();
}
