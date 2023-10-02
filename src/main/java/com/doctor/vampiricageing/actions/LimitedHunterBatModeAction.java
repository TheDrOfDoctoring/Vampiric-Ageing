package com.doctor.vampiricageing.actions;

import com.doctor.vampiricageing.capabilities.CapabilityHelper;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LimitedHunterBatModeAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    //This is essentially just the Vampire Bat Mode action but with a few minor tweaks to limit it.
    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntityDimensions BAT_SIZE = EntityDimensions.fixed(0.8f, 0.6f);

    private static final float PLAYER_WIDTH = 0.6F;
    private static final float PLAYER_HEIGHT = 1.8F;

    private final UUID armorModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91439053c");
    private final UUID armorToughnessModifierUUID = UUID.fromString("6d3df16d-85e4-4b99-b2fc-301818697a6d");

    @Override
    public boolean activate(IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.getRepresentingPlayer();
        setModifier(player, true);
        updatePlayer((HunterPlayer) hunter, true);
        return true;
    }

    @Override
    public int getCooldown(IHunterPlayer hunter) {
        return HunterAgeingConfig.limitedBatModeCooldown.get() * 20;
    }
    @Override
    public int getDuration(IHunterPlayer hunter) {
        return HunterAgeingConfig.limitedBatModeDuration.get() * 20;
    }
    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterLimitedBatModeAction.get();
    }

    @Override
    public boolean canBeUsedBy(@NotNull IHunterPlayer hunter) {
        return  CapabilityHelper.getCumulativeTaintedAge(hunter.getRepresentingPlayer()) >= HunterAgeingConfig.limitedBatModeAge.get()
                && !hunter.getRepresentingPlayer().isInWater()
                && !(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension() == Level.END)
                && !shouldSunAffect(hunter.getRepresentingPlayer())
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString())
                && (hunter.getRepresentingEntity().getVehicle() == null);
    }

    public boolean shouldSunAffect(Player player) {
        return Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && HunterAgeingConfig.sunAffectLimitedBatMode.get();
    }
    @Override
    public void onActivatedClient(@NotNull IHunterPlayer hunter) {
        if (!VampiricAgeingCapabilityManager.getAge(hunter.getRepresentingEntity()).map(h -> h.getBatMode()).orElse(false)) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    @Override
    public void onDeactivated(@NotNull IHunterPlayer hunter) {
        Player player = hunter.getRepresentingPlayer();
        setModifier(player, false);
        if (!player.isOnGround()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 100, false, false));
        }
        updatePlayer((HunterPlayer) hunter, false);
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer hunter) {
        if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(hunter.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString()) && hunter.getRepresentingPlayer().getCommandSenderWorld().dimension() == Level.END) {
            hunter.getRepresentingPlayer().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_dimension"));
            return true;
        } else if(shouldSunAffect(hunter.getRepresentingPlayer()) && !hunter.isRemote()) {
            hunter.getRepresentingPlayer().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_day"));
            return true;
        } else {
            float exhaustion = HunterAgeingConfig.limitedBatExhaustion.get().floatValue();
            if (exhaustion > 0) hunter.getRepresentingPlayer().getFoodData().addExhaustion(exhaustion);
            return hunter.getRepresentingPlayer().isInWater();
        }
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer hunter) {
        setModifier(hunter.getRepresentingPlayer(), true);
        if (!VampiricAgeingCapabilityManager.getAge(hunter.getRepresentingEntity()).map(h -> h.getBatMode()).orElse(false)) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    private void setModifier(Player player, boolean enabled) {
        if (enabled) {
            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(armorModifierUUID);
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID);
            if (m2 != null) {
                armorToughnessAttributeInst.removeModifier(m2);
            }

            boolean spectator = player.isSpectator();
            boolean creative = player.isCreative();
            player.getAbilities().mayfly = spectator || creative;
            player.getAbilities().flying = spectator;

            setFlightSpeed(player, 0.05F);
        }
        player.onUpdateAbilities();

    }
    private void updatePlayer(@NotNull HunterPlayer hunter, boolean bat) {
        Player player = hunter.getRepresentingPlayer();
        VampiricAgeingCapabilityManager.getAge(player).ifPresent(hntr -> hntr.setBatMode(bat
        ));
        VampiricAgeingCapabilityManager.syncAgeCap(player);
        player.setForcedPose(bat ? Pose.STANDING : null);
        player.refreshDimensions();
        if (bat) {
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height), player.getZ());
        }
    }
    private void setFlightSpeed(@NotNull Player player, float speed) {
        player.getAbilities().flyingSpeed = speed;
    }
    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}
