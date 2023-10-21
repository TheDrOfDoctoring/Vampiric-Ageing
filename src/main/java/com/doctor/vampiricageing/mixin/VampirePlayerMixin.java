package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import de.teamlapen.vampirism.entity.player.vampire.BloodStats;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerMixin extends FactionBasePlayer<IVampirePlayer> implements IVampirePlayer {


    @Shadow @Final private @NotNull BloodStats bloodStats;

    @Shadow protected abstract void handleSpareBlood(int amt);

    public VampirePlayerMixin(Player player) {
        super(player);
    }
    @Inject(method = "drinkBlood", at = @At("HEAD"),remap = false )
    private void drinkBlood(int amt, float saturationMod, boolean useRemaining, CallbackInfo ci) {
        if(CommonConfig.drainBasedIncrease.get() && !player.getCommandSenderWorld().isClientSide) {
            if(VampiricAgeingCapabilityManager.canAge(player)) {
                VampiricAgeingCapabilityManager.increaseDrainedBlood((ServerPlayer) player, amt);
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
