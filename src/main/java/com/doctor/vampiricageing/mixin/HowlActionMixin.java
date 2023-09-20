package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.AggressiveWolfEntity;
import de.teamlapen.werewolves.entities.player.werewolf.actions.HowlingAction;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.UUID;

@Mixin(HowlingAction.class)
public class HowlActionMixin {

     @Unique
     final UUID SPEED_INCREASE_UUID = UUID.fromString("ee73a62e-8bac-4fe6-9e95-fe0bf16a1305");
    @Inject(method = "activate(Lde/teamlapen/werewolves/api/entities/player/IWerewolfPlayer;Lde/teamlapen/vampirism/api/entity/player/actions/IAction$ActivationContext;)Z", at = @At(value = "INVOKE", target = "Lde/teamlapen/lib/lib/util/UtilLib;spawnEntityInWorld(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/entity/Entity;ILjava/util/List;Lnet/minecraft/world/entity/MobSpawnType;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private void activate(IWerewolfPlayer werewolfPlayer, IAction.ActivationContext context, CallbackInfoReturnable<Boolean> cir, Player player, AABB bb, List entities, Level world, int wolfAmount, int i, AggressiveWolfEntity wolf) {
        int age = VampiricAgeingCapabilityManager.getAge(player).map(ageCap -> ageCap.getAge()).orElse(0);
        //this thing has caused an unreasonable amount of trouble for how simple it is
        if(WerewolvesAgeingConfig.ageBuffsHowl.get() && age > 0) {
            wolf.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(VampiricAgeingCapabilityManager.ATTACK_DAMAGE_UUID, "AGE_WOLF_DAMAGE_INCREASE", age, AttributeModifier.Operation.ADDITION));
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(SPEED_INCREASE_UUID, "AGE_WOLF_SPEED_INCREASE", 0.045f * age, AttributeModifier.Operation.ADDITION));
        }
    }
}
