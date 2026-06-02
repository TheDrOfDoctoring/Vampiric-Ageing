package com.thedrofdoctoring.vampiricageing.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.thedrofdoctoring.vampiricageing.capabilities.cache.AgeingPlayerCache;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderHandler.class)
public class RenderHandlerMixin {

    @ModifyExpressionValue(
            method = "onRenderPlayerPreHigh",
            at = @At(value = "FIELD", target = "Lde/teamlapen/vampirism/entity/player/vampire/VampirePlayerSpecialAttributes;invisible:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean shouldHaveInvisibility(boolean isInvisible) {
        if(!isInvisible) return false;
        return (Minecraft.getInstance().player != null && !AgeingPlayerCache.get(Minecraft.getInstance().player).hasBypassInvisibility);
    }
}
