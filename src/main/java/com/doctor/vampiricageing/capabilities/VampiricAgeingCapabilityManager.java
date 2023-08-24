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
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.ai.goals.BiteNearbyEntityVampireGoal;
import de.teamlapen.vampirism.entity.ai.goals.MoveToBiteableVampireGoal;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.util.Helper;
import jdk.jfr.Percentage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = VampiricAgeing.MODID)
public class VampiricAgeingCapabilityManager {
    public static final ResourceLocation AGEING_KEY = new ResourceLocation(VampiricAgeing.MODID, "ageing");
    public static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("060749f1-7868-4fdf-8a54-eb5ddf93742e");
    public static final UUID MAX_HEALTH_UUID = UUID.fromString("08251d58-2513-4768-b4b5-f2a1a239998e");
    public static final UUID EXHAUSTION_UUID = UUID.fromString("1f14dd76-7d9b-47b3-9951-1c221f78d49f");
    public static final UUID STEP_ASSIST_UUID = UUID.fromString("edee6b7f-755a-4dc5-a036-2b8108415c4c");
    public static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("94d546a9-6848-48cf-bcba-5e162987d58b");
    public static LazyOptional<IAgeingCapability> getAge(LivingEntity livingEntity) {
        if (livingEntity == null) {
            return LazyOptional.empty();
        }
        return livingEntity.getCapability(AGEING_CAPABILITY);
    }

    public static final Capability<IAgeingCapability> AGEING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static boolean canAge(LivingEntity entity) {
        if(entity instanceof ServerPlayer player && entity.isAlive() && de.teamlapen.vampirism.util.Helper.isVampire(player)) {
            int level = FactionPlayerHandler.getOpt(player).map(fph -> fph.getCurrentLevel(VReference.VAMPIRE_FACTION)).orElse(0);
            int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            return (level >= CommonConfig.levelToBeginAgeMechanic.get() && age < 5);
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
                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.ENTITY_VAMPIRE_SCREAM.get(), SoundSource.PLAYERS, 1, 1);
                ModParticles.spawnParticlesServer(player.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "spell_1"), 50, 0x8B0000, 0.2F), player.getX(), player.getY(), player.getZ(), 100, 1, 1, 1, 0);
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
            syncAgeCap(player);
            if(shouldIncreaseRankInfected(player)) {
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
    public static void onCoffinInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if(!player.getCommandSenderWorld().isClientSide && player.getCommandSenderWorld().getBlockState(event.getPos()).getBlock() instanceof CoffinBlock && canAge(player)) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            int ticksAlive = getAge(event.getEntity()).map(ageCap -> ageCap.getTime()).orElse(0);
            int infected = getAge(event.getEntity()).map(ageCap -> ageCap.getInfected()).orElse(0);
            if(CommonConfig.timeBasedIncrease.get()) {
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_ticks").append(String.valueOf((CommonConfig.ticksForNextAge.get().get(age) - ticksAlive) / 20)).append(Component.translatable("text.vampiricageing.progress_ticks_end")).withStyle(ChatFormatting.DARK_RED));

            }
            if(CommonConfig.biteBasedIncrease.get()) {
                player.sendSystemMessage(Component.translatable("text.vampiricageing.progress_infected").append(String.valueOf(CommonConfig.infectedForNextAge.get().get(age) - infected)).append(Component.translatable("text.vampiricageing.progress_infected_end")).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }
    @SubscribeEvent
    public static void sireBloodInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if(player.isShiftKeyDown() && CommonConfig.sireingMechanic.get() && Helper.isVampire(player) && event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().is(Items.GLASS_BOTTLE)) {
            int age = getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
            if(age > 1 && VampirePlayer.get(player).getBloodLevel() > 8) {
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
        if(event.getEntity() instanceof ServerPlayer player && Helper.isVampire(event.getEntity()) && CommonConfig.deathReset.get()) {
            getAge(player).ifPresent(age -> {
                age.setAge(0);
                syncAgeCap(player);
            });
        }
        LivingEntity dead = event.getEntity();
        if(!dead.getCommandSenderWorld().isClientSide && event.getSource().getEntity() instanceof Player player && Helper.isVampire(dead) && CommonConfig.sireingMechanic.get()) {
            if(player.getOffhandItem().is(Items.GLASS_BOTTLE) && (dead instanceof AdvancedVampireEntity || dead instanceof Player)) {
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
            if(canAge(player)) {
                getAge(player).ifPresent(age -> {
                    age.setTime(age.getTime() + 100);
                    syncAgeCap(player);
                    if(shouldIncreaseRankTicks(player)) {
                        increaseAge(player);
                    }
                });
            }
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
        if(event.getNewLevel() == 0 || event.getCurrentFaction() != VReference.VAMPIRE_FACTION) {
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(0);
                age.setTime(0);
                age.setInfected(0);
                syncAgeCap(event.getPlayer().getPlayer());
            });
        } else if (event.getNewLevel() > 0 && event.getCurrentFaction() == VReference.VAMPIRE_FACTION && CommonConfig.sireingMechanic.get() && event.getPlayer().getPlayer().getPersistentData().contains("AGE")) {
            int sireAge = event.getPlayer().getPlayer().getPersistentData().getInt("AGE");
            getAge(event.getPlayer().getPlayer()).ifPresent(age -> {
                age.setAge(sireAge);
                age.setTime(0);
                age.setInfected(0);
                syncAgeCap(event.getPlayer().getPlayer());
                event.getPlayer().getPlayer().getPersistentData().remove("AGE");
            });
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if(Helper.isVampire(event.getEntity())) {
            int age = getAge(event.getEntity()).map(ageCap -> ageCap.getAge()).orElse(0);
            if(event.getSource() == VReference.SUNDAMAGE) {
                event.setAmount(event.getAmount() / CommonConfig.sunDamageReduction.get().get(age).floatValue());
            } else if(event.getSource() == VReference.VAMPIRE_IN_FIRE || event.getSource() == VReference.VAMPIRE_ON_FIRE || event.getSource() == VReference.HOLY_WATER || event.getSource() == VReference.NO_BLOOD) {
                event.setAmount(event.getAmount() / CommonConfig.genericVampireWeaknessReduction.get().get(age).floatValue());
            }
        }
    }
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Entity source = event.getSource().getEntity();
        if(source instanceof Player player && Helper.isVampire(player)) {
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
