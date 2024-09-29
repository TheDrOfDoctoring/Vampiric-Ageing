package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.BitingMethod;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.InfectAction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InfectAction.class)
public class InfectActionMixin  {
    //Increment the infected count, cant use the stat because infected count has to be reset.
    @Inject(method = "activate(Lde/teamlapen/vampirism/api/entity/player/vampire/IVampirePlayer;Lde/teamlapen/vampirism/api/entity/player/actions/IAction$ActivationContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void activate(IVampirePlayer vampire, IAction.ActivationContext context, CallbackInfoReturnable<Boolean> cir, Player player, Entity creature) {
        if(!player.getCommandSenderWorld().isClientSide) {
            AgeingManager manager = AgeingManager.getAge(player);
            if(manager.canAge() && manager.getMethod() instanceof BitingMethod && !creature.getType().is(EntityTypeTagProvider.infectedBlacklist)) {
                manager.increaseRankPoints(1);
            }
            if(creature instanceof Player && CommonConfig.sireingMechanic.get()) {
                int age = AgeingManager.getAge(player).getAge();
                if (age > 1) {
                    creature.getPersistentData().remove("AGE");
                    creature.getPersistentData().putInt("AGE", age - 1);
                }
            }
        }
    }
}
