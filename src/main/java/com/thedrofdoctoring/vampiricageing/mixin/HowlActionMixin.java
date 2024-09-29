package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import de.teamlapen.werewolves.api.entities.player.IWerewolfPlayer;
import de.teamlapen.werewolves.entities.AggressiveWolfEntity;
import de.teamlapen.werewolves.entities.player.werewolf.actions.HowlingAction;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HowlingAction.class)
public class HowlActionMixin {

    @Inject(method = "spawnWolves", at = @At(value = "INVOKE", target = "Lde/teamlapen/lib/lib/util/UtilLib;spawnEntityInWorld(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/entity/Entity;ILjava/util/List;Lnet/minecraft/world/entity/MobSpawnType;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private void activate(IWerewolfPlayer werewolf, CallbackInfo ci, Player player, Level world, int wolfAmount, int i, AggressiveWolfEntity wolf) {
        int age = AgeingManager.getAge(player).getAge();
        //this thing has caused an unreasonable amount of trouble for how simple it is
        if(WerewolvesAgeingConfig.ageBuffsHowl.get() && age > 0) {
            wolf.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(VampiricAgeing.rl("wolf_attack_damage_increase"), age, AttributeModifier.Operation.ADD_VALUE));
            wolf.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(VampiricAgeing.rl("wolf_speed_increase"), 0.045f * age, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
