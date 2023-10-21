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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerMixin extends VampirismPlayer<IVampirePlayer> implements IVampirePlayer {

    public VampirePlayerMixin(PlayerEntity player) {
        super(player);
    }
    //used to check against fully drained entities for incrementing drained, since the no blood damage source doesnt give information on the drainer

    @Inject(method = "drinkBlood", at = @At("HEAD"),remap = false )
    private void drinkBlood(int amt, float saturationMod, boolean useRemaining, CallbackInfo ci) {
        if(CommonConfig.drainBasedIncrease.get() && !player.getCommandSenderWorld().isClientSide) {
            if(VampiricAgeingCapabilityManager.canAge(player)) {
                VampiricAgeingCapabilityManager.increaseDrainedBlood((ServerPlayerEntity) player, amt);
            }
        }

    }
    @Inject(method = "getDbnoDuration", at = @At("RETURN"), remap = false, cancellable = true)
    private void getDbnoDuration(CallbackInfoReturnable<Integer> cir) {
        int age = VampiricAgeingCapabilityManager.getAge(this.getRepresentingPlayer()).map(vamp -> vamp.getAge()).orElse(0);
        int duration = Math.max(1, (int) (cir.getReturnValue() * CommonConfig.DBNOTimeMultiplier.get().get(age)));
        cir.setReturnValue(duration);
    }
}
