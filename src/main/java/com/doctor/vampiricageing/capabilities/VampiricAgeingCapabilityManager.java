package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import com.doctor.vampiricageing.networking.Networking;
import com.doctor.vampiricageing.networking.SyncCapabilityPacket;
import com.doctor.vampiricageing.skills.VampiricAgeingSkills;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.ModDamageSources;
import de.teamlapen.werewolves.api.WReference;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static de.teamlapen.vampirism.util.Helper.isHunter;
import static de.teamlapen.vampirism.util.Helper.isVampire;
import static de.teamlapen.werewolves.util.Helper.isWerewolf;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class VampiricAgeingCapabilityManager {
    //this is starting to get a bit messy isnt it
    public static final ResourceLocation AGEING_KEY = new ResourceLocation(VampiricAgeing.MODID, "ageing");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("060749f1-7868-4fdf-8a54-eb5ddf93742e");
    public static final UUID BITE_DAMAGE_MULTIPLIER_UUID = UUID.fromString("7936e1c2-3439-4819-b0e3-ea57dcc3e8ba");
    public static final UUID MAX_HEALTH_UUID = UUID.fromString("08251d58-2513-4768-b4b5-f2a1a239998e");
    public static final UUID WEREWOLF_MAX_HEALTH_AGE_UUID = UUID.fromString("47461eb3-1376-460d-b542-5d4d17d84b86");
    public static final UUID AGE_ADVANCED_SPEED_INCREASE = UUID.fromString("5728457b-ce7e-4bb5-96de-d1c6809dd1c3");
    public static final UUID EXHAUSTION_UUID = UUID.fromString("1f14dd76-7d9b-47b3-9951-1c221f78d49f");
    public static final UUID STEP_ASSIST_UUID = UUID.fromString("edee6b7f-755a-4dc5-a036-2b8108415c4c");
    public static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("94d546a9-6848-48cf-bcba-5e162987d58b");
    public static final UUID STRENGTH_INCREASE = UUID.fromString("ee0dca39-3d03-4f75-aed0-c1ae017969f2");
    public static final UUID WEREWOLF_STRENGTH_INCREASE = UUID.fromString("a47672d2-88c8-41de-bafe-8683de11f82a");
    public static final UUID HUNTER_MAX_HEALTH_UUID = UUID.fromString("c668f879-d57a-4182-ba82-87d93610e934");
    public static final UUID HUNTER_SPEED_INCREASE_UUID = UUID.fromString("4277f565-237e-4802-9653-203aa2ef92bb");
    public static final UUID HUNTER_TAINTED_DAMAGE_INCREASE_UUID = UUID.fromString("ea65e383-9172-42ab-a685-53a0c1c4fb3f");
    public static final UUID HUNTER_TAINTED_MAX_HEALTH_INCREASE_UUID = UUID.fromString("128f81c7-27b4-4d0b-a07f-e1a0055ba36a");
    public static final UUID HUNTER_TAINTED_MOVEMENT_SPEED_INCREASE_UUID = UUID.fromString("3c4cdc94-75e8-4528-9800-90298dc44b8a");
    public static LazyOptional<IAgeingCapability> getAge(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return LazyOptional.empty();
        }
        return livingEntity.getCapability(AGEING_CAPABILITY);
    }

    public static final Capability<IAgeingCapability> AGEING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean canAge(LivingEntity entity) {
        if(entity instanceof ServerPlayer player && entity.isAlive()) {
            if(isVampire(player)) {
                int level = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
                int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                return (level >= CommonConfig.levelToBeginAgeMechanic.get() && age < 5);

            } else if(CapabilityHelper.isWerewolfCheckMod(player)) {
                int level = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(WReference.WEREWOLF_FACTION)).orElse(0);
                int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                return (level >= CommonConfig.levelToBeginAgeMechanic.get() && age < 5);

            } else if(HunterAgeingConfig.hunterAgeing.get() && isHunter(player)) {
                int level = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.HUNTER_FACTION)).orElse(0);
                int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
                return (level >= CommonConfig.levelToBeginAgeMechanic.get() && age < 5);

            }
        }
        return false;
    }
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if(event.getSource().getEntity() instanceof ServerPlayer player && Helper.isVampire(player) && VampiricAgeingCapabilityManager.canAge(player)) {
            int pointWorth;
            if(event.getEntity().getType().is(EntityTypeTagProvider.pettyHunt)) {
                pointWorth = CommonConfig.pettyHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.commonHunt)) {
                pointWorth = CommonConfig.commonHuntWorth.get();
            } else if(event.getEntity().getType().is(EntityTypeTagProvider.greaterHunt)) {
                pointWorth = CommonConfig.greaterHuntWorth.get();
            } else {
                return;
            }
            CapabilityHelper.increasePoints(player, pointWorth);
        }
    }

    public static boolean shouldIncreaseRankTicks(Player player) {
        return getAge(player).map(age -> age.getTime() >= CommonConfig.ticksForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static boolean shouldIncreaseRankInfected(Player player) {
        return getAge(player).map(age -> age.getInfected() >= CommonConfig.infectedForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static boolean shouldIncreaseRankDrained(Player player) {
        return getAge(player).map(age -> age.getDrained() >= CommonConfig.drainedBloodForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static void increaseAge(ServerPlayer player) {
        if(canAge(player)) {
            getAge(player).ifPresent(age -> {
                if(Helper.isVampire(player)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);
                ModParticles.spawnParticlesServer(player.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
                }
                age.setAge(age.getAge() + 1);
                if(Helper.isHunter(player) && player.hasEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get())) {
                    player.removeEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get());
                }
                syncAgeCap(player);
            });
        }
    }
    //possibly needs a refactor
    public static void onAgeChange(ServerPlayer player, IPlayableFaction<?> faction) {
        int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        checkSkills(age, player);
        if(Helper.isVampire(player) || faction == VReference.VAMPIRE_FACTION) {
            removeModifier(player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()), EXHAUSTION_UUID);
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), MAX_HEALTH_UUID);
            removeModifier(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()), STEP_ASSIST_UUID);
            removeModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), STRENGTH_INCREASE);
            if(age > 0 && age >= CommonConfig.stepAssistBonus.get()) {
                player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()).addPermanentModifier(new AttributeModifier(STEP_ASSIST_UUID, "AGE_STEP_ASSIST_CHANGE", 0.5, AttributeModifier.Operation.ADDITION));
            }
            if(CommonConfig.shouldAgeAffectExhaustion.get()) {
                player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).addPermanentModifier(new AttributeModifier(EXHAUSTION_UUID, "AGE_EXHAUSTION_CHANGE", CommonConfig.ageExhaustionEffect.get().get(age), AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(STRENGTH_INCREASE, "AGE_ATTACK_DAMAGE_INCREASE", CommonConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH_UUID, "MAX_HEALTH_AGE_CHANGE", CommonConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
        } else if(CapabilityHelper.isWerewolfCheckMod(player, faction) ) {
            removeModifier(player.getAttribute(de.teamlapen.werewolves.core.ModAttributes.BITE_DAMAGE.get()), BITE_DAMAGE_MULTIPLIER_UUID);
            removeModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), WEREWOLF_STRENGTH_INCREASE);
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), WEREWOLF_MAX_HEALTH_AGE_UUID);

            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(WEREWOLF_STRENGTH_INCREASE, "WEREWOLF_AGE_ATTACK_DAMAGE_INCREASE", WerewolvesAgeingConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(WEREWOLF_MAX_HEALTH_AGE_UUID, "WEREWOLF_MAX_HEALTH_AGE_CHANGE", WerewolvesAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(de.teamlapen.werewolves.core.ModAttributes.BITE_DAMAGE.get()).addPermanentModifier(new AttributeModifier(BITE_DAMAGE_MULTIPLIER_UUID, "AGE_BITE_DAMAGE_INCREASE", WerewolvesAgeingConfig.biteDamageMultiplier.get().get(age), AttributeModifier.Operation.MULTIPLY_TOTAL));
        } else if(isHunter(player) || faction == VReference.HUNTER_FACTION) {
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), HUNTER_MAX_HEALTH_UUID);
            removeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), HUNTER_SPEED_INCREASE_UUID);
            removeModifier(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()), STEP_ASSIST_UUID);
            if(age > 0 && age >= HunterAgeingConfig.stepAssistAge.get()) {
                player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()).addPermanentModifier(new AttributeModifier(STEP_ASSIST_UUID, "AGE_STEP_ASSIST_CHANGE", 0.5, AttributeModifier.Operation.ADDITION));
            }
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HUNTER_MAX_HEALTH_UUID, "HUNTER_AGE_MAX_HEALTH_INCREASE", HunterAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(HUNTER_SPEED_INCREASE_UUID, "HUNTER_AGE_SPEED_INCREASE", HunterAgeingConfig.movementSpeedBonus.get().get(age), AttributeModifier.Operation.ADDITION));
            int cumulativeTaintedBloodAge = CapabilityHelper.getCumulativeTaintedAge(player);
            removeModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), HUNTER_TAINTED_DAMAGE_INCREASE_UUID);
            removeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), HUNTER_TAINTED_MOVEMENT_SPEED_INCREASE_UUID);
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), HUNTER_TAINTED_MAX_HEALTH_INCREASE_UUID);
            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(HUNTER_TAINTED_DAMAGE_INCREASE_UUID, "HUNTER_TAINTED_DAMAGE_INCREASE", HunterAgeingConfig.taintedDamageBonuses.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HUNTER_TAINTED_MAX_HEALTH_INCREASE_UUID, "HUNTER_TAINTED_MAX_HEALTH_INCREASE", HunterAgeingConfig.taintedBloodMaxHealthIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(HUNTER_TAINTED_MOVEMENT_SPEED_INCREASE_UUID, "HUNTER_TAINTED_MOVEMENT_SPEED_INCREASE", HunterAgeingConfig.taintedBloodMovementSpeedIncreases.get().get(cumulativeTaintedBloodAge), AttributeModifier.Operation.ADDITION));
        }
    }
    public static void checkSkills(int age, ServerPlayer player) {
        int unlockedSkills = 0;
        if(Helper.isVampire(player)) {
            unlockedSkills = VampirePlayer.getOpt(player).map(vamp -> {
                ISkillHandler<IVampirePlayer> handler = vamp.getSkillHandler();
                int unlockedSkillsCount = 0;
                if (age >= CommonConfig.drainBloodActionRank.get()) {
                    handler.enableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
                    unlockedSkillsCount++;
                } else {
                    handler.disableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
                }
                if (age >= CommonConfig.celerityActionRank.get()) {
                    handler.enableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
                    unlockedSkillsCount++;
                } else {
                    handler.disableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
                }
                return unlockedSkillsCount;
            }).orElse(0);
        }  else if(Helper.isHunter(player)) {
            unlockedSkills = HunterPlayer.getOpt(player).map(hunter -> {
                ISkillHandler<IHunterPlayer> handler = hunter.getSkillHandler();
                int unlockedSkillsCount = 0;
                if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
                    unlockedSkillsCount++;
                } else {
                    handler.disableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
                }
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                if(cumulativeAge >= HunterAgeingConfig.hunterTeleportActionAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
                    unlockedSkillsCount++;
                } else {
                    handler.disableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
                }

                if(cumulativeAge >= HunterAgeingConfig.limitedBatModeAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
                    unlockedSkillsCount++;
                } else {
                    handler.disableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
                }
                return unlockedSkillsCount;
            }).orElse(0);
        }
        int finalUnlockedSkills = unlockedSkills;
        getAge(player).ifPresent(ageCap -> ageCap.setAgeSkills(finalUnlockedSkills));
        syncAgeCapNoChange(player);
    }

    public static void removeModifier(@NotNull AttributeInstance att, @NotNull UUID uuid) {
        AttributeModifier m = att.getModifier(uuid);
        if (m != null) {
            att.removeModifier(m);
        }
    }
    public static void incrementInfected(ServerPlayer player) {
        getAge(player).ifPresent(age -> {
            age.setInfected(age.getInfected() + 1);
            syncAgeCap(player);
            if(shouldIncreaseRankInfected(player)) {
                increaseAge(player);
            }
        });
    }
    public static void increaseDrainedBlood(ServerPlayer player, int amount) {
        getAge(player).ifPresent(age -> {
            age.setDrained(age.getDrained() + amount);
            syncAgeCap(player);
            if(shouldIncreaseRankDrained(player)) {
                increaseAge(player);
            }
        });
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        final AgeingCapabilityProvider provider = new AgeingCapabilityProvider();
        if(event.getObject() instanceof Player || event.getObject() instanceof AdvancedVampireEntity || event.getObject() instanceof AdvancedVampireEntity.IMob ) {
            event.addCapability(AGEING_KEY, provider);
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IAgeingCapability.class);
    }
    @SubscribeEvent
    public static void onPotionEffectRemove(MobEffectEvent.Remove event) {
        if(CommonConfig.sireingMechanic.get() && event.getEffect() == ModEffects.SANGUINARE.get() && event.getEntity() instanceof Player player && !event.getEntity().getCommandSenderWorld().isClientSide) {
            player.getPersistentData().remove("AGE");
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
            event.getOriginal().reviveCaps();
                getAge(event.getOriginal()).ifPresent(oldAge -> {
                    IAgeingCapability newAgeCap = getAge(event.getEntity()).orElse(new AgeingCapability());
                    CompoundTag ageTag = oldAge.serializeNBT();
                    newAgeCap.deserializeNBT(ageTag);
                    syncAgeCap(event.getEntity());
                });
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onCoffinInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if(Helper.isVampire(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof CoffinBlock && canAge(player)) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            if(CommonConfig.timeBasedIncrease.get()) {
                int ticksAlive = getAge(event.getEntity()).map(ageCap -> ageCap.getTime()).orElse(0);
                int secondsForNextAge = (CommonConfig.ticksForNextAge.get().get(age) - ticksAlive) / 20;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_ticks", secondsForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
            if(CommonConfig.biteBasedIncrease.get()) {
                int infected = getAge(event.getEntity()).map(ageCap -> ageCap.getInfected()).orElse(0);
                int infectedForNextAge = CommonConfig.infectedForNextAge.get().get(age) - infected;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_infected", infectedForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
            if(CommonConfig.drainBasedIncrease.get()) {
                int drained = getAge(event.getEntity()).map(ageCap -> ageCap.getDrained()).orElse(0);
                int drainedForNextAge = CommonConfig.drainedBloodForNextAge.get().get(age) - drained;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_drained", drainedForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
            if(CommonConfig.huntingBasedIncrease.get()) {
                int hunted = getAge(event.getEntity()).map(ageCap -> ageCap.getHunted()).orElse(0);
                int huntedForNextAge = CommonConfig.huntedForNextAge.get().get(age) - hunted;
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_hunted_vampire", huntedForNextAge).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }
    @SubscribeEvent
    public static void sireBloodInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if(player.isShiftKeyDown() && CommonConfig.sireingMechanic.get() && Helper.isVampire(player) && event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().is(Items.GLASS_BOTTLE)) {
            int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            if(age > 1 && VampirePlayer.getOpt(player).map(vamp -> vamp.getBloodLevel() > 8).orElse(false)) {
                age -= 1;
                ItemStack mainHandStack = player.getMainHandItem();
                mainHandStack.shrink(1);
                ItemStack stack = ModItems.BLOOD_BOTTLE.get().getDefaultInstance();
                stack.getOrCreateTag().putInt("AGE", age);
                stack.setDamageValue(4);
                player.addItem(stack);
                VampirePlayer.getOpt(player).ifPresent(vamp -> vamp.removeBlood(0.5f));
            }

        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer player && CommonConfig.deathReset.get()) {
            getAge(player).ifPresent(age -> {
                age.setAge(0);
                syncAgeCap(player);
            });
        }
        LivingEntity dead = event.getEntity();
        if(!dead.getCommandSenderWorld().isClientSide && event.getSource().getEntity() instanceof ServerPlayer player ) {
            if (CommonConfig.sireingMechanic.get() && player.getOffhandItem().is(Items.GLASS_BOTTLE) && Helper.isVampire(dead) && (dead instanceof AdvancedVampireEntity || dead instanceof Player)) {
                int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
                ItemStack offHandStack = player.getOffhandItem();
                offHandStack.shrink(1);
                ItemStack stack = ModItems.BLOOD_BOTTLE.get().getDefaultInstance();
                stack.getOrCreateTag().putInt("AGE", age);
                stack.setDamageValue(1);
                player.addItem(stack);
            }
        }
        if(dead.getPersistentData().contains("AGE")) {
            dead.getPersistentData().remove("AGE");
        }
    }
    @SubscribeEvent
        public static void tooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.is(ModItems.BLOOD_BOTTLE.get()) && CommonConfig.sireingMechanic.get()) {
            if(stack.getOrCreateTag().contains("AGE")) {
                int age = stack.getOrCreateTag().getInt("AGE");
                event.getToolTip().add(Component.translatable("text.vampiricageing.blood_rank").append(String.valueOf(age)));
            }
        }
    }
    @SubscribeEvent
    public static void useItem(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if(CommonConfig.sireingMechanic.get() && stack.getOrCreateTag().contains("AGE") && entity instanceof Player player && Helper.isVampire(entity) && event.getItem().is(ModItems.BLOOD_BOTTLE.get())) {
            int age = stack.getOrCreateTag().getInt("AGE");
            int ageRank = getAge(entity).map(ageCap -> ageCap.getAge()).orElse(0);
            stack.getOrCreateTag().remove("AGE");
            if(age > 0 && age > ageRank) {
                getAge(entity).ifPresent(ageCap -> {
                    ageCap.setAge(age);
                    syncAgeCap(player);
                });
            }

        }
    }



    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if(event.player.tickCount % 100 == 0 && event.player instanceof ServerPlayer player && CommonConfig.timeBasedIncrease.get()) {
            if(canAge(player) && Helper.isVampire(player)) {
                getAge(player).ifPresent(age -> {
                    age.setTime(age.getTime() + 100);
                    syncAgeCap(player);
                    if(shouldIncreaseRankTicks(player)) {
                        increaseAge(player);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            syncAgeCap(event.getEntity());
        }
    }


    @SubscribeEvent
    public static void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            syncAgeCap(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            syncAgeCap(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof ServerPlayer) {
            syncAgeCap(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onChangeFaction(PlayerFactionEvent.FactionLevelChanged event) {

        if(event.getNewLevel() == 0 || (event.getCurrentFaction() != event.getOldFaction() && event.getOldFaction() != null)) {
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(0);
                syncAgeCap(event.getPlayer().getPlayer(), event.getOldFaction());
                Player player = event.getPlayer().getPlayer();
                if(event.getOldFaction() == VReference.HUNTER_FACTION && player.hasEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get())) {
                    player.removeEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get());
                }
            });
        } else if (event.getNewLevel() > 0 && event.getCurrentFaction() == VReference.VAMPIRE_FACTION && CommonConfig.sireingMechanic.get() && event.getPlayer().getPlayer().getPersistentData().contains("AGE")) {
            int sireAge = event.getPlayer().getPlayer().getPersistentData().getInt("AGE");
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(sireAge);
                syncAgeCap(event.getPlayer().getPlayer());
                event.getPlayer().getPlayer().getPersistentData().remove("AGE");
            });
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if(Helper.isVampire(event.getEntity())) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);

            if(event.getSource().is(ModDamageTypes.SUN_DAMAGE)) {
                event.setAmount(event.getAmount() / CommonConfig.sunDamageReduction.get().get(age).floatValue());
            } else if(event.getSource().is(ModDamageTypes.VAMPIRE_IN_FIRE) || event.getSource().is(ModDamageTypes.VAMPIRE_ON_FIRE)  || event.getSource().is(ModDamageTypes.HOLY_WATER) ) {
                if(event.getEntity() instanceof Player player && CommonConfig.rageModeWeaknessToggle.get() && VampirePlayer.getOpt(player).map(vamp -> vamp.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())).orElse(false) && CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue() < 1) {
                    return;
                }
                event.setAmount(event.getAmount() / CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue());
            } else if(event.getSource().is(DamageTypes.STARVE) && CommonConfig.harsherOutOfBlood.get() && age > 0) {
                event.setAmount(event.getAmount() * age);
            } else if(event.getSource().getEntity() != null && event.getSource().getEntity().getType().is(ModTags.Entities.HUNTER) && CommonConfig.shouldAgeIncreaseHunterMobDamage.get()) {
                event.setAmount(event.getAmount() * CommonConfig.damageMultiplierFromHunters.get().get(age));
            }

        }
    }
    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if(CommonConfig.shouldAgeAffectHealing.get() && Helper.isVampire(event.getEntity())) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() * CommonConfig.ageHealingMultiplier.get().get(age));
        }

    }
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if(source instanceof Player player && Helper.isVampire(player)) {
            VampirePlayer.getOpt(player).ifPresent(vamp -> {
                if(vamp.getActionHandler().isActionActive(VampiricAgeingActions.DRAIN_BLOOD_ACTION.get())) {
                    IVampirePlayer.BITE_TYPE biteType = vamp.determineBiteType(target);
                    int blood = 0;
                    float saturationMod = 1.0F;
                    if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_PLAYER) {
                        blood = VampirePlayer.getOpt((Player) target).map(v -> v.onBite(vamp)).orElse(0);
                        saturationMod = VampirePlayer.getOpt((Player) target).map(VampirePlayer::getBloodSaturation).orElse(0f);
                        vamp.drinkBlood(blood, saturationMod);
                    } else if (biteType == IVampirePlayer.BITE_TYPE.HUNTER_CREATURE && target instanceof Player targetPlayer) {
                        targetPlayer.getFoodData().addExhaustion(1f);
                        vamp.drinkBlood(1, 0.1f);

                    } else if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_CREATURE) {
                        LazyOptional<IExtendedCreatureVampirism> opt = ExtendedCreature.getSafe(target);
                        blood = opt.map((creature) -> {
                            return creature.onBite(vamp);
                        }).orElse(0);
                        saturationMod = opt.map(IBiteableEntity::getBloodSaturation).orElse(0.0F);

                        vamp.drinkBlood(blood, saturationMod);
                    }
                }
            });

        }
        //Advanced Vampires dont seem to come with the Goal to bite entities unlike Basic Vampires, not sure if intentional
        //Cant get adding the same goals on level join to work properly
        //saves out on a mixin at least
        if(source instanceof AdvancedVampireEntity vamp && CommonConfig.sireingMechanic.get() && target instanceof ServerPlayer player && Helper.canBecomeVampire(player)) {
            getAge(vamp).ifPresent(vampireAge -> {
                if (vampireAge.getAge() > 1 && vamp.getRandom().nextFloat() > 0.85) {
                    SanguinareEffect.addRandom(player, true);
                    player.getPersistentData().remove("AGE");
                    player.getPersistentData().putInt("AGE", vampireAge.getAge() - 1);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof AdvancedVampireEntity vamp && CommonConfig.advancedVampireAge.get() && !event.getEntity().getCommandSenderWorld().isClientSide) {
            getAge(vamp).ifPresent(vampireAge -> {
                if(vampireAge.getAge() != 0) {
                    return;
                }
                List<? extends Double> percentages = CommonConfig.percentageAdvancedVampireAges.get();
                double random = vamp.getRandom().nextDouble();
                //im tired but i think this works right?
                if(random <= percentages.get(0)) {
                    vampireAge.setAge(1);
                } else if ((random <= percentages.get(1) + percentages.get(0)) && random > percentages.get(0)) {
                    vampireAge.setAge(2);
                } else if (random <= percentages.get(2) + percentages.get(1) && random > percentages.get(1)) {
                    vampireAge.setAge(3);
                } else if (random <= percentages.get(3) + percentages.get(2) && random > percentages.get(2)) {
                    vampireAge.setAge(4);
                } else if(random <= percentages.get(4) + percentages.get(3) && random > percentages.get(3)) {
                    vampireAge.setAge(5);
                } else {
                    vampireAge.setAge(0);
                    return;
                }
                float ageMultiplier = Math.min(1, (float) vampireAge.getAge() / 2);
                vamp.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH_UUID, "AGE_VAMPIRE_HEALTH_INCREASE", ageMultiplier, AttributeModifier.Operation.MULTIPLY_BASE));
                vamp.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE_UUID, "AGE_VAMPIRE_DAMAGE_INCREASE", ageMultiplier, AttributeModifier.Operation.MULTIPLY_BASE));
                vamp.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, "AGE_VAMPIRE_KNOCKBACK_RESISTANCE", 0.25 * ageMultiplier, AttributeModifier.Operation.ADDITION));
                vamp.setHealth(vamp.getMaxHealth());
                if(vampireAge.getAge() > 2) {
                    vamp.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(AGE_ADVANCED_SPEED_INCREASE, "AGE_VAMPIRE_SPEED_INCREASE", 0.2 * ageMultiplier, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            });
        }
    }


    public static void syncAgeCap(Player player) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundTag tag = cap.serializeNBT();
        if(player instanceof ServerPlayer serverPlayer){
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
            onAgeChange(serverPlayer, null);
        }
    }
    public static void syncAgeCap(Player player, IPlayableFaction<?> faction) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundTag tag = cap.serializeNBT();
        if(player instanceof ServerPlayer serverPlayer){
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
            onAgeChange(serverPlayer, faction);
        }
    }
    public static void syncAgeCapNoChange(Player player) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundTag tag = cap.serializeNBT();
        if(player instanceof ServerPlayer serverPlayer){
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
        }
    }

}
