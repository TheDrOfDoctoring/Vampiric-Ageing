package com.thedrofdoctoring.vampiricageing.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.thedrofdoctoring.vampiricageing.capabilities.CapabilityHelper;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CoffinBlock.class)
public class CoffinBlockMixin {

    @ModifyExpressionValue(
            method = "useWithoutItem",
            at = @At(value = "FIELD", target = "Lde/teamlapen/vampirism/entity/player/VampirismPlayerAttributes;vampireLevel:I", opcode = Opcodes.GETFIELD)
    )
    private int getModifiedLevel(int original, @Local(argsOnly = true) Player player) {
        int taintedAge = CapabilityHelper.getCumulativeTaintedAge(player);
        if(taintedAge >= HunterAgeingConfig.taintedAgeForCoffinUse.get()) {
            return 1;
        }
        return original;
    }

}
