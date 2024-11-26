package com.thedrofdoctoring.vampiricageing.mixin;

import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.IAgeType;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.player.FactionBasePlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VampirePlayer.class)
public abstract class VampirePlayerMixin extends FactionBasePlayer<IVampirePlayer> implements IVampirePlayer {

    public VampirePlayerMixin(Player player) {
        super(player);
    }
    @Inject(method = "tryResurrect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void addEffect(CallbackInfo ci) {
        if(CommonConfig.ageLossDBNO.get() > 0 && player instanceof ServerPlayer sp) {
            AgeingManager manager = AgeingManager.getAge(player);
            IAgeType old = manager.getAgeType();
            manager.setAge(Math.max(0, manager.getAge() - CommonConfig.ageLossDBNO.get()));
            manager.sync(false);
            manager.onAgeChange(sp, manager.getAgeType());
        }
    }
}
