package com.thedrofdoctoring.vampiricageing.data;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookup, blockTags.contentsGetter(), VampiricAgeing.MODID, existingFileHelper);
    }

    public static final TagKey<Item> taintedFood = ItemTags.create(VampiricAgeing.rl("tainted_food"));

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        this.tag(taintedFood).add(ModItems.HUMAN_HEART.get()).add(ModItems.WEAK_HUMAN_HEART.get());
    }


}