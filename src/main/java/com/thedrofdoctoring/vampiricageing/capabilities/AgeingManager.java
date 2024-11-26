package com.thedrofdoctoring.vampiricageing.capabilities;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.AgeingRegistry;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeMethod;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.types.TypeState;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.init.ModAttachments;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.lib.lib.storage.IAttachment;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AgeingManager implements IAgeingCapability, IAttachment {
    public static final String NBT_KEY = "ageing_manager";
    public static final ResourceLocation AGEING_KEY = VampiricAgeing.rl(NBT_KEY);

    public static @NotNull AgeingManager getAge(@NotNull PathfinderMob entity) {
        return entity.getData(ModAttachments.AGEING_MANAGER.get());
    }
    public static @NotNull AgeingManager getAge(@NotNull Player entity) {
        return entity.getData(ModAttachments.AGEING_MANAGER.get());
    }
    public static @NotNull Optional<AgeingManager> getAge(@NotNull LivingEntity entity) {
        if(entity instanceof Player || entity instanceof PathfinderMob) {
            return Optional.of(entity.getData(ModAttachments.AGEING_MANAGER.get()));
        }
        return Optional.empty();
    }


    private IAgeType type;
    private IAgeMethod method;
    private TypeState ageingTypeData;
    private int ageRank;
    private final LivingEntity entity;
    private int rankProgress;
    private String typeId = null;

    public AgeingManager(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public TypeState getTypeState() {
        return ageingTypeData;
    }

    public static @NotNull Optional<AgeingManager> getOpt(@NotNull Player player) {
        return Optional.of(player.getData(ModAttachments.AGEING_MANAGER.get()));
    }

    @Override
    public String nbtKey() {
        return NBT_KEY;
    }

    @Override
    public int getAge() {
        return ageRank;
    }

    @Override
    public void setAge(int age) {
        this.ageRank = age;
    }

    @Override
    public void setRankProgress(int progress) {
        this.rankProgress = progress;
    }

    @Override
    public int getRankProgress() {
        return this.rankProgress;
    }


    @Override
    public IAgeMethod getMethod() {
        return this.method;
    }

    @Override
    public IAgeType getType() {
        return this.type;
    }
    @Override
    public void setType(IAgeType type) {
        this.type = type;
        if(type == null) {
            this.typeId = null;
        } else {
            this.typeId = type.getId();
        }
    }

    @Override
    public void setMethod(IAgeMethod method) {
        this.method = method;
    }

    @Override
    public ResourceLocation getAttachedKey() {
        return AGEING_KEY;
    }

    @Override
    public Entity asEntity() {
        return this.entity;
    }

    public void increaseRankPoints(int amount) {
        if(canAge()) {
            this.rankProgress += amount;
            if(this.rankProgress >= this.method.getRankProgressions()[ageRank]) {
                this.ageRank += 1;
                this.rankProgress = 0;
                if(Helper.isVampire(entity)) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);
                } else {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1, 1);
                }
                ModParticles.spawnParticlesServer(entity.level(), new GenericParticleOptions(ResourceLocation.fromNamespaceAndPath("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), entity.getX(), entity.getY(), entity.getZ(), 100, 1, 1, 1, 0);
            }
        }
    }

    public boolean canAge() {
        if(this.type == null) return false;

        if(entity instanceof ServerPlayer player && entity.isAlive()) {
            int level = FactionPlayerHandler.get(player).getCurrentLevel();

            return (checkRank(level) && this.ageRank < 5);
        }
        return false;
    }

    private boolean checkRank(int level) {
        if(CommonConfig.lordLevelRequirement.get() && entity instanceof ServerPlayer player) {
            int lordLevel = FactionPlayerHandler.get(player).getLordLevel();
            return lordLevel >= CommonConfig.lordLevelRankRequirement.get();
        } else return level >= type.minFactionRank();
    }



    public void  onAgeChange(ServerPlayer player, IAgeType oldAgeType) {

        this.type = this.getAgeType();
        if(oldAgeType != null && oldAgeType != this.type) {
            oldAgeType.handleSkills(this.ageRank, player);
            Map<Holder<Attribute>, AttributeModifier> oldAttributes =  oldAgeType.getAgeAttributes(this.ageRank, player, true);
            oldAttributes.forEach((attribute, modifier) -> {
                removeModifier(player.getAttribute(attribute), modifier.id());
            });
            if(this.type != null && this.type.getStateType().isPresent()) {
                this.ageingTypeData = this.type.getStateType().get();
            }
        }
        if(this.type != null) {
            Map<Holder<Attribute>, AttributeModifier> attributes =  this.type.getAgeAttributes(this.ageRank, player, false);
            attributes.forEach((attribute, modifier) -> {
                removeModifier(player.getAttribute(attribute), modifier.id());
                player.getAttribute(attribute).addPermanentModifier(modifier);
            });
            type.handleSkills(this.ageRank, player);
            if(this.ageingTypeData == null && this.type.getStateType().isPresent()) {
                this.ageingTypeData = this.type.getStateType().get();
            }
        }

    }


    public static void removeModifier(@NotNull AttributeInstance att, @NotNull ResourceLocation id) {
        AttributeModifier m = att.getModifier(id);
        if (m != null) {
            att.removeModifier(m);
        }
    }
    public IAgeType getAgeType() {
        IAgeType ageType;
        for(IAgeType type : AgeingRegistry.getAgeingTypes()) {
            if(type.getId().equals(this.typeId)) {
                ageType = type;
                return ageType;
            }
        }
        return null;
    }


    public void sync(boolean all) {
        HelperLib.sync(this, entity, all);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if(type != null) {
            nbt.putInt("ageing_rank", this.ageRank);
            if(entity instanceof Player) {
                nbt.putString("ageing_type", this.typeId);
                nbt.putInt("ageing_rank_progress", this.rankProgress);
                if(this.ageingTypeData != null ) {
                    nbt = ageingTypeData.serializeNBT(provider, nbt);
                }
            }
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.contains("ageing_type")) {
            this.ageRank = nbt.getInt("ageing_rank");
            if(entity instanceof Player) {
                this.typeId = nbt.getString("ageing_type");
                this.type = getAgeType();
                this.rankProgress = nbt.getInt("ageing_rank_progress");
                if(this.ageingTypeData == null && this.type.getStateType().isPresent()) {
                    this.ageingTypeData = this.type.getStateType().get();
                }
                if(this.ageingTypeData != null) {
                    this.ageingTypeData.deserializeNBT(provider, nbt);
                }
            }
        }
    }

    @Override
    public void deserializeUpdateNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        if(nbt.getString("ageing_type").isEmpty()) {
            this.type = null;
            this.ageRank = 0;
            this.rankProgress = 0;
        } else {
            this.ageRank = nbt.getInt("ageing_rank");
            if (entity instanceof Player) {
                this.typeId = nbt.getString("ageing_type");
                this.type = getAgeType();
                this.rankProgress = nbt.getInt("ageing_rank_progress");
                if(this.ageingTypeData == null && this.type.getStateType().isPresent()) {
                    this.ageingTypeData = this.type.getStateType().get();
                }
                if(this.ageingTypeData != null) {
                    this.ageingTypeData.deserializeUpdateNBT(provider, nbt);
                }
            }

        }
    }

    @Override
    public @NotNull CompoundTag serializeUpdateNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag nbt = new CompoundTag();

        if(type != null) {
            nbt.putInt("ageing_rank", this.ageRank);
            if(entity instanceof Player) {
                nbt.putString("ageing_type", this.typeId);
                nbt.putInt("ageing_rank_progress", this.rankProgress);
                if(this.ageingTypeData != null) {
                    nbt = ageingTypeData.serializeUpdateNBT(provider, nbt);
                }
            }

        }

        return nbt;
    }

    public static class Serializer implements IAttachmentSerializer<CompoundTag, AgeingManager> {

        @Override
        public @NotNull AgeingManager read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            if (holder instanceof PathfinderMob mob) {
                AgeingManager manager = new AgeingManager(mob);
                manager.deserializeNBT(provider, tag);
                return manager;
            }
            if (holder instanceof Player player) {
                AgeingManager manager = new AgeingManager(player);
                manager.deserializeNBT(provider, tag);
                return manager;
            }
            throw new IllegalStateException("Cannot deserialize ageing Manager for non-PathfinderMob / non-Player");
        }

        @Override
        public CompoundTag write(AgeingManager attachment, HolderLookup.@NotNull Provider provider) {
            return attachment.serializeNBT(provider);
        }
    }

    public static class Factory implements Function<IAttachmentHolder, AgeingManager> {

        @Override
        public AgeingManager apply(IAttachmentHolder holder) {
            if (holder instanceof PathfinderMob mob) {
                return new AgeingManager(mob);
            }
            if (holder instanceof Player player) {
                return new AgeingManager(player);
            }
            throw new IllegalArgumentException("Cannot create ageing manager attachment for holder " + holder.getClass() + ". Expected PathfinderMob or Player");
        }

    }

}
