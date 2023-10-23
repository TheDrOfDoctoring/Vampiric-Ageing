package com.doctor.vampiricageing.data;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(DataGenerator gen, BlockTagsProvider provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen, provider, modId, existingFileHelper);
    }
    public static final TagKey<Item> taintedFood  = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(VampiricAgeing.MODID, "tainted_food"));


    @Override
    protected void addTags() {
        this.tag(taintedFood).add(ModItems.HUMAN_HEART.get()).add(ModItems.WEAK_HUMAN_HEART.get());
    }
}
