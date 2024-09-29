package com.thedrofdoctoring.vampiricageing.data;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

;

public class AgeingBlockTagsProvider extends net.neoforged.neoforge.common.data.BlockTagsProvider {
    public AgeingBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VampiricAgeing.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
