package com.thedrofdoctoring.vampiricageing.data.datamaps;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AgeingDatamapsProvider extends DataMapProvider {
    public AgeingDatamapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        gatherItemRestrictions(builder(AgeingDatamaps.AGE_ITEM_RESTRICTION));
    }
    @SuppressWarnings("unused")
    protected void gatherItemRestrictions(Builder<AgeItemRestriction, Item> itemRestrictions) {
        Function<Item, Holder<Item>> holder = BuiltInRegistries.ITEM::wrapAsHolder;
    }
}
