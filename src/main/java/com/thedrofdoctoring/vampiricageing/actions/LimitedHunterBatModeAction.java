package com.thedrofdoctoring.vampiricageing.actions;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.capabilities.other.IHunterSpecialAttributes;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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

public class LimitedHunterBatModeAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    //This is essentially just the Vampire Bat Mode action but with a few minor tweaks to limit it.
    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntityDimensions BAT_SIZE = EntityDimensions.fixed(0.8f, 0.6f).withEyeHeight(BAT_EYE_HEIGHT);

    private static final float PLAYER_WIDTH = 0.6F;
    private static final float PLAYER_HEIGHT = 1.8F;


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
        if(AgeingManager.getAge(hunter.asEntity()).isTransformed()) {
            return Mth.clamp(HunterAgeingConfig.limitedBatModeDurationTransformed.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
        }
        return HunterAgeingConfig.limitedBatModeDuration.get() * 20;
    }
    @Override
    public boolean isEnabled() {
        return HunterAgeingConfig.hunterLimitedBatModeAction.get();
    }

    @Override
    public boolean canBeUsedBy(@NotNull IHunterPlayer hunter) {
        Player player = hunter.asEntity();
        return  CapabilityHelper.getCumulativeTaintedAge(player) >= HunterAgeingConfig.limitedBatModeAge.get()
                && !player.isInWater()
                && !(player.getCommandSenderWorld().dimension() == Level.END)
                && !shouldSunAffect(player)
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(player.getCommandSenderWorld().dimension().location().toString())
                && (player.getVehicle() == null);
    }

    public boolean shouldSunAffect(Player player) {
        return Helper.gettingSundamge(player, player.getCommandSenderWorld(), player.getCommandSenderWorld().getProfiler()) && HunterAgeingConfig.sunAffectLimitedBatMode.get();
    }
    @Override
    public void onActivatedClient(@NotNull IHunterPlayer hunter) {
        IHunterSpecialAttributes atts = (IHunterSpecialAttributes) ((HunterPlayer) hunter).getSpecialAttributes();
        if (!atts.ageing$getBatMode()) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    @Override
    public void onDeactivated(@NotNull IHunterPlayer hunter) {
        Player player = hunter.asEntity();
        setModifier(player, false);
        if (!player.onGround()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 100, false, false));
        }
        updatePlayer((HunterPlayer) hunter, false);
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer hunterP) {
        Player hunter = hunterP.asEntity();
        if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(hunter.getCommandSenderWorld().dimension().location().toString()) && hunter.getCommandSenderWorld().dimension() == Level.END) {
            hunter.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_dimension"));
            return true;
        } else if(shouldSunAffect(hunter) && !hunter.getCommandSenderWorld().isClientSide) {
            hunter.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_day"));
            return true;
        } else {
            float exhaustion = HunterAgeingConfig.limitedBatExhaustion.get().floatValue();
            if (exhaustion > 0) hunter.getFoodData().addExhaustion(exhaustion);
            return hunter.isInWater();
        }
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer hunter) {
        setModifier(hunter.asEntity(), true);
        IHunterSpecialAttributes atts = (IHunterSpecialAttributes) ((HunterPlayer) hunter).getSpecialAttributes();
        if (atts.ageing$getBatMode()) {
            updatePlayer((HunterPlayer) hunter, true);
        }
    }
    private void setModifier(Player player, boolean enabled) {
        if (enabled) {
            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(VampiricAgeing.rl("bat_armour_modifier")) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(VampiricAgeing.rl("bat_armour_modifier"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(VampiricAgeing.rl("bat_armour_toughness")) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(VampiricAgeing.rl("bat_armour_toughness"), -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(VampiricAgeing.rl("bat_armour_toughness"));
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(VampiricAgeing.rl("bat_armour_toughness"));
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
        IHunterSpecialAttributes atts = (IHunterSpecialAttributes) ((HunterPlayer) hunter).getSpecialAttributes();
        atts.ageing$setBatMode(bat);
        player.refreshDimensions();
        player.setPose(Pose.CROUCHING);
        player.setForcedPose(bat ? null : Pose.STANDING);
        if (bat) {
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height()), player.getZ());
        }
    }
    private void setFlightSpeed(@NotNull Player player, float speed) {
        player.getAbilities().setFlyingSpeed(speed);
    }
    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}
