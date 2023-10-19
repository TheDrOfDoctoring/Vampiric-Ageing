package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


import java.util.UUID;

public class LimitedHunterBatModeAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    //This is essentially just the Vampire Bat Mode action but with a few minor tweaks to limit it.
    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntitySize BAT_SIZE = EntitySize.fixed(0.8f, 0.6f);

    private static final float PLAYER_WIDTH = 0.6F;
    private static final float PLAYER_HEIGHT = 1.8F;

    private final UUID armorModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91439053c");
    private final UUID armorToughnessModifierUUID = UUID.fromString("6d3df16d-85e4-4b99-b2fc-301818697a6d");

    @Override
    public boolean activate(IHunterPlayer hunter, ActivationContext context) {
        PlayerEntity player = hunter.getRepresentingPlayer();
        setModifier(player, true);
        updatePlayer((HunterPlayer) hunter, true);
        return true;
    }

    @Override
    public int getCooldown() {
        return HunterAgeingConfig.limitedBatModeCooldown.get() * 20;
    }
    @Override
    public int getDuration(IFactionPlayer player) {
        if(VampiricAgeingCapabilityManager.getAge(player.getRepresentingPlayer()).map(ageCap -> ageCap.isTransformed()).orElse(false)) {
            return MathHelper.clamp(HunterAgeingConfig.limitedBatModeDurationTransformed.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
        }
        return HunterAgeingConfig.limitedBatModeDuration.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterLimitedBatModeAction.get();
    }

    @Override
    public boolean canBeUsedBy(IHunterPlayer hunter) {
        return  CapabilityHelper.getCumulativeTaintedAge(hunter.getRepresentingPlayer()) >= HunterAgeingConfig.limitedBatModeAge.get()
                && !hunter.getRepresentingPlayer().isInWater()
                && !(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension() == World.END)
                && !shouldSunAffect(hunter.getRepresentingPlayer())
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString())
                && (hunter.getRepresentingEntity().getVehicle() == null);
    }

    public boolean shouldSunAffect(PlayerEntity player) {
        return Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && HunterAgeingConfig.sunAffectLimitedBatMode.get();
    }
    @Override
    public void onActivatedClient(IHunterPlayer hunter) {
        if (!VampiricAgeingCapabilityManager.getAge(hunter.getRepresentingEntity()).map(h -> h.getBatMode()).orElse(false)) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    @Override
    public void onDeactivated(IHunterPlayer hunter) {
        PlayerEntity player = hunter.getRepresentingPlayer();
        setModifier(player, false);
        if (!player.isOnGround()) {
            player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 20, 100, false, false));
        }
        updatePlayer((HunterPlayer) hunter, false);
    }

    @Override
    public boolean onUpdate(IHunterPlayer hunter) {
        if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString()) && hunter.getRepresentingPlayer().getCommandSenderWorld().dimension() == World.END) {
            hunter.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_dimension"), Util.NIL_UUID);
            return true;
        } else if(shouldSunAffect(hunter.getRepresentingPlayer()) && !hunter.isRemote()) {
            hunter.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_day"), Util.NIL_UUID);
            return true;
        } else {
            float exhaustion = HunterAgeingConfig.limitedBatExhaustion.get().floatValue();
            if (exhaustion > 0) hunter.getRepresentingPlayer().getFoodData().addExhaustion(exhaustion);
            return hunter.getRepresentingPlayer().isInWater();
        }
    }

    @Override
    public void onReActivated(IHunterPlayer hunter) {
        setModifier(hunter.getRepresentingPlayer(), true);
        if (!VampiricAgeingCapabilityManager.getAge(hunter.getRepresentingEntity()).map(h -> h.getBatMode()).orElse(false)) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    private void setModifier(PlayerEntity player, boolean enabled) {
        if (enabled) {
            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.abilities.mayfly = true;
            player.abilities.flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(armorModifierUUID);
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID);
            if (m2 != null) {
                armorToughnessAttributeInst.removeModifier(m2);
            }

            boolean spectator = player.isSpectator();
            boolean creative = player.isCreative();
            player.abilities.mayfly = spectator || creative;
            player.abilities.flying = spectator;

            setFlightSpeed(player, 0.05F);
        }
        player.onUpdateAbilities();

    }
    private void updatePlayer(HunterPlayer hunter, boolean bat) {
        PlayerEntity player = hunter.getRepresentingPlayer();
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(hntr -> hntr.setBatMode(bat));
        VampiricAgeingCapabilityManager.syncAgeCap(player);
        player.refreshDimensions();
        player.setPose(Pose.CROUCHING);
        player.setForcedPose(bat ? null : Pose.STANDING);
        if (bat) {
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height), player.getZ());
        }
    }
    private void setFlightSpeed(PlayerEntity player, float speed) {
        player.abilities.flyingSpeed = speed;
    }
    @Override
    public boolean showHudDuration(PlayerEntity player) {
        return true;
    }

}
