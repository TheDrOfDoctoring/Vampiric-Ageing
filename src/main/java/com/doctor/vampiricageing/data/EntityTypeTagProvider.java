package com.doctor.vampiricageing.data;

import com.doctor.vampiricageing.VampiricAgeing;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
    public EntityTypeTagProvider(DataGenerator gen, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen, modId, existingFileHelper);
    }

    public static final TagKey<EntityType<?>> countsForDrained = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(VampiricAgeing.MODID, "valid_for_draining"));

    @Override
    protected void addTags() {
        this.tag(countsForDrained).add(EntityType.VILLAGER);
    }

}
