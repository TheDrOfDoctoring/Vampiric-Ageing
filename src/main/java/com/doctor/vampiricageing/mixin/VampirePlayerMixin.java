package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerMixin extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    public VampirePlayerMixin(PlayerEntity player) {
        super(player);
    }
    //used to check against fully drained entities for incrementing drained, since the no blood damage source doesnt give information on the drainer

    @Inject(method = "biteFeed", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false )
    private void biteFeed(LivingEntity entity, CallbackInfoReturnable<Boolean> cir, int blood) {
        if(cir.getReturnValue() && CommonConfig.drainBasedIncrease.get() && !player.getCommandSenderWorld().isClientSide) {
            if(VampiricAgeingCapabilityManager.canAge(player) && entity.getType().is(EntityTypeTagProvider.countsForDrained) && blood < 2) {
                VampiricAgeingCapabilityManager.incrementDrained((ServerPlayerEntity) player);
            }
        }

    }
}
