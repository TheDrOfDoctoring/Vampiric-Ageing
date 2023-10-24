package com.doctor.vampiricageing.data;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(DataGenerator gen, BlockTagsProvider provider, String modId, ExistingFileHelper existingFileHelper) {
        super(gen, provider, modId, existingFileHelper);
    }
    public static final ITag.INamedTag<Item> taintedFood = vampiricAgeing("tainted_food");
    private static ITag.INamedTag<Item> vampiricAgeing(String id) {
        return ItemTags.bind(new ResourceLocation(VampiricAgeing.MODID, id).toString());
    }

    @Override
    protected void addTags() {
        this.tag(taintedFood).add(ModItems.HUMAN_HEART.get()).add(ModItems.WEAK_HUMAN_HEART.get());
    }
}
