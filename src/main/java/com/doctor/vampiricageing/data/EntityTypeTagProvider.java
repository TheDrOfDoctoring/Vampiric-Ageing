package com.doctor.vampiricageing.data;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
    public EntityTypeTagProvider(DataGenerator gen, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen, modId, existingFileHelper);
    }

    public static final ITag.INamedTag<EntityType<?>> countsForDrained = vampiricAgeing("valid_for_draining");
    public static final ITag.INamedTag<EntityType<?>> pettyDevour = vampiricAgeing("petty_devour");
    public static final ITag.INamedTag<EntityType<?>> commonDevour = vampiricAgeing("common_devour");
    public static final ITag.INamedTag<EntityType<?>> greaterDevour = vampiricAgeing("greater_devour");
    public static final ITag.INamedTag<EntityType<?>> exquisiteDevour = vampiricAgeing("exquisite_devour");

    public static final ITag.INamedTag<EntityType<?>> pettyHunt = vampiricAgeing("petty_hunt");
    public static final ITag.INamedTag<EntityType<?>> commonHunt = vampiricAgeing("common_hunt");
    public static final ITag.INamedTag<EntityType<?>> greaterHunt = vampiricAgeing("greater_hunt");

    public static final ITag.INamedTag<EntityType<?>> infectedBlackList = vampiricAgeing("infected_blacklist");

    private static INamedTag<EntityType<?>> vampiricAgeing(String id) {
        return EntityTypeTags.bind(new ResourceLocation(VampiricAgeing.MODID, id).toString());
    }
    @Override
    protected void addTags() {
        this.tag(countsForDrained).add(EntityType.VILLAGER);

        this.tag(pettyDevour).add(EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.FOX, EntityType.SQUID, EntityType.WOLF, EntityType.HORSE, EntityType.DONKEY);
        this.tag(commonDevour).add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.GHAST, EntityType.BLAZE, EntityType.ENDERMAN, EntityType.GUARDIAN, ModEntities.HUNTER.get(), ModEntities.VAMPIRE.get(), ModEntities.HUNTER_IMOB.get(), ModEntities.VAMPIRE_IMOB.get(), de.teamlapen.werewolves.core.ModEntities.WEREWOLF_SURVIVALIST.get(), de.teamlapen.werewolves.core.ModEntities.ALPHA_WEREWOLF.get());
        this.tag(greaterDevour).add(ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_HUNTER_IMOB.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get());
        this.tag(exquisiteDevour).add(ModEntities.VAMPIRE_BARON.get(), de.teamlapen.werewolves.core.ModEntities.ALPHA_WEREWOLF.get(), EntityType.ELDER_GUARDIAN);

        this.tag(pettyHunt).add(ModEntities.VAMPIRE.get(), ModEntities.VAMPIRE_IMOB.get());
        this.tag(commonHunt).add(ModEntities.ADVANCED_VAMPIRE.get(), ModEntities.ADVANCED_VAMPIRE_IMOB.get());
        this.tag(greaterHunt).add(ModEntities.VAMPIRE_BARON.get());

        //any entities with this tag wont count towards infected counter
        this.tag(infectedBlackList).add(EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.FOX, EntityType.SQUID, EntityType.WOLF, EntityType.HORSE, EntityType.DONKEY);
    }

}
