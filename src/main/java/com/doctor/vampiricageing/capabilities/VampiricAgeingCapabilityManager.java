package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.networking.Networking;
import com.doctor.vampiricageing.networking.SyncCapabilityPacket;
import com.doctor.vampiricageing.skills.VampiricAgeingSkills;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.event.FactionEvent;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.werewolves.util.WReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;
import java.util.UUID;

import static de.teamlapen.vampirism.util.Helper.isHunter;
import static de.teamlapen.vampirism.util.Helper.isVampire;

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
    @CapabilityInject(IAgeingCapability.class)
    public static final Capability<IAgeingCapability> AGEING_CAPABILITY = null;

    public static boolean canAge(LivingEntity entity) {
        if(entity instanceof ServerPlayerEntity && entity.isAlive()) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
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


    public static boolean shouldIncreaseRankTicks(PlayerEntity player) {
        return getAge(player).map(age -> age.getTime() >= CommonConfig.ticksForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static boolean shouldIncreaseRankInfected(PlayerEntity player) {
        return getAge(player).map(age -> age.getInfected() >= CommonConfig.infectedForNextAge.get().get(age.getAge())).orElse(false);
    }
    public static boolean shouldIncreaseRankDrained(PlayerEntity player) {
        return getAge(player).map(age -> age.getDrained() >= CommonConfig.drainedForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static void increaseAge(ServerPlayerEntity player) {
        if(canAge(player)) {
            getAge(player).ifPresent(age -> {
                if(Helper.isVampire(player)) {
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundCategory.PLAYERS, 1, 1);
                ModParticles.spawnParticlesServer(player.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
                }
                age.setAge(age.getAge() + 1);
                if(Helper.isHunter(player) && player.hasEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get())) {
                    player.removeEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get());
                }
                syncAgeCap(player);
            });
        }
    }
    public static void changeUpStep(PlayerEntity player) {
        int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        boolean upstep = getAge(player).map(ageCap -> ageCap.getUpStep()).orElse(false);
        if(Helper.isVampire(player)) {
            if (upstep && age < CommonConfig.stepAssistBonus.get()) {
                player.maxUpStep = 0.6f;
                getAge(player).ifPresent(ageCap -> ageCap.setUpStep(false));
            }
            if (age > 0 && age >= CommonConfig.stepAssistBonus.get() && !upstep) {
                player.maxUpStep = 1f;
                getAge(player).ifPresent(ageCap -> ageCap.setUpStep(true));
            }
        } else if(Helper.isHunter(player)) {
            if(upstep && age < HunterAgeingConfig.stepAssistAge.get()) {
                player.maxUpStep = 0.6f;
                getAge(player).ifPresent(ageCap -> ageCap.setUpStep(false));
            }
            if(age > 0 && age >= HunterAgeingConfig.stepAssistAge.get() && !upstep) {
                player.maxUpStep = 1f;
                getAge(player).ifPresent(ageCap -> ageCap.setUpStep(true));
            }
        }
    }
    public static void onAgeChange(ServerPlayerEntity player, IPlayableFaction<?> faction) {
        int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        checkSkills(age, player);
        changeUpStep(player);
        if(Helper.isVampire(player) || faction == VReference.VAMPIRE_FACTION) {
            removeModifier(player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()), EXHAUSTION_UUID);
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), MAX_HEALTH_UUID);
            removeModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), STRENGTH_INCREASE);
            if(CommonConfig.shouldAgeAffectExhaustion.get()) {
                player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).addPermanentModifier(new AttributeModifier(EXHAUSTION_UUID, "AGE_EXHAUSTION_CHANGE", CommonConfig.ageExhaustionEffect.get().get(age), AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(STRENGTH_INCREASE, "AGE_ATTACK_DAMAGE_INCREASE", CommonConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH_UUID, "MAX_HEALTH_AGE_CHANGE", CommonConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
        } else if(CapabilityHelper.isWerewolfCheckMod(player, faction)) {
            removeModifier(player.getAttribute(de.teamlapen.werewolves.core.ModAttributes.BITE_DAMAGE.get()), BITE_DAMAGE_MULTIPLIER_UUID);
            removeModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), WEREWOLF_STRENGTH_INCREASE);
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), WEREWOLF_MAX_HEALTH_AGE_UUID);

            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(WEREWOLF_STRENGTH_INCREASE, "WEREWOLF_AGE_ATTACK_DAMAGE_INCREASE", WerewolvesAgeingConfig.ageDamageIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(WEREWOLF_MAX_HEALTH_AGE_UUID, "WEREWOLF_MAX_HEALTH_AGE_CHANGE", WerewolvesAgeingConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));
            player.getAttribute(de.teamlapen.werewolves.core.ModAttributes.BITE_DAMAGE.get()).addPermanentModifier(new AttributeModifier(BITE_DAMAGE_MULTIPLIER_UUID, "AGE_BITE_DAMAGE_INCREASE", WerewolvesAgeingConfig.biteDamageMultiplier.get().get(age), AttributeModifier.Operation.MULTIPLY_TOTAL));
        } else if(isHunter(player) || faction == VReference.HUNTER_FACTION) {
            removeModifier(player.getAttribute(Attributes.MAX_HEALTH), HUNTER_MAX_HEALTH_UUID);
            removeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), HUNTER_SPEED_INCREASE_UUID);
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
    public static void checkSkills(int age, ServerPlayerEntity player) {
        if(Helper.isVampire(player)) {
            VampirePlayer.getOpt(player).ifPresent(vamp -> {
                ISkillHandler<IVampirePlayer> handler = vamp.getSkillHandler();
                if(age >= CommonConfig.drainBloodActionRank.get()) {
                    handler.enableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
                } else {
                    handler.disableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get());
                }
                if(age >= CommonConfig.celerityActionRank.get()) {
                    handler.enableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
                } else {
                    handler.disableSkill(VampiricAgeingSkills.CELERTIY_ACTION.get());
                }
            });
        }  else if(Helper.isHunter(player)) {
            HunterPlayer.getOpt(player).ifPresent(hunter -> {
                ISkillHandler<IHunterPlayer> handler = hunter.getSkillHandler();

                if(age >= HunterAgeingConfig.taintedBloodBottleAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
                } else {
                    handler.disableSkill(VampiricAgeingSkills.TAINTED_BLOOD_SKILL.get());
                }
                int cumulativeAge = CapabilityHelper.getCumulativeTaintedAge(player);
                if(cumulativeAge >= HunterAgeingConfig.hunterTeleportActionAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
                } else {
                    handler.disableSkill(VampiricAgeingSkills.HUNTER_TELEPORT_SKILL.get());
                }

                if(cumulativeAge >= HunterAgeingConfig.limitedBatModeAge.get()) {
                    handler.enableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
                } else {
                    handler.disableSkill(VampiricAgeingSkills.LIMITED_BAT_MODE_SKILL.get());
                }

            });
        }
    }

    public static void removeModifier(ModifiableAttributeInstance att, UUID uuid) {
        AttributeModifier m = att.getModifier(uuid);
        if (m != null) {
            att.removeModifier(m);
        }
    }
    public static void incrementInfected(ServerPlayerEntity player) {
        getAge(player).ifPresent(age -> {
            age.setInfected(age.getInfected() + 1);
            syncAgeCap(player);
            if(shouldIncreaseRankInfected(player)) {
                increaseAge(player);
            }
        });
    }
    public static void incrementDrained(ServerPlayerEntity player) {
        getAge(player).ifPresent(age -> {
            age.setDrained(age.getDrained() + 1);
            syncAgeCap(player);
            if(shouldIncreaseRankDrained(player)) {
                increaseAge(player);
            }
        });
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        final AgeingCapabilityProvider provider = new AgeingCapabilityProvider();
        if(event.getObject() instanceof PlayerEntity || event.getObject() instanceof AdvancedVampireEntity || event.getObject() instanceof AdvancedVampireEntity.IMob ) {
            event.addCapability(AGEING_KEY, provider);
        }
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IAgeingCapability.class, new Storage(), AgeingCapability::new);
    }
    @SubscribeEvent
    public static void onPotionEffectRemove(PotionEvent.PotionRemoveEvent event) {
        if(CommonConfig.sireingMechanic.get() && event.getPotion() == ModEffects.SANGUINARE.get() && event.getEntity() instanceof PlayerEntity && !event.getEntity().getCommandSenderWorld().isClientSide) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            player.getPersistentData().remove("AGE");
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
                getAge(event.getOriginal()).ifPresent(oldAge -> {
                    IAgeingCapability newAgeCap = getAge(event.getPlayer()).orElse(new AgeingCapability());
                    CompoundNBT ageTag = oldAge.serializeNBT();
                    newAgeCap.deserializeNBT(ageTag);
                    syncAgeCap(event.getPlayer());
                });
    }

    @SubscribeEvent
    public static void onCoffinInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        if(Helper.isVampire(player) && !player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof CoffinBlock && canAge(player)) {
            int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            if(CommonConfig.timeBasedIncrease.get()) {
                int ticksAlive = getAge(event.getPlayer()).map(ageCap -> ageCap.getTime()).orElse(0);
                int secondsForNextAge = (CommonConfig.ticksForNextAge.get().get(age) - ticksAlive) / 20;
                player.sendMessage(new TranslationTextComponent("text.vampiricageing.progress_ticks", secondsForNextAge).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
            }
            if(CommonConfig.biteBasedIncrease.get()) {
                int infected = getAge(event.getPlayer()).map(ageCap -> ageCap.getInfected()).orElse(0);
                int infectedForNextAge = CommonConfig.infectedForNextAge.get().get(age) - infected;
                player.sendMessage(new TranslationTextComponent("text.vampiricageing.progress_infected", infectedForNextAge).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
            }
            if(CommonConfig.drainBasedIncrease.get()) {
                int drained = getAge(event.getPlayer()).map(ageCap -> ageCap.getDrained()).orElse(0);
                int drainedForNextAge = CommonConfig.drainedForNextAge.get().get(age) - drained;
                player.sendMessage(new TranslationTextComponent("text.vampiricageing.progress_drained", drainedForNextAge).withStyle(TextFormatting.DARK_RED), Util.NIL_UUID);
            }
        }
    }
    @SubscribeEvent
    public static void sireBloodInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        if(player.isShiftKeyDown() && CommonConfig.sireingMechanic.get() && Helper.isVampire(player) && event.getHand() == Hand.MAIN_HAND && event.getItemStack().getItem() == Items.GLASS_BOTTLE) {
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
        if(event.getEntity() instanceof ServerPlayerEntity && CommonConfig.deathReset.get()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            getAge(player).ifPresent(age -> {
                age.setAge(0);
                syncAgeCap(player);
            });
        }
        LivingEntity dead = event.getEntityLiving();
        if(!dead.getCommandSenderWorld().isClientSide && event.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getSource().getEntity();
            if (CommonConfig.sireingMechanic.get() && player.getOffhandItem().getItem() == Items.GLASS_BOTTLE && Helper.isVampire(dead) && (dead instanceof AdvancedVampireEntity || dead instanceof PlayerEntity)) {
                int age = getAge(dead).map(ageCap -> ageCap.getAge()).orElse(0);
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
        if(stack.getItem() == ModItems.BLOOD_BOTTLE.get() && CommonConfig.sireingMechanic.get()) {
            if(stack.getOrCreateTag().contains("AGE")) {
                int age = stack.getOrCreateTag().getInt("AGE");
                event.getToolTip().add(new TranslationTextComponent("text.vampiricageing.blood_rank").append(String.valueOf(age)));
            }
        }
    }
    @SubscribeEvent
    public static void useItem(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack stack = event.getItem();
        if(CommonConfig.sireingMechanic.get() && stack.getOrCreateTag().contains("AGE") && entity instanceof PlayerEntity && Helper.isVampire(entity) && event.getItem().getItem() == ModItems.BLOOD_BOTTLE.get()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
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
        if(event.player.level.getGameTime() % 100 == 0 && event.player instanceof ServerPlayerEntity && CommonConfig.timeBasedIncrease.get()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
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
        if(event.player.isAlive() && !event.player.getCommandSenderWorld().isClientSide && event.player.getRandom().nextFloat() <= 0.25 && event.player.level.getGameTime() % 18000 == 0 && getAge(event.player).orElse(null).getAge() > 3 && CommonConfig.highAgeBadOmen.get()) {
            event.player.sendMessage(new TranslationTextComponent("text.vampiricageing.bad_omen"), Util.NIL_UUID);
            event.player.addEffect(new EffectInstance(ModEffects.BAD_OMEN_HUNTER.get(), 1, 36000));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            syncAgeCap((PlayerEntity) event.getEntity());
        }
    }


    @SubscribeEvent
    public static void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            syncAgeCap((PlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            syncAgeCap((PlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayerEntity ) {
            syncAgeCap((PlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onChangeFaction(FactionEvent.FactionLevelChanged event) {

        if(event.getNewLevel() == 0 || (event.getCurrentFaction() != event.getOldFaction() && event.getOldFaction() != null)) {
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(0);
                IPlayableFaction<?> faction = event.getOldFaction();
                syncAgeCap(event.getPlayer().getPlayer(), faction);
                PlayerEntity player = event.getPlayer().getPlayer();
                if(Helper.isHunter(player) && player.hasEffect(com.doctor.vampiricageing.init.ModEffects.TAINTED_BLOOD_EFFECT.get())) {
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
        if(Helper.isVampire(event.getEntityLiving())) {
            int age = getAge(event.getEntityLiving()).map(ageCap -> ageCap.getAge()).orElse(0);

            if(event.getSource() == VReference.SUNDAMAGE) {
                event.setAmount(event.getAmount() / CommonConfig.sunDamageReduction.get().get(age).floatValue());
            } else if(event.getSource() == VReference.VAMPIRE_IN_FIRE || event.getSource() == VReference.VAMPIRE_ON_FIRE || event.getSource() == VReference.HOLY_WATER) {
                if(event.getEntityLiving() instanceof PlayerEntity&& CommonConfig.rageModeWeaknessToggle.get() && VampirePlayer.getOpt((PlayerEntity) event.getEntityLiving()).map(vamp -> vamp.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())).orElse(false) && CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue() < 1) {
                    return;
                }
                event.setAmount(event.getAmount() / CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue());
            } else if(event.getSource() == DamageSource.STARVE && CommonConfig.harsherOutOfBlood.get() && age > 0) {
                event.setAmount(event.getAmount() * age);
            } else if(event.getSource().getEntity() != null && event.getSource().getEntity().getType().is(ModTags.Entities.HUNTER) && CommonConfig.shouldAgeIncreaseHunterMobDamage.get()) {
                event.setAmount(event.getAmount() * CommonConfig.damageMultiplierFromHunters.get().get(age));
            }

        }
    }
    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if(CommonConfig.shouldAgeAffectHealing.get() && Helper.isVampire(event.getEntity())) {
            int age = getAge(event.getEntityLiving()).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() * CommonConfig.ageHealingMultiplier.get().get(age));
        }

    }
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntityLiving();
        Entity source = event.getSource().getEntity();
        if(source instanceof PlayerEntity && Helper.isVampire(source)) {
            PlayerEntity player = (PlayerEntity) source;
            VampirePlayer.getOpt(player).ifPresent(vamp -> {
                if(vamp.getActionHandler().isActionActive(VampiricAgeingActions.DRAIN_BLOOD_ACTION.get())) {
                    IVampirePlayer.BITE_TYPE biteType = vamp.determineBiteType(target);
                    int blood = 0;
                    float saturationMod = 1.0F;
                    if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_PLAYER) {
                        blood = VampirePlayer.getOpt((PlayerEntity) target).map(v -> v.onBite(vamp)).orElse(0);
                        saturationMod = VampirePlayer.getOpt((PlayerEntity) target).map(VampirePlayer::getBloodSaturation).orElse(0f);
                        vamp.drinkBlood(blood, saturationMod);
                    } else if (biteType == IVampirePlayer.BITE_TYPE.HUNTER_CREATURE && target instanceof PlayerEntity) {
                        PlayerEntity targetPlayer = (PlayerEntity) target;
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
        if(source instanceof AdvancedVampireEntity && CommonConfig.sireingMechanic.get() && target instanceof ServerPlayerEntity && Helper.canBecomeVampire((PlayerEntity) target)) {
            ServerPlayerEntity player = (ServerPlayerEntity) target;
            AdvancedVampireEntity vamp = (AdvancedVampireEntity) source;
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
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof AdvancedVampireEntity && CommonConfig.advancedVampireAge.get() && !event.getEntity().getCommandSenderWorld().isClientSide) {
            AdvancedVampireEntity vamp = (AdvancedVampireEntity) event.getEntity();
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


    public static void syncAgeCap(PlayerEntity player) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundNBT tag = cap.serializeNBT();
        if(player instanceof ServerPlayerEntity ) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
            onAgeChange(serverPlayer, null);
        }
    }
    public static void syncAgeCap(PlayerEntity player, IPlayableFaction<?> faction) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundNBT tag = cap.serializeNBT();
        if(player instanceof ServerPlayerEntity ) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
            onAgeChange(serverPlayer, faction);
        }
    }
    private static class Storage implements Capability.IStorage<IAgeingCapability> {
        @Override
        public void readNBT(Capability<IAgeingCapability> capability, IAgeingCapability instance, Direction side, INBT nbt) {
            ((VampirePlayer) instance).loadData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IAgeingCapability> capability, IAgeingCapability instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((VampirePlayer) instance).saveData(nbt);
            return nbt;
        }
    }

}
