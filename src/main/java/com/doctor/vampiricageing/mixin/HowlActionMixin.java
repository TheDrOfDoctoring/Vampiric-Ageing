package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.werewolves.entities.AggressiveWolfEntity;
import de.teamlapen.werewolves.entities.player.werewolf.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.player.werewolf.actions.HowlingAction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.UUID;

@Mixin(HowlingAction.class)
public class HowlActionMixin {

     @Unique
     private static final UUID SPEED_INCREASE_UUID = UUID.fromString("ee73a62e-8bac-4fe6-9e95-fe0bf16a1305");
    @Inject(method = "lambda$activate$2", at = @At(value = "INVOKE", target = "Lde/teamlapen/lib/lib/util/UtilLib;spawnEntityInWorld(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;ILjava/util/List;Lnet/minecraft/entity/SpawnReason;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void activate(PlayerEntity player, World world, AggressiveWolfEntity wolf, CallbackInfo ci) {
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        //this thing has caused an unreasonable amount of trouble for how simple it is
        if(WerewolvesAgeingConfig.ageBuffsHowl.get() && age > 0) {
            wolf.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(VampiricAgeingCapabilityManager.ATTACK_DAMAGE_UUID, "AGE_WOLF_DAMAGE_INCREASE", age, AttributeModifier.Operation.ADDITION));
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SPEED_INCREASE_UUID, "AGE_WOLF_SPEED_INCREASE", 0.045f * age, AttributeModifier.Operation.ADDITION));
        }
    }
}
