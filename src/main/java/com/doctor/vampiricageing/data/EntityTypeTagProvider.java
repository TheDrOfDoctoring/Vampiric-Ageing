package com.doctor.vampiricageing.data;

import com.doctor.vampiricageing.VampiricAgeing;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
    public EntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VampiricAgeing.MODID, existingFileHelper);
    }

    public static final TagKey<EntityType<?>> countsForDrained = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "valid_for_draining"));
    public static final TagKey<EntityType<?>> pettyDevour = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "petty_devour"));
    public static final TagKey<EntityType<?>> commonDevour = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "common_devour"));
    public static final TagKey<EntityType<?>> greaterDevour = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "greater_devour"));
    public static final TagKey<EntityType<?>> exquisiteDevour = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "exquisite_devour"));

    public static final TagKey<EntityType<?>> pettyHunt = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "petty_hunt"));
    public static final TagKey<EntityType<?>> commonHunt = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "common_hunt"));
    public static final TagKey<EntityType<?>> greaterHunt = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(VampiricAgeing.MODID, "greater_hunt"));
    @Override
    protected void addTags(HolderLookup.Provider holderLookup) {
        this.tag(countsForDrained).add(EntityType.VILLAGER);

        this.tag(pettyDevour).add(EntityType.SHEEP, EntityType.COW, EntityType.PIG, EntityType.FOX, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.AXOLOTL, EntityType.FROG, EntityType.GOAT, EntityType.WOLF, EntityType.HORSE, EntityType.DONKEY);
        this.tag(commonDevour).add(EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.GHAST, EntityType.BLAZE, EntityType.ENDERMAN, EntityType.GUARDIAN, ModEntities.HUNTER.get(), ModEntities.VAMPIRE.get(), de.teamlapen.werewolves.core.ModEntities.WEREWOLF_SURVIVALIST.get(), de.teamlapen.werewolves.core.ModEntities.ALPHA_WEREWOLF.get());
        this.tag(greaterDevour).add(ModEntities.ADVANCED_HUNTER.get(), ModEntities.ADVANCED_VAMPIRE.get());
        this.tag(exquisiteDevour).add(ModEntities.VAMPIRE_BARON.get(), de.teamlapen.werewolves.core.ModEntities.ALPHA_WEREWOLF.get(), EntityType.ELDER_GUARDIAN);

        this.tag(pettyHunt).add(ModEntities.VAMPIRE.get(), de.teamlapen.werewolves.core.ModEntities.WEREWOLF_SURVIVALIST.get());
        this.tag(commonHunt).add(ModEntities.ADVANCED_VAMPIRE.get(), de.teamlapen.werewolves.core.ModEntities.WEREWOLF_BEAST.get());
        this.tag(greaterHunt).add(ModEntities.VAMPIRE_BARON.get(), de.teamlapen.werewolves.core.ModEntities.ALPHA_WEREWOLF.get());
    }

}
