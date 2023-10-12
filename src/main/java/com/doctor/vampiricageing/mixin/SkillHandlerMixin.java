package com.doctor.vampiricageing.mixin;

import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkillHandler.class)
public abstract class SkillHandlerMixin <T extends IFactionPlayer<T>> implements ISkillHandler<T> {
    @Shadow
    @Final
    private T player;

    @Inject(method = "getLeftSkillPoints", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private void getLeftSkillPoints(CallbackInfoReturnable<Integer> cir) {
        int unlockedSkills = VampiricAgeingCapabilityManager.getAge(this.player.getRepresentingPlayer()).map(age -> age.getAgeSkills()).orElse(0);
        cir.setReturnValue(cir.getReturnValue() + unlockedSkills);
    }
}