package com.doctor.vampiricageing.capabilities;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.networking.Networking;
import com.doctor.vampiricageing.networking.SyncCapabilityPacket;
import com.doctor.vampiricageing.skills.VampiricAgeingSkills;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class VampiricAgeingCapabilityManager {
    public static final ResourceLocation AGEING_KEY = new ResourceLocation(VampiricAgeing.MODID, "ageing");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("060749f1-7868-4fdf-8a54-eb5ddf93742e");
    public static final UUID MAX_HEALTH_UUID = UUID.fromString("08251d58-2513-4768-b4b5-f2a1a239998e");
    public static final UUID EXHAUSTION_UUID = UUID.fromString("1f14dd76-7d9b-47b3-9951-1c221f78d49f");
    public static final UUID STEP_ASSIST_UUID = UUID.fromString("edee6b7f-755a-4dc5-a036-2b8108415c4c");
    public static LazyOptional<IAgeingCapability> getAge(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return LazyOptional.empty();
        }
        return livingEntity.getCapability(AGEING_CAPABILITY);
    }

    public static final Capability<IAgeingCapability> AGEING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean canAge(LivingEntity entity) {
        if(entity instanceof Player player && entity.isAlive() && de.teamlapen.vampirism.util.Helper.isVampire(player) && !entity.getCommandSenderWorld().isClientSide) {
            int level = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
            int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            return level >= CommonConfig.levelToBeginAgeMechanic.get() && age < 5;
        }
        return false;
    }

    public static boolean shouldIncreaseRankTicks(Player player) {
        return getAge(player).map(age -> age.getTime() >= CommonConfig.ticksForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static boolean shouldIncreaseRankInfected(Player player) {
            return getAge(player).map(age -> age.getInfected() >= CommonConfig.infectedForNextAge.get().get(age.getAge())).orElse(false);
    }

    public static void increaseAge(ServerPlayer player) {
        if(canAge(player)) {
            getAge(player).ifPresent(age -> {
                age.setAge(age.getAge() + 1);
                syncAgeCap(player);
            });
        }
    }
    public static void onAgeChange(ServerPlayer player) {
        int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        removeModifier(player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()), EXHAUSTION_UUID);
        removeModifier(player.getAttribute(Attributes.MAX_HEALTH), MAX_HEALTH_UUID);
        removeModifier(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()), STEP_ASSIST_UUID);
        if(age > 0 && age >= CommonConfig.stepAssistBonus.get()) {
            player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()).addPermanentModifier(new AttributeModifier(STEP_ASSIST_UUID, "AGE_STEP_ASSIST_CHANGE", 0.5, AttributeModifier.Operation.ADDITION));
        }
        if(age >= CommonConfig.drainBloodActionRank.get()) {
            VampirePlayer.getOpt(player).ifPresent(vamp -> vamp.getSkillHandler().enableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get()));
        } else {
            VampirePlayer.getOpt(player).ifPresent(vamp -> vamp.getSkillHandler().disableSkill(VampiricAgeingSkills.BLOOD_DRAIN_SKILL.get()));
        }
        player.getAttribute(ModAttributes.BLOOD_EXHAUSTION.get()).addPermanentModifier(new AttributeModifier(EXHAUSTION_UUID, "AGE_EXHAUSTION_CHANGE", CommonConfig.ageExhaustionEffect.get().get(age), AttributeModifier.Operation.MULTIPLY_TOTAL));
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH_UUID, "MAX_HEALTH_AGE_CHANGE", CommonConfig.maxHealthIncrease.get().get(age), AttributeModifier.Operation.ADDITION));

    }

    private static void removeModifier(@NotNull AttributeInstance att, @NotNull UUID uuid) {
        AttributeModifier m = att.getModifier(uuid);
        if (m != null) {
            att.removeModifier(m);
        }
    }
    public static void incrementInfected(ServerPlayer player) {
        getAge(player).ifPresent(age -> {
            age.setInfected(age.getInfected() + 1);
            VampirePlayer.getOpt(player).ifPresent(vamp -> HelperLib.sync(vamp, age.serializeNBT(), player, false));
            if(shouldIncreaseRankInfected(player)) {
                increaseAge(player);
            }
        });
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        final AgeingCapabilityProvider provider = new AgeingCapabilityProvider();
        if(event.getObject() instanceof LivingEntity ) {
            event.addCapability(AGEING_KEY, provider);
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IAgeingCapability.class);
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
    public static void onCoffinInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if(!player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof CoffinBlock && canAge(player)) {
            int age = getAge(player).orElse(null).getAge();
            if(CommonConfig.timeBasedIncrease.get()) {
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_ticks").append(String.valueOf(CommonConfig.ticksForNextAge.get().get(age) / 20)).append(Component.translatable("text.vampiricageing.progress_ticks_end")).withStyle(ChatFormatting.DARK_RED));

            }
            if(CommonConfig.biteBasedIncrease.get()) {
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_infected").append(String.valueOf(CommonConfig.infectedForNextAge.get().get(age))).append(Component.translatable("text.vampiricageing.progress_infected_end")).withStyle(ChatFormatting.DARK_RED));
            }

        }
    }
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof ServerPlayer player && Helper.isVampire(event.getEntity()) && CommonConfig.deathReset.get()) {
            getAge(player).ifPresent(age -> {
                age.setAge(0);
                syncAgeCap(player);
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if(event.player.tickCount % 100 == 0 && canAge(event.player) && CommonConfig.timeBasedIncrease.get()) {
            ServerPlayer player = (ServerPlayer) event.player;
            getAge(player).ifPresent(age -> {
                age.setTime(age.getAge() + 100);
                if(shouldIncreaseRankTicks(player)) {
                    increaseAge(player);
                }
            });
        }
        if(event.player.isAlive() && !event.player.getCommandSenderWorld().isClientSide && event.player.getRandom().nextFloat() <= 0.25 && event.player.tickCount % 18000 == 0 && getAge(event.player).orElse(null).getAge() > 3 && CommonConfig.highAgeBadOmen.get()) {
            event.player.sendSystemMessage(Component.translatable("text.vampiricageing.bad_omen"));
            event.player.addEffect(new MobEffectInstance(ModEffects.BAD_OMEN_HUNTER.get(), 1, 36000));
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
        if(event.getNewLevel() == 0) {
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(0);
                age.setTime(0);
                age.setInfected(0);
                syncAgeCap(event.getPlayer().getPlayer());
            });
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if(Helper.isVampire(event.getEntity()) && event.getSource() == VReference.SUNDAMAGE) {;
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() / CommonConfig.sunDamageReduction.get().get(age));
        } else if(Helper.isVampire(event.getEntity()) && (event.getSource() == VReference.VAMPIRE_IN_FIRE || event.getSource() == VReference.VAMPIRE_ON_FIRE || event.getSource() == VReference.HOLY_WATER || event.getSource() == VReference.NO_BLOOD)) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            event.setAmount(event.getAmount() / CommonConfig.genericVampireWeaknessReduction.get().get(age));
        }
    }
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if(source instanceof ServerPlayer player && Helper.isVampire(player)) {
            VampirePlayer vamp = VampirePlayer.get(player);
            if(vamp.getActionHandler().isActionActive(VampiricAgeingActions.DRAIN_BLOOD_ACTION.get())) {
                IVampirePlayer.BITE_TYPE biteType = vamp.determineBiteType(target);
                int blood = 0;
                float saturationMod = 1.0F;
                if(biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_PLAYER) {
                    blood = VampirePlayer.getOpt((Player) target).map(v -> v.onBite(vamp)).orElse(0);
                    saturationMod = VampirePlayer.getOpt((Player) target).map(VampirePlayer::getBloodSaturation).orElse(0f);

                } else if (biteType == IVampirePlayer.BITE_TYPE.SUCK_BLOOD_CREATURE) {
                    LazyOptional<IExtendedCreatureVampirism> opt = ExtendedCreature.getSafe(target);
                    blood = opt.map((creature) -> {
                        return creature.onBite(vamp);
                    }).orElse(0);
                    saturationMod = opt.map(IBiteableEntity::getBloodSaturation).orElse(0.0F);
                }
                vamp.drinkBlood(blood, saturationMod);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof AdvancedVampireEntity vamp && CommonConfig.advancedVampireAge.get() && !event.getEntity().getCommandSenderWorld().isClientSide) {
            getAge(vamp).ifPresent(vampireAge -> {
                if(vampireAge.getAge() != 0) {
                    return;
                }
                float random = vamp.getRandom().nextFloat();
                if(random <= 0.5) {
                    vampireAge.setAge(1);
                } else if (random <= 0.8 && random > 0.5) {
                    vampireAge.setAge(2);
                } else if (random <= 0.95 && random > 0.8) {
                    vampireAge.setAge(3);
                } else if (random <= 0.985 && random > 0.95) {
                    vampireAge.setAge(4);
                } else if(random <= 1 && random > 0.985) {
                    vampireAge.setAge(5);
                }
                float ageMultiplier = Math.min(1, (float) vampireAge.getAge() / 2);
                vamp.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(MAX_HEALTH_UUID, "AGE_VAMPIRE_HEALTH_INCREASE", ageMultiplier, AttributeModifier.Operation.MULTIPLY_BASE));
                vamp.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE_UUID, "AGE_VAMPIRE_DAMAGE_INCREASE", ageMultiplier, AttributeModifier.Operation.MULTIPLY_BASE));
                vamp.setHealth(vamp.getMaxHealth());
            });
        }
    }

    public static void syncAgeCap(Player player) {
        IAgeingCapability cap = getAge(player).orElse(new AgeingCapability());
        CompoundTag tag = cap.serializeNBT();
        if(player instanceof ServerPlayer serverPlayer){
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncCapabilityPacket(tag));
            onAgeChange(serverPlayer);
        }
    }

}
